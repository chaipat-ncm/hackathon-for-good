# inspired by fashion_mnist.py
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import gzip
import os

from PIL import Image as img

from pathlib import Path
import numpy as np

from tensorflow.python.keras.utils.data_utils import get_file
from tensorflow.python.util.tf_export import tf_export


@tf_export('data.load_data')
def load_data():
    """Loads the Fashion-MNIST dataset.

  Returns:
      Tuple of Numpy arrays: `(x_train, y_train), (x_test, y_test)`.

  License:
      The copyright for Fashion-MNIST is held by Zalando SE.
      Fashion-MNIST is licensed under the [MIT license](
      https://github.com/zalandoresearch/fashion-mnist/blob/master/LICENSE).

  """

    files = [
        'gebouwen', 'testgebouwen'
    ]
    # dirty hack made with the best intentions, problems along the way
    x_train = []
    x_test = []
    i = 0
    # for fname in files:
    y_train = [4, 3, 1, 3, 2, 0, 3, 1]
    for filename in os.listdir('gebouwen'):
         x_train.append(np.array(img.open(os.getcwd() + '/gebouwen/' + filename))[:,:,0])

         print(i)
         i = i + 1
    x_train = np.array(x_train).reshape(-1, 28, 28)
    print(x_train.shape)

    y_test = [0]
    for filenames in os.listdir('testgebouwen'):
      x_test.append(np.array(img.open(os.getcwd() + '/testgebouwen/' + filenames))[:,:,0])

    x_test = np.array(x_test).reshape(-1, 28, 28)
    print(x_train)
    return (x_train, y_train), (x_test, y_test)
