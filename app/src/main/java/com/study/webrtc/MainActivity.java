package com.study.webrtc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements Emitter.Listener{

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IO.Options options = new IO.Options();
                String url = "https://www.crazyou.cn";
                try {
                    mSocket = IO.socket(url, options);
                    mSocket.on("new_message", MainActivity.this);
                    mSocket.connect();

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new_message");
    }

    @Override
    public void call(Object... args) {

    }
}
