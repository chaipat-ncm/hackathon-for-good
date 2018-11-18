# Getting thumbnail image of satellite image
# Tutorial: https://geohackweek.github.io/raster/04-workingwithrasters/
import rasterio
import rasterio.plot
import pyproj
import numpy as np
import matplotlib
import matplotlib.pyplot as plt

filepath = '../input/afterimg2/RescUAV_12917_Philipsburg.tif'
with rasterio.open(filepath) as src:
    print(src.profile)

# The grid of raster values can be accessed as a numpy array and plotted:
with rasterio.open(filepath) as src:
   oviews = src.overviews(1) # list of overviews from biggest to smallest
   oview = oviews[-1] # let's look at the smallest thumbnail
   print('Decimation factor= {}'.format(oview))
   # NOTE this is using a 'decimated read' (http://rasterio.readthedocs.io/en/latest/topics/resampling.html)
   thumbnail = src.read(1, out_shape=(1, int(src.height // oview), int(src.width // oview)))

print('array type: ',type(thumbnail))
print(thumbnail)

plt.imshow(thumbnail)
plt.colorbar()
plt.title('Overview - Band 4 {}'.format(thumbnail.shape))
plt.xlabel('Column #')
plt.ylabel('Row #')

# Showing part of image
with rasterio.open(filepath) as src:
    w = src.read(1, window=((src.height/2, src.width/2), (src.height/2, src.width/2 + 200)))
plt.imshow(w)

# Displaying building geojson
# Tutorial:

import geopandas as gpd
buildings = gpd.read_file('../input/building/AllBuildingOutline.geojson')
print(buildings.head())

import geoplot
geoplot.polyplot(buildings, figsize=(20, 15))