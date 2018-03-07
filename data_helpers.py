import numpy as np
import re
import itertools
from collections import Counter
import os  

def clean_str(string):
    """
    Tokenization/string cleaning for all datasets except for SST.
    Original taken from https://github.com/yoonkim/CNN_sentence/blob/master/process_data.py
    """
    string = re.sub(r"[^A-Za-z0-9(),!?\'\`]", " ", string)
    string = re.sub(r"\'s", " \'s", string)
    string = re.sub(r"\'ve", " \'ve", string)
    string = re.sub(r"n\'t", " n\'t", string)
    string = re.sub(r"\'re", " \'re", string)
    string = re.sub(r"\'d", " \'d", string)
    string = re.sub(r"\'ll", " \'ll", string)
    string = re.sub(r",", " , ", string)
    string = re.sub(r"!", " ! ", string)
    string = re.sub(r"\(", " \( ", string)
    string = re.sub(r"\)", " \) ", string)
    # string = re.sub(r"\?", " \? ", string)
    string = re.sub(r"\s{2,}", " ", string)
    # string = re.sub()
    return string.strip().lower()


def load_data_and_labels(data_file,label_file,conv_to_one_hot = True):
    """
    Loads MR polarity data from files, splits the data into words and generates labels.
    Returns split sentences and labels.
    """
    # Load data from files
    data = list(open(data_file, "r",encoding="utf8").readlines())
    data = [s.strip() for s in data]
    # Split by words
    x_text = data
    x_text = [clean_str(sent) for sent in x_text]


    # Generate labels
    labels = assign_vector(label_file,conv_to_one_hot)
    # negative_labels = [[1, 0] for _ in negative_examples]
    y = np.array(labels)
    return [x_text, y]


def assign_vector(label_file,conv_to_one_hot = True):
    labels = open(label_file,"r",encoding="utf8").readlines()
    labels = [s.strip() for s in labels]
    k = []
    for label in labels:
        b = label.split(" ")
        b = [int(x) for x in b]
        # q = np.append(k,b,type=int)
        k.append(b)
        pass

    if conv_to_one_hot:
        y = np.concatenate([np.array(i) for i in k])   
        one_hot = {     1:[1,0,0,0],
                        2:[0,1,0,0],
                        3:[0,0,1,0],
                        4:[0,0,0,1]
                    }
        label_vectors = []
        for i in y:
            label_vectors.append(one_hot[i])
        return label_vectors

    else:
        return k
def batch_iter(data, batch_size, num_epochs, shuffle=True):
    """
    Generates a batch iterator for a dataset.
    """
    data = np.array(data)
    data_size = len(data)
    num_batches_per_epoch = int((len(data)-1)/batch_size) + 1
    for epoch in range(num_epochs):
        # Shuffle the data at each epoch
        if shuffle:
            shuffle_indices = np.random.permutation(np.arange(data_size))
            shuffled_data = data[shuffle_indices]
        else:
            shuffled_data = data
        for batch_num in range(num_batches_per_epoch):
            start_index = batch_num * batch_size
            end_index = min((batch_num + 1) * batch_size, data_size)
            yield shuffled_data[start_index:end_index]
