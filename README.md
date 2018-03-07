<div align="center">
  <img src="https://i.imgur.com/kS2lNm6.png">
</div>


# Indra

> Proof of Concept stage of AI assistant


### Idea

This project helps an AI assistant working autonomously understand when it should assist a user and when it should not.

### Our Approach

To realize this, we used the following approach:

#### Architechture

The project uses the following 4 components:

- [Backend](https://github.com/DataByteNITT/Indra/tree/js_server_branch)
- [Android App](https://github.com/DataByteNITT/Indra/tree/app_branch)
- [Python Backend](https://github.com/DataByteNITT/Indra/tree/python_backend)
- [Neural Network Implementation in TensorFlow](https://github.com/DataByteNITT/Indra/tree/cnn_impl_branch)
 
#### Android App
The Android app uses the following dependencies:
 	
- [Retrofit](http://square.github.io/retrofit/)
- [OKHttp](http://square.github.io/okhttp/)
- [Socket-IO](https://github.com/socketio/socket.io-client-java)

We used a google API ( Speech to Text ) to convert the spoken speech by the user to text.
The text was then sent to the Node.js server to be processed further.
All sending and receiving was done via sockets, because a live connection to the server is required to support multiple queries.
Upon receiving the query, the query contains the context word, for which we google, or else it contains a "UNK" (unknown), which when received, we dont google for anything and instead display a message to the user.

#### Node.js Server

The JS server receives the speech as a string of text from the app via a scoket connection established with the App. The string is then sent to the google's NLP API where it would receive 2 return responses: 
- First one contains the words tagged into categories like noun, verb, etc and a salience (indicating the importance of the word in the sentence).
- Second one contains a dependency Tree representation in JSON which links all the related words.

We forward these 2 responses received from google's API to the python server next.

#### Python Server

The python server uses the following dependencies:
-[Flask](https://github.com/pallets/flask)

We were taking in data from the node server and then sending it to the custom algorithm and vice versa

#### CNN 

We used a CNN implemention by YoonKim (https://arxiv.org/abs/1408.5882) and trained it on an open source dataset called Daily Dialog (https://arxiv.org/abs/1710.03957)

#### Custom Algorithm

We used a custom made python algorithm where we assigned values to different classes: Topic, Act, Emotion, Google_Entities. We used this, and received results from the CNN to find the highest product from the values of the classes. 
The search results for the "common noun --> person" were discarded. The highest product was then used to build a google search link and return results.

### Results

The application succesfully distinguishes between cases where it should return search results and when it should not.

The application worked with a fair degree of accuracy. The classification had an accuracy of around 8 on 10 times. The application can be further streamlined and improved from its current stage.


![Alt Text]()

### Pitfalls

- The dependency tree was not implemented due to time constraints. Implementing it will allow more complex searches to be performed.
- CNN classification was not very accurate, this could be because of data bias or because of hyper parameters not being trained properly.

### Misc

```
TBC
```

#### Installation and building instructions

- install ngrok
- run npm install in project directory
- run python main.py in project directory
- run node server in project directory
- run ngrok.exe
- set the ngrok url in the ApiUtils.java class of the app code 
- build the app from the app code


#### Dependencies

```
TBC
```

#### Resources
- Google NLP API
- Speech To Text API
