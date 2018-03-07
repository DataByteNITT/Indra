package com.databyte.indra;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.databyte.indra.remote.APIService;
import com.databyte.indra.remote.ApiUtils;
import com.databyte.indra.remote.RetrofitClient;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener, URLDialogInterface {



    TextView textView;
    //Vars
    private static final String TAG = "speechActivity";


    Button voiceBtn;
    Button sendBtn;
    RadioGroup userSelectionRadioGroup;

    HashMap<String, String> ttsMap;
    String processedString;
    TextToSpeech textToSpeech;
    SpeechRecognizer speechRecog;

    Context mContext;

    boolean user1 = true;
    APIService mAPIService;
    TextView mResponseTv;
    //End of Vars
    boolean isFinishedSpeaking = false;

    //animation vars
    ImageView anim1;
    ImageView anim2;
    ImageView anim3;
    ScaleAnimation growAnim,shrinkAnim;
    LinearLayout animationContainer;


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(ApiUtils.BASE_URL);
        } catch (URISyntaxException e) {
            Log.d("Hello",e.getMessage());

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_linkChange:

                showDialog();
                return true;
            default:return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    void showDialog() {
//        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DFragment newFragment = new DFragment();
        newFragment.Init(this);
        newFragment.show(getFragmentManager(),"dialog");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        voiceBtn = findViewById(R.id.ttsButton);
        userSelectionRadioGroup = findViewById(R.id.radiogrpID);

        sendBtn = findViewById(R.id.sendBtn);
        mResponseTv = findViewById(R.id.responseText);
        sendBtn.setOnClickListener(this);
        mAPIService = ApiUtils.getAPIService();


        voiceBtn.setEnabled(false);

        textToSpeech = new TextToSpeech(this, this);
        ttsMap = new HashMap<>();

        userSelectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                user1 = i == R.id.RadioBtnUser1;
            }
        });

        mSocket.on("receive",onNewMessage);
        mSocket.connect();


        InitAnimation();
//        startActivity(new Intent(MainActivity.this,SocketActivity.class));
            voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initBtnClick(view);
            }
        });

    }

    public void initBtnClick(View v) {

        ttsMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "1");

        //noinspection deprecation
        textToSpeech.speak("Hello", TextToSpeech.QUEUE_FLUSH, ttsMap);

    }


    public void initVoiceRecognition() {
        speechRecog = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecog.setRecognitionListener(new listener());
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecog.cancel();
        speechRecog.startListening(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecog != null) {
            speechRecog.stopListening();
            speechRecog.destroy();
        }
        textToSpeech.stop();
        textToSpeech.shutdown();

    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS){
            voiceBtn.setEnabled(true);
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener(){
                @Override
                public void onStart(String s) {
                    Log.d(TAG, "Start");
                }

                @Override
                public void onDone(String s) {
                    Log.d(TAG, "done");
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initVoiceRecognition();
                        }
                    });
                }

                @Override
                public void onError(String s) {
                }
            });

            voiceBtn.setEnabled(true);
            int result = textToSpeech.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language not supported");
                Intent installLanguage = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installLanguage);
            }
            Log.d(TAG, "Started Voice Speaker");
        }
    }

    @Override
    public void onClick(View view) {
        if ( isFinishedSpeaking) {
            startAnimation();
            attemptSend();
//             mAPIService.savePost(processedString).enqueue(new Callback<SpeechQueryCreator>() {
//                @Override
//                public void onResponse(Call<SpeechQueryCreator> call, Response<SpeechQueryCreator> response) {
//                    Toast.makeText(mContext, "post submitted to API.", Toast.LENGTH_SHORT).show();
//                    if (response.isSuccessful()) {
//                        if (mResponseTv.getVisibility() == View.GONE) {
//                            mResponseTv.setVisibility(View.VISIBLE);
//                        }
//                        mResponseTv.setText(response.message());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<SpeechQueryCreator> call, Throwable t) {
//                     Log.e(TAG, t.getMessage());
//                    Toast.makeText(mContext, "Unable to submit post to API.", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }
    void InitAnimation(){

        animationContainer = findViewById(R.id.animationContainer);
        animationContainer.setVisibility(View.INVISIBLE);
        growAnim =
                new ScaleAnimation(
                        1.0f, 1.15f,
                        1.0f, 2.0f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 1.5f);
        shrinkAnim = new ScaleAnimation(1.15f, 1.0f, 2.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1.5f);

        growAnim.setDuration(2000);
        shrinkAnim.setDuration(2000);


        anim1 = findViewById(R.id.anim1);
        anim2 = findViewById(R.id.anim2);
        anim3 = findViewById(R.id.anim3);

    }
    void startAnimation(){
        animationContainer.setVisibility(View.VISIBLE);
        anim2.setAnimation(shrinkAnim);
        anim1.setAnimation(growAnim);
        anim3.setAnimation(growAnim);
        shrinkAnim.start();
        growAnim.start();

        growAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim1.setAnimation(shrinkAnim);
                anim3.setAnimation(shrinkAnim);
                shrinkAnim.start();    //start growth of others from here
//                shrinkAnim.start();
                anim2.setAnimation(growAnim);
                growAnim.start();
            }
        });
        shrinkAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim1.setAnimation(growAnim);
                anim3.setAnimation(growAnim);
                growAnim.start();
                anim2.setAnimation(shrinkAnim);
                shrinkAnim.start();
            }
        });

    }
    void turnOffAnimation(){
        animationContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClickedButton(String link) {
        Log.d("Hello","Called");
//        try {
//            mSocket = IO.socket("https://" + link);
//        }catch (URISyntaxException e){
//            Log.d("Hello",e.getMessage());
//        }
//        mSocket.on("receive",onNewMessage);
//        mSocket.connect();
    }

    public class listener implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(mContext, "Okay, Speak!", Toast.LENGTH_SHORT).show();
            isFinishedSpeaking = false;
            startAnimation();
        }

        public void onBeginningOfSpeech() {

        }

        public void onRmsChanged(float rmsdB) {

        }

        public void onBufferReceived(byte[] buffer) {

        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
            Toast.makeText(mContext, "Finished", Toast.LENGTH_SHORT).show();
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
        }

        public void onResults(Bundle results) {
            turnOffAnimation();
            ArrayList data;
            data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if ((data != null ? data.size() : 0) != 0) {
                int lastIndex = data.size() - 1;
                processedString = data.get(lastIndex).toString().toLowerCase();
                textView.setText(processedString);
                isFinishedSpeaking = true;

            }
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    private void attemptSend() {
        mSocket.emit("message", processedString);
        Log.d("Hello","sent");
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //Log.d("Hello","Received");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args[0];
                    turnOffAnimation();
                    String link;
                    link = (String) args[0];
                    Log.d("Hello","Received");
                    if(TextUtils.equals(link,"UNK")){
                        link = "No proper result found";
                    }
                    else {
                        Log.d("Hello","Received");
                        Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
                        i.putExtra(SearchManager.QUERY,link);
                        startActivity(i);

                    }

//                    try {
////                        username = data.getString("username");
////                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        username = "bleh";
//                        message = " bleh";
//                    }
                    // add the message to vie
                    addMessage(link);
                }
            });
        }
    };

    private void addMessage(String link) {
        textView.setText(link);
    }
}
