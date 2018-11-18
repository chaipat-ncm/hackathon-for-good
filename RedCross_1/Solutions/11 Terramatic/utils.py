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

import os
import geopandas as gpd
import rasterio
from rasterio.mask import mask
import numpy as np
import glob

# Make directories
def makeDirs(path, subdirs):
	# Root dir
	if not os.path.exists(path):
		os.mkdir(path)
		print ('Creating dir {}'.format(path))

	# Subdirs
	for sb in subdirs:
		if not os.path.exists(os.path.join(path, sb)): 
			os.mkdir(os.path.join(path, sb))
			print ('Creating dir {}'.format(os.path.join(path, sb)))

# Read a GeoJSON into geodataframe
def readGeoJSON(path):
	return gpd.read_file(path)

# Create map structure of images after [img -> coverage]
def readImagesAfter(imgDir):
	res = dict()
	for f in os.listdir(imgDir):
		if f.endswith('.tif'):
			data = rasterio.open(os.path.join(imgDir, f))
			res[f] = data.bounds
	return res

# Find best coverage from available images
def findBestCoverage(mapAfter, geometry):
	res = [] # overlap?
	if len(geometry) == 4:
		xmin, ymin, xmax, ymax = geometry
		for k,bbox in mapAfter.items():			
			# Inside image	
			if (bbox.left <= xmin) and (bbox.right >= xmax) and (bbox.bottom <= ymin) and (bbox.top >= ymax):
				res.append(k)
	return res # building not found

# Clip raster by bounds[xmin, ymin, xmax, ymax]
def clipImage(source_tif, shape, out_tif):
	# Read source
	data = rasterio.open(source_tif)
	out_meta = data.meta.copy()
	
	# Clip
	out_img, out_transform = mask(data, shape, crop=True)
	out_meta.update({
		"driver": "GTiff",
		"height": out_img.shape[1],
		"width": out_img.shape[2],
		"transform": out_transform		
		}
	)	

	# Write output	
	with rasterio.open(out_tif, "w", **out_meta) as dest:
		dest.write(out_img)	

