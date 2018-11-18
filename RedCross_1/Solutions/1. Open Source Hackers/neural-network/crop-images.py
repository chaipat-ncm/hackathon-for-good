from rasterio.plot import show
from rasterio.plot import show_hist
from rasterio.mask import mask
from shapely.geometry import box
from fiona.crs import from_epsg
import pycrs
import geopandas as gpd
import json
import rasterio as rasterio

buildings = gpd.read_file('../input/geojson/TrainingDataset.geojson')
buildings_json = json.loads(buildings.to_json())

def getFeatures(gdf_json, i):
    """Function to parse features from GeoDataFrame in such a manner that rasterio wants them"""
    return [gdf_json['features'][i]['geometry']]

def bounding_box(points):
    x_coordinates, y_coordinates = zip(*points)
    return [(min(x_coordinates), min(y_coordinates)), (max(x_coordinates), max(y_coordinates))]

import os
path = '../input/images/'
files = os.listdir(path)

for file in files:
    filepath = '../input/images/' + file
    print(filepath)
    count = 0
    for x in range(len(buildings_json['features'])):
        building_coords = getFeatures(buildings_json, x)
        damage = buildings_json['features'][x]['properties']['_damage']
        objectid = buildings_json['features'][x]['properties']['OBJECTID']
        try:
            box_coord = bounding_box(building_coords[0]['coordinates'][0][0])
        except TypeError: 
            continue
        sat = rasterio.open(filepath)

        bbox = box(box_coord[0][0], box_coord[0][1], box_coord[1][0], box_coord[1][1])
        geo = gpd.GeoDataFrame({'geometry': bbox}, index=[0], crs=sat.crs.data)

        coords = getFeatures(json.loads(geo.to_json()), 0)
        try:
            out_img, out_transform = mask(sat, shapes=coords, crop=True)
        except:
            continue
        out_meta = sat.meta.copy()
        out_meta.update({"driver": "GTiff",
                        "height": out_img.shape[1],
                         "width": out_img.shape[2],
                         "transform": out_transform})
        out_tif =  str(objectid) + "_" + file + "_" + str(x) + "_" + damage +  ".tif"
        print("Saving " + out_tif)
        count += 1
        with rasterio.open(out_tif, "w", **out_meta) as dest:
          dest.write(out_img)
    print('done ' + str(count))