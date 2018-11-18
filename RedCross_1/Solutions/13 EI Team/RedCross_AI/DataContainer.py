import tensorflow as tf
from random import randint
import math
import tensorlayer as tl
import time
import numpy as np
import ImgLoader
import glob
from PIL import Image


from matplotlib import pyplot as plt

class DataContainer(object):

    def __init__(self, train_input_file, dev_input_file):
        """Initiliase the data container with the feature names and amount of samples"""
        self.record_image = 'image_raw'
        self.record_label = 'class'
        self.record_key = 'key'
        self.record_width = 'width'
        self.record_height = 'height'
        self.const_img_dim = 500
        self.train_input_file = [train_input_file]
        self.dev_input_file = [dev_input_file]
        self.min_after_dequeue = 160
        self.threads = 32
        self.batch_size = 32
        self.init_batches()

    def init_batches(self):
        """Init the variables holding the queue with batchees"""
        # self.dev_files = self.append_in_folder(input_dir + 'dev/*')
        # self.test_files = self.append_in_folder(input_dir + 'test/*')
        # print("De files", self.train_files)

        #Store all the data in the queue calling the variable will dequeueu once
        self.key_train_batch, self.x_train_batch, self.y_train_batch = self.parse_tf_rec(self.train_input_file, True)
        self.key_dev_batch, self.x_dev_batch, self.y_dev_batch = self.parse_tf_rec(self.dev_input_file, True)
        # self.x_test_batch, self.y_test_batch = self.parse_tf_rec(self.test_files, True)

    def parse_tf_rec(self, data, is_train = None):
        """Parse the tfreocrds and store them into a queue with batches"""

        capacity = self.min_after_dequeue + (self.threads + 1) * self.batch_size
        filename_queue = tf.train.string_input_producer(data, shuffle=True) #Shuffled queue with filenames

        reader = tf.TFRecordReader()
        _, serialized_example = reader.read(filename_queue)

        if is_train:
            features = tf.parse_single_example(
                serialized_example, features={
                    self.record_key: tf.FixedLenFeature([], tf.int64),
                    self.record_label : tf.FixedLenFeature([], tf.int64),
                    self.record_width : tf.FixedLenFeature([], tf.int64),
                    self.record_height : tf.FixedLenFeature([], tf.int64),
                    self.record_image : tf.FixedLenFeature([], tf.string),
                })
        else:
            features = tf.parse_single_example(
                serialized_example, features={
                    self.record_key : tf.FixedLenFeature([], tf.string),
                    self.record_image : tf.FixedLenFeature([], tf.string),
                    self.record_width : tf.FixedLenFeature([], tf.int64),
                    self.record_height : tf.FixedLenFeature([], tf.int64),
                })

        key = tf.cast(features[self.record_key], tf.int64)
        width = tf.cast(features[self.record_width], tf.int32)
        height = tf.cast(features[self.record_height], tf.int32)

        img = tf.decode_raw(features[self.record_image], tf.uint8)
        img_shape = tf.stack([width, height, 3])
        img = tf.reshape(img, img_shape)
        img = tf.image.per_image_standardization(img)
        # image_size_const = tf.constant((self.const_img_dim, self.const_img_dim, 3), dtype=tf.int32)

        # c_img = tf.random_crop(img, [50, 50, 3])

        resized_image = tf.image.resize_image_with_crop_or_pad(image=img,
                                                               target_height=500,
                                                               target_width=500)

        # resized_image = tf.image.resize_images(img, [750, 750])
        flip_img = tf.image.random_flip_left_right(resized_image)
        # bright_img = tf.image.random_brightness(flip_img, max_delta=63)
        degree_angle = randint(-45, 45) # In degrees
        radian = degree_angle * math.pi / 180
        tf_img = tf.contrib.image.rotate(flip_img, radian)

        # c_img = tf.random_crop(tf_img, [500, 500, 3])
        # img = tf.to_float(img)
        # img = tf.nn.l2_normalize(img)
        # img = tf.contrib.layers.instance_norm(img, epsilon=1e-06)
        # img = tf.image.per_image_standardization(img)


        if(is_train):
            label = tf.cast(features[self.record_label], tf.int64)
            return tf.train.shuffle_batch([key, tf_img, label], batch_size=self.batch_size, capacity=capacity, num_threads=self.threads, min_after_dequeue=self.min_after_dequeue)
        else:
            return tf.train.shuffle_batch([key, img], batch_size=self.batch_size, capacity=capacity, num_threads=self.threads, min_after_dequeue=self.min_after_dequeue)

    def visualise_data(self, train_set = True):
        with tf.Session() as sess:
            sess.run(tf.global_variables_initializer())
            coord = tf.train.Coordinator()
            threads = tf.train.start_queue_runners(sess=sess, coord=coord)

            for i in range(20):
                print("Step %d" % i)
                if(train_set):
                    val, l = sess.run([self.x_train_batch, self.y_train_batch])
                    # print(self.C.class_list[l[0]])
                    # print(val.shape)
                    print('label', l)
                    plt.imshow(val[0])
                    plt.show()
                # else:
                #     key, val = sess.run([self.key_test_batch, self.x_test_batch])
                # self.show_image(val[i])

            coord.request_stop()
            coord.join(threads)
            sess.close()