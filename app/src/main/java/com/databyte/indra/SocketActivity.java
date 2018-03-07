package com.databyte.indra;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.databyte.indra.remote.ApiUtils;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class SocketActivity extends AppCompatActivity {


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(ApiUtils.BASE_URL);
        } catch (URISyntaxException e) {
            Log.d("Hello",e.getMessage());

        }
    }

    private EditText mInputMessageView;
    Button b;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        mInputMessageView = findViewById(R.id.socket_editText);
        b = findViewById(R.id.socket_btn);
        mSocket.on("receive",onNewMessage);
        mSocket.connect();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSend();
            }
        });
        mContext = this;

    }

    private void attemptSend() {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mInputMessageView.setText("");
        mSocket.emit("message", message);
        Log.d("Hello","sent");
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    username = (String) args[0];
                    message = " bleh";
//                    try {
////                        username = data.getString("username");
////                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        username = "bleh";
//                        message = " bleh";
//                    }
                    // add the message to view
                    Log.d("Hello","got oit");
                    addMessage(username, message);
                }
            });
        }
    };

    private void addMessage(String username, String message) {
        mInputMessageView.setText(username + message);
    }
}
