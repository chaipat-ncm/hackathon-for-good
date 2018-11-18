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

## MAIN ##
if __name__ == "__main__":

	# Read configuration into dictionary
	if len(sys.argv) != 2:
		print ('USAGE: python redCross_smartClipper.py <configuration.json>')
		exit(-1)

	# Read configuration file	
	with open(sys.argv[1]) as handle:
		conf = json.loads(handle.read())

	# Log files
	#overlapLog = open(os.path.join(conf['outputDir'], 'overlap.txt'), 'w')
	#notFoundLog = open(os.path.join(conf['outputDir'], 'notfound.txt'), 'w')

	# Output dir create if necessary
	tn = os.path.basename(conf['buildingsInfo']).split('.')[0]
	makeDirs(conf['outputDir'], ['{}_after_clipped'.format(tn), '{}_before_clipped'.format(tn)])

	# Create dictionary structure of images before	
	mapAfter = readImagesAfter(conf['imgAfterDir'])

	# For every building/feature in the GeoJSON file
	gdf = readGeoJSON(conf['buildingsInfo'])
	#gdf.to_crs(epsg=conf['crs']) # reproject to data CRS
	n = float(gdf.shape[0])
	c = 0.0
	lastperc = 0

	for index,row in gdf.iterrows():
		# Get identifier and bounding box
		iden=row['OBJECTID']
		geometry=row['geometry']

		# Test [shift geometry]
		geomshift=shapely.affinity.translate(geometry, xoff=conf['shiftAfter'][0], yoff=conf['shiftAfter'][1], zoff=0.0)

		# Clip images after		
		selImgs = findBestCoverage(mapAfter, geomshift.bounds)
		outAfter = os.path.join(conf['outputDir'], '{}_after_clipped/{}_after_v0.tif'.format(tn, iden))

		# Zero images found
		if len(selImgs) == 0:
			#print ('{}'.format(iden), file=notFoundLog)
			continue # No need to proceed and cut the image before
		# One image found
		elif len(selImgs) == 1:
			clipImage(os.path.join(conf['imgAfterDir'], selImgs[0]), geomshift, outAfter)
		# Overlapping between N images
		else:
			i=0		
			#print ('{}'.format(json.dumps({'id':iden, 'selImgs':selImgs})), file=overlapLog)
			for sel in selImgs:				
				outAfter = os.path.join(conf['outputDir'], '{}_after_clipped/{}_after_v{}.tif'.format(tn, iden, i))
				clipImage(os.path.join(conf['imgAfterDir'], sel), geomshift, outAfter)
				i+=1

		# Clip images before
		outBefore = os.path.join(conf['outputDir'], '{}_before_clipped/{}_before_v0.tif'.format(tn, iden))
		clipImage(conf['imgBefore'], geometry, outBefore)
		
		# Percent update
		perc = int((c/n)*100)		
		c+=1
		if perc % 10 == 0 and perc != lastperc:	
			print ('Processing {}% ...'.format(perc))
			lastperc = perc	