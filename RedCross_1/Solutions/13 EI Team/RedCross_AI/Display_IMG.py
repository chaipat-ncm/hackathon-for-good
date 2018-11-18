import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import glob
import time
import json
import rasterio
import tqdm
from rasterio.mask import mask

data_dir = '/home/stephan/Documents/Data_01/RedCross'
training_gson_name = '/TrainingDataset.geojson.txt'
after_dir = data_dir + '/after'
before_dir = data_dir + '/before'
before_houses_dir = data_dir + '/before_houses'
after_houses_dir = data_dir + '/after_houses'

use_after_images = True
if use_after_images:
    file_list = glob.glob(after_dir + '/*.tif')
else:
    file_list = glob.glob(before_dir + '/*.tif')
    test_image =  file_list[0]

amount_images_per_key = {}

how_many_lists_of_coords = {}

def write_image(image, geom_data, key_id):
    with rasterio.open(image) as src:
            try:
                out_image, out_transform = mask(src, [geom_data], crop=True)
                out_meta = src.meta.copy()

                # Shows the image
                # plt.imshow(out_image.transpose(1, 2, 0))
                # plt.show()

                if use_after_images:
                    if key_id not in amount_images_per_key:
                        amount_images_per_key[key_id] = 1
                    else:
                        num = amount_images_per_key[key_id]
                        num += 1
                        amount_images_per_key[key_id] = num

                out_meta.update({"driver": "GTiff",
                                 "height": out_image.shape[1],
                                 "width": out_image.shape[2],
                                 "transform": out_transform})

                with rasterio.open(after_houses_dir + "/" + key_id + ".tif", "w", **out_meta) as dest:
                    dest.write(out_image)
                return True

            except ValueError:
                return False


with open(data_dir + training_gson_name) as data_file:

    geoms = json.load(data_file)
    data = geoms['features'] #other keys not needed
    print("Amount of training", len(data))
    for index, line in enumerate(data):
        print(index)
        key_id = line['properties']['osm_way_id']
        geom_data = line['geometry']

        list_of_coords = geom_data['coordinates']

        # only take lists where length is 1
        if len(list_of_coords) != 1:
            continue

        coordinates = geom_data['coordinates'][0] # list way to nested
        geom_data['coordinates'] = coordinates

        # change geometry data
        new_coords = []
        for coord in geom_data['coordinates'][0]:
            new_coords.append(tuple(coord))

        geom_data['coordinates'] = [new_coords]
        geom_data['type'] = 'Polygon'
        if use_after_images:
            for image in file_list:
                write_image(image, geom_data, key_id)
        else:
            write_image(test_image, geom_data, key_id)

print("Amount images per key", amount_images_per_key)