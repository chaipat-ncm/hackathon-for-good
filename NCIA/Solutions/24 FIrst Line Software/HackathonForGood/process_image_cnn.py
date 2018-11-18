#!/usr/bin/env python3
import tensorflow as tf
from tensorflow.python.keras.preprocessing.image import ImageDataGenerator
from tensorflow.python.keras.models import model_from_json
from tensorflow.python.keras.optimizers import Adam
from tensorflow import Graph, Session
from keras import backend as K
from PIL import ImageFile
from shutil import copyfile
import tempfile
import os.path
import os
import shutil

# Prevent errors during image loading
ImageFile.LOAD_TRUNCATED_IMAGES = True

model_file_path = os.environ.get('PROPAGANDA_CNN_MODEL')
model_weights_file_path = os.environ.get('PROPAGANDA_CNN_MODEL_WEIGHTS')

neutral_or_positive_classifier = 'nato_neutral_or_positive_propaganda'
negative_classifier = 'nato_bad_propaganda'

img_width, img_height = 32, 32
batch_size = 16
nb_test_samples = 1

json_file = open(model_file_path, "r")
loaded_model_json = json_file.read()
json_file.close()


def load_model():
    loaded_model = model_from_json(loaded_model_json)
    loaded_model.load_weights(model_weights_file_path)
    loaded_model.compile(loss='binary_crossentropy', optimizer=Adam(lr=1e-5), metrics=['accuracy'])
    loaded_model._make_predict_function()
    return loaded_model


model = load_model()
graph = tf.get_default_graph()


def assert_file_exists(file_path):
    return os.path.isfile(file_path)


def is_negative(source_file_path):
    assert_file_exists(source_file_path)
    file_basename = os.path.basename(source_file_path)

    tempdir = tempfile.mkdtemp()
    output_directory = os.path.join(tempdir, negative_classifier)
    os.mkdir(output_directory)

    result_processing_file_name = os.path.join(output_directory, file_basename)

    copyfile(source_file_path, result_processing_file_name)

    datagen = ImageDataGenerator(rescale=1.0 / 255)

    test_generator = datagen.flow_from_directory(
        tempdir,
        target_size=(img_width, img_height),
        batch_size=batch_size,
        class_mode='binary')

    global graph
    with graph.as_default():
        result = model.evaluate_generator(test_generator, nb_test_samples)

    # cleanup
    shutil.rmtree(tempdir)

    return True if result[1] == 1.0 else False
