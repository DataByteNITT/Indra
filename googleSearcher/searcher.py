# -*- coding: utf-8 -*-
"""
Created on Fri Feb  2 23:10:01 2018
@author: satra_000
"""

import json
import requests
import webbrowser


class GoogleSearcher:
    TOPIC = {1: 0.2826, 2: 0.0369, 3: 0.0042, 4: 0.0495, 5: 0.3333, 6: 0.0832, 7: 0.0196, 8: 0.1449,
             9: 0.0100, 10: 0.0359}  # for topic of conversation
    ACT = {1: 0.452, 2: 0.286, 3: 0.168, 4: 0.094}  # for type of the conversation
    EMOTION = {0: 0.7453, 1: 0.0587, 2: 0.0203, 3: 0.01, 4: 0.07402, 5: 0.0661, 6: 0.1047}
    Google_Entities = {"ORGANIZATION": 0.25, "PERSON": 0.4, "CONSUMER_GOOD": 0.15, "LOCATION": 0.1,
                       "EVENT": 0.03, "OTHER": 0.01, "WORK_OF_ART": 0.06}
    SPEECH = ""
    EMO = 0
    AC = 0
    TOP = 0

    def __init__(self, sentence, e_tag, t_tag, a_tag):
        self.SPEECH = sentence
        self.EMO = self.EMOTION[e_tag]
        self.TOP = self.TOPIC[t_tag]
        self.AC = self.ACT[a_tag]
        self.json_salience = 'salience'
        self.json_word = 'word'
        self.json_wiki_url = 'wikipedia_url'
        self.json_word_type = 'word_type'
        self.json_type = 'type'
        self.json_dependency = 'dependency_tree'

    def process(self, response_string):
        # json_data is the string in json format
        entries = json.loads(response_string)  # response_string is the returned response from Google API
        calculated_prob = {}  # contains the calculated probabilities of the received words
        prob_const = self.EMO * self.AC * self.TOP
        for entry in entries:
            if entry[self.json_word_type] == "COMMON":
                if entry[self.json_type] == "PERSON":  # discard only common+person pairs
                    calculated_prob[entry[self.json_word]] = 0.0  # else will give it a value

                    # calculated_prob[entry[self.json_word]] = entry[self.json_salience]*prob_const*self.
                else:
                    calculated_prob[entry[self.json_word]] = entry[self.json_salience] * prob_const * \
                                                             self.Google_Entities[
                                                                 entry[self.json_type]]
            else:
                calculated_prob[entry[self.json_word]] = entry[self.json_salience] * prob_const * self.Google_Entities[
                    entry[self.json_type]]
            # print(entry[self.json_word])

        # for entry in entries:
        #     if entry[self.json_word_type] == "PROPER" and entry[self.json_type] == "PERSON" and\
        #             calculated_prob[entry[self.json_word]] < 0.5:
        #                 calculated_prob[entry[self.json_word]] = 0.0

        max_key, max_val = 0, -1000
        for k, v in calculated_prob.items():
            if max_val < v:
                max_val = v
                max_key = k
        #API_ENDPOINT = "http:localhost:3000/hell"  # endpoint URL
        #link = "https://www.google.co.in/search?q={}".format(max_key)
        print(max_val)
        if max_val == -1000:
            print("open plain google")
            data = {'original_sentence': self.SPEECH,
                    'response_state': 0}  # response_state holds the value of whether any search result is returnable
            # or not
            link = "https://www.google.co.in"
            webbrowser.open_new_tab(link)
        if max_val > 0:
            print("search found")
            link = "https://www.google.co.in/search?q={}".format(max_key)
            webbrowser.open_new_tab(link)
            data = {'original_sentence': self.SPEECH,
                    'url': link,
                    'response_state': 1}
        #r_post_object = requests.post(url=API_ENDPOINT + "/algo", data=data)
        #print(r_post_object.content)
        #print(link)
        print(self.TOP, ' ', self.EMO, ' ', self.AC)


        """THINGS YET TO BE DONE:
                        1) Dependency tree
                        3) Receive values for topic, act and emotion

                        POINTS TO BE NOTED:
                        1) webbrowser library was being used to open links
                        2) requests library is being used to send GET and POST requests to server
                        3) the PIL (pillow) library is used to open images on the computer
                        4) from server side you are receiving the following data
                                a) the original inputted string
                                b) the index of TOPIC, ACT, EMOTION
                                c) the JSON object of the google entities along with the dependency of noun and verbs
                        5) Convert anything to json recognizable string using json.loads() belonging to the json library
                        """
