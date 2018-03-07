<div align="center">
  <img src="https://i.imgur.com/kS2lNm6.png">
</div>


# Indra

> AI assistant


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

```
TBC
```

### Results

```
TBC
```

### Pitfalls

- The dependency tree was not able to be effectively used, this is an area that we can improve
- CNN classification was not very accurate, this could be because of data bias or because of hyper parameters not being trained properly.

### Misc

```
TBC
```

#### Installation and building instructions

```
TBC
```

#### Dependencies

```
TBC
```

#### Resources
- Google NLP API
- Speech To Text API