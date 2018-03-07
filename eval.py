#! /usr/bin/env python

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
# Parameters
# ==================================================

# Data Parameters
tf.flags.DEFINE_string("data_file", "./data/daily_dialog/test/dialogues_test.txt", "Data source for the sentence data.")
tf.flags.DEFINE_string("label_file", "./data/daily_dialog/test/dialogues_act_test.txt", "Data source for the label data.")

# Eval Parameters
tf.flags.DEFINE_integer("batch_size", 64, "Batch Size (default: 64)")
tf.flags.DEFINE_string("checkpoint_dir", "./runs/1518866807/checkpoints", "Checkpoint directory from training run")
tf.flags.DEFINE_boolean("eval_train", False, "Evaluate on all training data")

# Misc Parameters
tf.flags.DEFINE_boolean("allow_soft_placement", True, "Allow device soft device placement")
tf.flags.DEFINE_boolean("log_device_placement", False, "Log placement of ops on devices")


FLAGS = tf.flags.FLAGS
tf.flags.FLAGS(sys.argv)
print("\nParameters:")
for attr, value in sorted(FLAGS.__flags.items()):
    print("{}={}".format(attr.upper(), value))
print("")

# CHANGE THIS: Load data. Load your own data here
if FLAGS.eval_train:
    x_raw = ["How are you?","I'm afraid not , sir .","everything is off.","Sure . There is a changing room behind you .","Yes . Can I see that T-shirt on the top shelf please ?"]
    # y_test = [[0,1,0,0],[0,1,0,0]]
    y_test = None
    #data_helpers.load_data_and_labels(FLAGS.positive_data_file, FLAGS.negative_data_file)
    # y_test = np.argmax(y_test, axis=1)
else:
    x_raw,y_test = data_helpers.load_data_and_labels(FLAGS.data_file,FLAGS.label_file,conv_to_one_hot = False)
    # x_raw = ["a masterpiece four years in the making", ]
    # y_test = [1, 0]

# Map data into vocabulary
vocab_path = os.path.join(FLAGS.checkpoint_dir,"..", "vocab")
vocab_processor = learn.preprocessing.VocabularyProcessor.restore(vocab_path)
x_test = np.array(list(vocab_processor.transform(x_raw)))
# print (np.shape(x_test))
# print (x_test)
print("\nEvaluating...\n")

# Evaluation
# ==================================================
checkpoint_file = tf.train.latest_checkpoint(FLAGS.checkpoint_dir)
print ("chk : %s" %checkpoint_file)
graph = tf.Graph()
with graph.as_default():
    session_conf = tf.ConfigProto(
      allow_soft_placement=FLAGS.allow_soft_placement,
      log_device_placement=FLAGS.log_device_placement)
    sess = tf.Session(config=session_conf)
    with sess.as_default():
        # Load the saved meta graph and restore variables
        saver = tf.train.import_meta_graph("{}.meta".format(checkpoint_file))
        saver.restore(sess, checkpoint_file)

        # Get the placeholders from the graph by name
        input_x = graph.get_operation_by_name("input_x").outputs[0]
        # input_y = graph.get_operation_by_name("input_y").outputs[0]
        dropout_keep_prob = graph.get_operation_by_name("dropout_keep_prob").outputs[0]

        # Tensors we want to evaluate
        predictions = graph.get_operation_by_name("output/predictions").outputs[0]
        # print("predictions : {}".format(np.shape(np.array(predictions))))
        scores = graph.get_operation_by_name("output/scores").outputs
        # Generate batches for one epoch
        batches = data_helpers.batch_iter(list(x_test), FLAGS.batch_size, 1, shuffle=False)
        # print(scores)
        # Collect the predictions here
        all_predictions = []
        all_scores = []
        for x_test_batch in batches:
            batch_predictions = sess.run(predictions, {input_x: x_test_batch, dropout_keep_prob: 1.0})
            batch_scores = sess.run(scores, {input_x: x_test_batch, dropout_keep_prob: 1.0})
            # print ("scores : %s" %batch_scores)

            all_predictions = np.concatenate([all_predictions, batch_predictions])
            # all_scores = np.concatenate([all_scores,batch_scores])
        # print ("predictions : %s" %all_predictions)
        # print ("predictions : %s" )

y_test = np.array(y_test).flatten()
y_test = list(map(lambda x : x-1,y_test))
# print(all_predictions.shape)
# print(y_test.shape)
print(y_test[:5])
print(all_predictions[:5])
    # Print accuracy if y_test is defined
        
        
if y_test is not None:
    correct_predictions = float(sum(all_predictions == y_test))
    print("Total number of test examples: {}".format(len(y_test)))
    print("Accuracy: {:g}".format(correct_predictions/float(len(y_test))))

# Save the evaluation to a csv
predictions_human_readable = np.column_stack((np.array(x_raw), all_predictions))
out_path = os.path.join(FLAGS.checkpoint_dir, "..", "prediction.csv")
print("Saving evaluation to {0}".format(out_path))
with open(out_path, 'w') as f:
    csv.writer(f).writerows(predictions_human_readable)
