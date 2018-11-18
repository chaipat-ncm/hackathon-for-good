import tensorflow as tf
import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import tensorlayer as tl
import time
import DataContainer as dc
import logging
from tensorlayer.layers import *
import ResModel
import rnet
from sklearn.preprocessing import normalize

data_dir = '/home/stephan/Documents/Data_01/RedCross/'
train_output_dir = data_dir + 'tfrecords/train_record'
dev_output_dir = data_dir + 'tfrecords/dev_record'

data_container = dc.DataContainer(train_output_dir, dev_output_dir)

# data_container.visualise_data(True)
tf.logging.set_verbosity(tf.logging.DEBUG)
tl.logging.set_verbosity(tl.logging.DEBUG)
num_training = 6000
batch_size = 32
newmodel = False
def train():

    sess = tf.InteractiveSession()
    n_epoch = 20
    n_step_epoch = int(num_training / batch_size)

    y_batch = data_container.y_train_batch
    x_batch = data_container.x_train_batch
    k_batch = data_container.key_train_batch

    y_dev_batch = data_container.y_dev_batch
    x_dev_batch = data_container.x_dev_batch

    coord = tf.train.Coordinator()
    threads = tf.train.start_queue_runners(sess=sess, coord=coord)

    train_network = ResModel.ResModel(5).create_model(x_batch)
    # train_network = rnet
    # test_network = ResModel.ResModel(5).create_model(x_dev_batch, is_train=False, reuse=True)


    # Training cost
    y = train_network.outputs


    train_cost = tl.cost.cross_entropy(y, y_batch, name='xentropy')

    correct_train_prediction = tf.equal(tf.argmax(y, 1), y_batch)
    acc_train = tf.reduce_mean(tf.cast(correct_train_prediction, tf.float32))

    # Evaluation cost
    y2 = train_network.outputs
    cost_test = tl.cost.cross_entropy(y2, y_dev_batch, name='xentropy2')
    correct_test_prediction = tf.equal(tf.argmax(y2, 1), y_dev_batch)
    acc_test = tf.reduce_mean(tf.cast(correct_test_prediction, tf.float32))

    # train_op = tf.train.GradientDescentOptimizer(learning_rate=0.0001).minimize(train_cost)
    train_op = tf.train.AdamOptimizer(0.001).minimize(train_cost)
    sess.run(tf.global_variables_initializer())

    if not newmodel:
        load_params = tl.files.load_npz(name=data_dir + 'models/model_test_4.npz')
        tl.files.assign_params(sess, load_params, train_network)

    for epoch in range(n_epoch):
        print("EPOCH NUM:", epoch)
        start_time = time.time()
        train_loss, train_acc, n_batch = 0, 0, 0
        for s in range(n_step_epoch):
            err, ac, _ = sess.run([train_cost, acc_train, train_op])
            train_loss += err
            train_acc += ac
            n_batch += 1

            # if s % 50 == 0:
            print("Epoch:", epoch, "of", n_epoch, "Step number:", str(s), "of", n_step_epoch)
            print("ERROR", err , "ACC", ac)

        print("Epoch took:", time.time() - start_time)
        print("Train loss:" , (train_loss / n_batch))
        print("Training accuracy", (train_acc / n_batch))
        tl.files.save_npz(train_network.all_params, name=data_dir + 'models/model_test_' + str(epoch) + '.npz', sess=sess)

        dev_loss, dev_acc, n_batch = 0, 0, 0
        for s in range(n_step_epoch):
            err, ac = sess.run([cost_test, acc_test])
            dev_loss += err
            dev_acc += ac
            n_batch += 1

        print("Dev loss:" , (dev_loss / n_batch))
        print("Dev accuracy", (dev_acc / n_batch))

    coord.request_stop()
    coord.join(threads)
    sess.close()

train()
