

import tensorflow as tf
import numpy as np
import os
import time
import datetime
import data_helpers
from text_cnn import TextCNN
from tensorflow.contrib import learn
import csv
import sys

tf.flags.DEFINE_boolean("allow_soft_placement", True, "Allow device soft device placement")
tf.flags.DEFINE_boolean("log_device_placement", False, "Log placement of ops on devices")
tf.flags.DEFINE_string("checkpoint_dir", "./runs/1518866807/checkpoints", "Checkpoint directory from training run")
# tf.flags.DEFINE_integer("batch_size", 1, "Batch Size (default: 64)")

FLAGS = tf.flags.FLAGS
tf.flags.FLAGS(sys.argv)

checkpoint_file = tf.train.latest_checkpoint(FLAGS.checkpoint_dir)

graph = tf.Graph()
with graph.as_default():
    session_conf = tf.ConfigProto(
      allow_soft_placement=FLAGS.allow_soft_placement,
      log_device_placement=FLAGS.log_device_placement)
    sess = tf.Session(config=session_conf)
    with sess.as_default():
        while(1):
            
            x_raw=[input()]
            vocab_path = os.path.join(FLAGS.checkpoint_dir,"..", "vocab")
            vocab_processor = learn.preprocessing.VocabularyProcessor.restore(vocab_path)
            x_test = np.array(list(vocab_processor.transform(x_raw)))
            saver = tf.train.import_meta_graph("{}.meta".format(checkpoint_file))
            saver.restore(sess, checkpoint_file)
            input_x = graph.get_operation_by_name("input_x").outputs[0]
            dropout_keep_prob = graph.get_operation_by_name("dropout_keep_prob").outputs[0]
            predictions = graph.get_operation_by_name("output/predictions").outputs[0]
            # batches = data_helpers.batch_iter(list(x_test), FLAGS.batch_size, 1, shuffle=False)
            # all_predictions = []
            # for x_test_batch in batches:
            all_predictions = sess.run(predictions, {input_x: x_test, dropout_keep_prob: 1.0})
                # all_predictions = np.concatenate([all_predictions, batch_predictions])
            all_predictions = int(all_predictions)+1
            print(all_predictions)


