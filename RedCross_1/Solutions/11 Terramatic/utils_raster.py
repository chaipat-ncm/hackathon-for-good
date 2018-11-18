# -*- coding: utf-8 -*-
# Copyright notice
#   --------------------------------------------------------------------
#   Copyright (C) 2018
#       Joan Sala
#		Carlos Villanueva
#       
#
#   This library is free software: you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation, either version 3 of the License, or
#   (at your option) any later version.
#
#   This library is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.
#
#   You should have received a copy of the GNU General Public License
#   along with this library.  If not, see <http://www.gnu.org/licenses/>.
#   --------------------------------------------------------------------
# $Keywords: $

import cv2
from osgeo import gdal
import os
import geopandas as gpd
import rasterio
from rasterio.mask import mask
import numpy as np
import glob

# Get the image size in pixels
def getImgSize(fname):
    src_ds = gdal.Open(fname)
    md = src_ds.GetRasterBand(1).ReadAsArray()
    return np.asarray(md).shape

# Number of non-zero pixels for the three bands RGB relative to the image size
def getImgStats(fname):
    src_ds = gdal.Open(fname)
    N = src_ds.GetRasterBand(1).ReadAsArray().size * 3
    nR = np.count_nonzero(src_ds.GetRasterBand(1).ReadAsArray())
    nG = np.count_nonzero(src_ds.GetRasterBand(2).ReadAsArray())
    nB = np.count_nonzero(src_ds.GetRasterBand(3).ReadAsArray())
    return (nR+nG+nB)/float(N)

# Get Best image estimate
def getBestImage(afterDir, iden):
	candidates = glob.glob(os.path.join(afterDir, '{}_after_v*.tif'.format(iden)))
	better = 0.0
	
	if len(candidates):
		selImg = candidates[0]
		for c in candidates:
			val = getImgStats(c)
			if val > better:
				selImg = c
				better = val
		return selImg
	else:
		return []

# Copy header from one tiff to another
def copyGtiffHeader(inp, outp):
	dataset = gdal.Open(inp)
	projection = dataset.GetProjection()
	geotransform = dataset.GetGeoTransform()
	dataset2 = gdal.Open(outp, gdal.GA_Update)
	dataset2.SetGeoTransform(geotransform)
	dataset2.SetProjection(projection)

# Resample and resize to smaller resolution [e.g drone imagery resampling to orthophoto/bing]
def resampleImages(fnameBefore, fnameAfter, iden, outDir):
	fnameOut = os.path.join(outDir, '{}_resampled.tif'.format(iden))
	h,w = getImgSize(fnameBefore)
	cmd = '''gdal_translate -of GTiff -outsize {xsize} {ysize} {inp} {outp}'''.format(
		inp=fnameAfter, outp=fnameOut, xsize=w, ysize=h)
	print (cmd)
	os.system(cmd)	

	# Copy geometry 
	copyGtiffHeader(fnameBefore, fnameOut)
	return fnameOut 

# RGB to grayscale
def RGBtoGrayScale(fnameIn):
	fnameOut = fnameIn.replace('.tif', '_BW.png')
	image = cv2.imread(fnameIn)
	gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
	cv2.imwrite(fnameOut,gray_image)
	return fnameOut

# Get statistics of tiff
def getPercPixels(fname, minV, maxV):
	src_ds = gdal.Open(fname)
	N = src_ds.GetRasterBand(1).ReadAsArray().size * 3 # RGB
	R = src_ds.GetRasterBand(1).ReadAsArray()
	G = src_ds.GetRasterBand(2).ReadAsArray()
	B = src_ds.GetRasterBand(3).ReadAsArray()
	RGBc = (((R > maxV) | (R < minV)).sum() + ((G > maxV) | (G < minV)).sum() + ((B > maxV) | (B < minV)).sum())
	return float(RGBc)/float(N)

# Method based on: 
def compareImages_method_OrfeoToolbox(fname1, fname2, iden, outDir, threshold):
	minV = threshold*-1.0
	maxV = threshold
	fnameOut = os.path.join(outDir, '{}_otb.tif'.format(iden))
	if os.path.exists(fnameOut):	os.remove(fnameOut)
	cmd = 'otbcli_MultivariateAlterationDetector -in1 {} -in2 {} -out {}'.format(fname1, fname2, fnameOut)
	print (cmd)
	os.system(cmd)
	# Gather stats [MAD returns values between -1/1, change detection]
	return getPercPixels(fnameOut, minV, maxV)

# Get damage
def getDamage(idx_otb, threshold_values):	
	if idx_otb > threshold_values['destroyed']:	
		return 'destroyed'
	elif idx_otb <= threshold_values['destroyed'] and idx_otb > threshold_values['significant']:
		return 'significant'
	elif idx_otb <= threshold_values['significant'] and idx_otb > threshold_values['partial']:
		return 'partial'
	elif idx_otb <= threshold_values['partial'] and idx_otb > threshold_values['none']:
		return 'none'
	else:
		return 'unknown'

# Get Edges
def getEdges(fnameIn):
	fnameOut = fnameIn.replace('.tif', '_edges.tif')
	if os.path.exists(fnameOut):	os.remove(fnameOut)
	cmd = 'otbcli_EdgeExtraction -in {} -out {}'.format(fnameIn, fnameOut)
	print (cmd)
	os.system(cmd)
	return fnameOut

def compareImages_method_Edges(edgesBefore, edgesAfter, iden, damageDir):
	fnameOut = os.path.join(damageDir, '{}_edges_comparison.tif')
	if os.path.exists(fnameOut):	os.remove(fnameOut)
	

	return 0.5

# PCA of image [outlines of shapes]