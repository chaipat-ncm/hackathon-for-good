import time
import glob
import json
import matplotlib
import numpy as np
matplotlib.use('TkAgg')
import pandas as pd
from matplotlib import pyplot as plt

#Amount of training 9759
#{0: 1522, 1: 8230, 2: 7}

def data_explore(data):

    building_types_dict = [] # type of building, kinda useless
    damages_dict = {} #type of damages
    geom_types_dict = {} #type of geoms
    for index, line in enumerate(data):
        data = line['properties'] #keys: osm_way_id, building, objectID, name, _damage

        geom = line['geometry']
        geom_type = geom['type']

        if geom_type not in geom_types_dict:
            geom_types_dict[geom_type] = 1
        else:
            num_types = geom_types_dict[geom_type]
            num_types += 1
            geom_types_dict[geom_type] = num_types

        if data['building'] not in building_types_dict:
            building_types_dict.append(data['building'])

        damage_type = data['_damage']
        if damage_type not in damages_dict:
            damages_dict[damage_type] = 1
        else:
            num = damages_dict[damage_type]
            num += 1
            damages_dict[damage_type] = num
    return damages_dict, building_types_dict, geom_types_dict

# def create_dataframe(data):
#     df = pd.DataFrame({'id': [], 'damage': []})
#     for index, line in enumerate(data):
#         data = line['properties'] #keys: osm_way_id, building, objectID, name, _damage
#         df = df.append({'id': data['osm_way_id'], 'damage': int(unique_damages.index(data['_damage']))}, ignore_index=True)
#     return df


data_dir = '/home/stephan/Documents/Data_01/RedCross'
training_gson_name = '/TrainingDataset.geojson.txt'

with open(data_dir + training_gson_name) as data_file:
    geoms = json.load(data_file)
    data = geoms['features'] #other keys not needed
    print("Amount of training", len(data))

    # data = data.pop(0) #remove cause usless

    print(data_explore(data)[2]) #prints the unique damage values :: ({'destroyed': 836, 'unknown': 301, 'none': 4904, 'significant': 1596, 'partial': 2122}

    # unique_damages = ['destroyed', 'unknown', 'none', 'significant', 'partial']
    # data_df = create_dataframe(data)
    # print(data_df.head(100))
    #
    # data_df.to_csv(data_dir + '/damages.csv')
