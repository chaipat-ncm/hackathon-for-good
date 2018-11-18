# -*- coding: utf-8 -*-
import struct
import numpy as np
from tqdm import tqdm
import ndjson
import tensorflow as tf
from PIL import Image
import pandas as pd
import time

import matplotlib
matplotlib.use('TkAgg')
from matplotlib import pyplot as plt

class ImgLoader(object):
    """ This class downloads the images and converts them to Tensorflow records"""

    def __init__(self, output_file, key_file, input_after_img_dir, input_before_img_dir):
        self.output_file = output_file
        self.df = pd.read_csv(key_file)
        self.input_bef_dir = input_before_img_dir
        self.input_after_dir = input_after_img_dir

    def _int64_feature(self, value):
        """ Convert an int to a tfrecord train feature"""
        return tf.train.Feature(int64_list=tf.train.Int64List(value=[value]))

    def _bytes_feature(self, value):
        """ Convert a byte to a tfrecord train feature"""
        return tf.train.Feature(bytes_list=tf.train.BytesList(value=[value]))

    def create_image_records(self):
        """Loop through the image files and write to tfrecords"""
        writer = tf.python_io.TFRecordWriter(self.output_file + '/train_record')
        sum = 0
        for index, row in self.df.sample(frac=1).iterrows():
            id = (int)(row['id'])
            label = (int)(row['damage'])


            try:
                im_bef = Image.open(self.input_bef_dir + '/' + str(id) + '.tif')
                im_after = Image.open(self.input_after_dir + '/' + str(id) + '.tif')
                img_bef = np.array(im_bef)
                img_after = np.array(im_after)

                new_img = img_after
                # print(new_img)

                nonz = np.count_nonzero(img_after)
                tot = img_after.size
                rat = nonz / tot
                # print("NONZERO :", nonz, type(nonz))
                # print("TOTAL :", tot, type(tot))
                if rat > 0.1 and tot > 100000:

                    img_raw = new_img.tostring()
                    sum += 1


                    feature = {
                                'width': self._int64_feature(new_img.shape[0]),
                                'height': self._int64_feature(new_img.shape[1]),
                                'class': self._int64_feature(label),
                                'key': self._int64_feature(id),
                                'image_raw': self._bytes_feature(img_raw)
                            }

                    example = tf.train.Example(features=tf.train.Features(feature = feature))
                    writer.write(example.SerializeToString())

                    if sum == 5200:
                        writer.close()
                        writer = tf.python_io.TFRecordWriter(self.output_file + '/dev_record')

            except:
                continue

        writer.close()
        print(sum)

    def rec_img(self, record_file):
        """Reconstruct the image and show the image"""

        record_iterator = tf.python_io.tf_record_iterator(path=record_file)
        labels = {}
        sum = 0
        unique_damages = ['destroyed', 'unknown', 'none', 'significant', 'partial']

        for string_record in record_iterator:

            sum +=1

            example = tf.train.Example()
            example.ParseFromString(string_record)

            img_string = (example.features.feature['image_raw']
                .bytes_list
                .value[0])

            label = (example.features.feature['class'].int64_list.value[0])

            dmg = unique_damages[label]
            if dmg not in labels:
                labels[dmg] = 1
            else:
                num = labels[dmg]
                num += 1
                labels[dmg] = num

            width = (example.features.feature['width'].int64_list.value[0])
            height = (example.features.feature['height'].int64_list.value[0])

        print(labels)
            # img_1d = np.fromstring(img_string, dtype=np.uint8)
            # key = key_string.decode()
            # reconstructed_img = img_1d.reshape((width, height, 3))
            # plt.imshow(reconstructed_img)
            # plt.show()
            # img = Image.fromarray(reconstructed_img, 'L')
            # img.show()
            # print(label)

        # print(sum)
        # time.sleep(10)


