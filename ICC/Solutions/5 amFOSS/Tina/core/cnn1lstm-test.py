from __future__ import print_function
import numpy as np
np.random.seed(1337)  # for reproducibility

from keras.preprocessing import sequence
from keras.models import Sequential
from keras.layers import Dense, Dropout, Activation, Lambda
from keras.layers import Embedding
from keras.layers import Convolution1D,MaxPooling1D, Flatten
from keras.datasets import imdb
from keras import backend as K
from sklearn.cross_validation import train_test_split
import pandas as pd
from keras.utils.np_utils import to_categorical

from sklearn.preprocessing import Normalizer
from keras.models import Sequential
from keras.layers import Convolution1D, Dense, Dropout, Flatten, MaxPooling1D
from keras.utils import np_utils
import numpy as np
import h5py
from keras import callbacks
from keras.layers import LSTM, GRU, SimpleRNN
from keras.callbacks import CSVLogger
from keras.callbacks import ModelCheckpoint, EarlyStopping, ReduceLROnPlateau, CSVLogger
from sklearn.metrics import (precision_score, recall_score,
                             f1_score, accuracy_score)
from sklearn import metrics
from sklearn.preprocessing import Normalizer

traindata = pd.read_csv('data/train.csv', header=None)
trainlabel = pd.read_csv('data/trnlabels.csv', header=None)
testdata = pd.read_csv('data/test.csv', header=None)
testlabel = pd.read_csv('data/teslabels.csv', header=None)

X = traindata.iloc[:,0:1024]
Y = trainlabel.iloc[:,0]
C = testlabel.iloc[:,0]
T = testdata.iloc[:,0:1024]


trainX = np.array(X)
testT = np.array(T)


y_train1 = np.array(Y)
y_test1 = np.array(C)

y_train= to_categorical(y_train1)
y_test= to_categorical(y_test1)

# reshape input to be [samples, time steps, features]
X_train = np.reshape(trainX, (trainX.shape[0],trainX.shape[1],1))
X_test = np.reshape(testT, (testT.shape[0],testT.shape[1],1))

lstm_output_size = 70

cnn = Sequential()
cnn.add(Convolution1D(64, 3, border_mode="same",activation="relu",input_shape=(1024, 1)))
cnn.add(MaxPooling1D(pool_length=(2)))
cnn.add(LSTM(lstm_output_size))
cnn.add(Dropout(0.1))
cnn.add(Dense(25, activation="softmax"))


name = []
score = []
f = open("results/60-40/lstm1-60-40results.txt","w") 
import os
for file in os.listdir("results/60-40/cnnlstm1results/"):
  cnn.load_weights("results/60-40/cnnlstm1results/"+file)
  y_pred = cnn.predict_classes(X_test)
  accuracy = accuracy_score(y_test1, y_pred)
  recall = recall_score(y_test1, y_pred , average="weighted")
  precision = precision_score(y_test1, y_pred , average="weighted")
  f1 = f1_score(y_test1, y_pred, average="weighted")
  name.append(file)
  score.append(accuracy)
  f.write(file)
  f.write(",")
  f.write(str(accuracy))
  f.write(",")
  f.write(str(recall))
  f.write(",")
  f.write(str(precision))
  f.write(",")
  f.write(str(f1))
  f.write("\n")  

print(max(score))
print(name[score.index(min(score))])

cnn.load_weights("results/60-40/cnnlstm1results/"+name[score.index(max(score))])
y_pred = cnn.predict_classes(X_test)
accuracy = accuracy_score(y_test1, y_pred)
recall = recall_score(y_test1, y_pred , average="weighted")
precision = precision_score(y_test1, y_pred , average="weighted")
f1 = f1_score(y_test1, y_pred, average="weighted")
f.write("================================================================")
f.write(str(accuracy))
f.write(",")
f.write(str(recall))
f.write(",")
f.write(str(precision))
f.write(",")
f.write(str(f1))
f.write("================================================================")
np.savetxt("results/60-40/lstm1predicted.txt", np.transpose([y_test1,y_pred]), fmt='%01d')