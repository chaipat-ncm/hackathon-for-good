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

import json
import sys
import shapely

# Local
from utils import *
from utils_raster import *
from utils_pca import *

## MAIN ##
if __name__ == "__main__":

	# Read configuration into dictionary
	if len(sys.argv) != 2:
		print ('USAGE: python redCross_damageEvaluator.py <configuration.json>')
		exit(-1)

	# Read configuration file	
	with open(sys.argv[1]) as handle:
		conf = json.loads(handle.read())

	# Output dir create if necessary
	tn = os.path.basename(conf['buildingsInfo']).split('.')[0]
	makeDirs(conf['outputDir'], ['{}_damage_previews'.format(tn)])
	beforeDir = os.path.join(conf['outputDir'], '{}_before_clipped'.format(tn))
	afterDir = os.path.join(conf['outputDir'], '{}_after_clipped'.format(tn))
	damageDir = os.path.join(conf['outputDir'], '{}_damage_previews'.format(tn))

	# For every building/feature in the GeoJSON file
	gdf = readGeoJSON(conf['buildingsInfo'])
	outgdf = gdf.copy()
	n = float(gdf.shape[0])
	c = 0.0
	lastperc = 0

	# Add indicator columns
	outgdf['_idx_otb_diff'] = -1.0
	outgdf['_idx_otb_edges'] = -1.0
	outgdf['_idx_opencv_pca'] = -1.0
	outgdf['_damage_otb_diff'] = 'unknown'
	outgdf['_daamge_otb_edges'] = 'unknown'
	outgdf['_damage_opencv_pca'] = 'unknown'

	for index,row in gdf.iterrows():
		# Get identifier and bounding box
		iden=row['OBJECTID']
		#if iden != 2661: continue
		# Get image cut before
		fnameBefore = os.path.join(beforeDir, '{}_before_v0.tif'.format(iden))
		if not os.path.exists(fnameBefore):
			continue # Skip missing data [could be done better]

		# Get best image cut possible [after crisis]
		fnameAfter = getBestImage(afterDir, iden)
		
		# Resample to the same geometry [width/height]
		fnameResam = resampleImages(fnameBefore, fnameAfter, iden, damageDir)	
		
		# Compare images
		idx_otb_diff = compareImages_method_OrfeoToolbox(fnameResam, fnameBefore, iden, damageDir, conf['thresholdDestruct'])
		print ('-------------')
		print ('IDX_OTB_DIFF: {}'.format(idx_otb_diff))
		print ('-------------')

		# Get Edges comparison
		edgesAfter = getEdges(fnameResam)
		edgesBefore = getEdges(fnameBefore)
		idx_otb_edges = compareImages_method_Edges(edgesBefore, edgesAfter, iden, damageDir)
		print ('-------------')
		print ('IDX_OTB_EDGES: {}'.format(idx_otb_edges))
		print ('-------------')

		# Apply PCA (Principal Componen Analysis)
		pcaBefore, idxpcaBefore = getPCA(fnameBefore)
		pcaAfter, idxpcaAfter = getPCA(fnameAfter)
		idx_opencv_pca = idxpcaBefore - idxpcaAfter / float(conf['maxPCA'])
		print ('-------------')
		print ('IDX_OCV2_PCA: {}'.format(idx_opencv_pca))
		print ('-------------')

		# Add to Geodataframe column
		# TODO: Damage will be computed feeding several indexes [0..1] to a trained CNN
		outgdf.loc[index, '_damage'] = getDamage(idx_otb_diff, conf['thresholdsSegmentsDIFF'])
		outgdf.loc[index, '_damage_otb'] = getDamage(idx_otb_diff, conf['thresholdsSegmentsDIFF'])
		outgdf.loc[index, '_damage_pca'] = getDamage(idx_otb_diff, conf['thresholdsSegmentsPCA'])
		outgdf.loc[index, '_idx_otb_diff'] = idx_otb_diff
		outgdf.loc[index, '_idx_otb_edges'] = idx_otb_edges
		outgdf.loc[index, '_idx_opencv_pca'] = idx_opencv_pca 

		# Percent update
		perc = int((c/n)*100)		
		c+=1
		if perc % 10 == 0 and perc != lastperc:	
			print ('Processing {}% ...'.format(perc))
			lastperc = perc	

	# Write GeoDataframe / GeoJSON
	outfname = os.path.join(conf['outputDir'], os.path.basename(conf['buildingsInfo']))
	if os.path.exists(outfname): os.remove(outfname)
	outgdf.to_file(outfname, driver='GeoJSON')