package com.example.fyp_controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.makerlab.protocol.Mobile;
import com.makerlab.protocol.Turret;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class fyp002_activity_voice_control extends AppCompatActivity {
    private static final int MOBILE_DELAY = 1000;
    private static final int TURRET_DELAY = 1000;
    private fyp002_controller_application fypCA;
    private Socket s;
    private OutputStream outputStream;
    private SpeechRecognizer sr;
    private Intent srIntent;
    private String instruction = "forward, backward, left, right, stop";
    private boolean MIC_ON = false;
    private int SpeechRecognizerInt = 3000;
    private ImageView mic;
    private final byte MOBILE = 0;
    private final byte TURRET = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fyp002_activity_voice_control);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        ImageView mic = findViewById(R.id.micImg);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Switch to Voice Control", Snackbar.LENGTH_LONG)
                        .setAction("Go", new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                toJoystickControl(v);
                            }
                        }).show();
            }
        });
        fypCA = (fyp002_controller_application)getApplication();
        s = fypCA.get_connection_socket();
        try {
            outputStream = s.getOutputStream();
        }catch (Exception e) {
            outputStream = null;
        }

    }

    public void toJoystickControl(View view) {
        Intent i = new Intent(this, fyp002_activity_joystick_control.class);
        startActivity(i);
    }

    private void startListening () {
        srIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        srIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //srIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, instruction);
        sr.startListening(srIntent);
        startActivityForResult(srIntent, SpeechRecognizerInt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SpeechRecognizerInt){
            if (resultCode==RESULT_OK){
                String command = "init";
                try {
                    command = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                    Log.i("VoiceControlActivity", "result = " + command);
                    if (outputStream == null) {
                        return;
                    }
                    switch (command.toUpperCase()){
                        case "FORWARD" :
                            outputStream.write(MOBILE + Mobile.SIDEWAY_UP);
                            break;
                        case "BACKWARD" :
                            outputStream.write(MOBILE + Mobile.SIDEWAY_DOWN);
                            break;
                        case "LEFT" :
                            outputStream.write(MOBILE + Mobile.SIDEWAY_LEFT);
                            break;
                        case "RIGHT" :
                            outputStream.write(MOBILE + Mobile.SIDEWAY_RIGHT);
                            break;
                        case "STOP" :
                            outputStream.write(MOBILE + Mobile.HALT);
                            break;
                        case "UP RIGHT":
                            outputStream.write(MOBILE + Mobile.DIAG_UP_RIGHT);
                            break;
                        case "UPRIGHT":
                            outputStream.write(MOBILE + Mobile.DIAG_UP_RIGHT);
                            break;
                        case "UP LEFT":
                            outputStream.write(MOBILE + Mobile.DIAG_UP_LEFT);
                            break;
                        case "DOWN RIGHT":
                            outputStream.write(MOBILE + Mobile.DIAG_DOWN_RIGHT);
                            break;
                        case "DOWN LEFT":
                            outputStream.write(MOBILE + Mobile.DIAG_DOWN_LEFT);
                            break;
                        case "TURRET UP":
                            outputStream.write(TURRET + Turret.UP);
                            break;
                        case "TURRET DOWN":
                            outputStream.write(TURRET + Turret.DOWN);
                            break;
                        case "TURRET LEFT":
                            outputStream.write(TURRET + Turret.LEFT);
                            break;
                        case "TURRET RIGHT":
                            outputStream.write(TURRET + Turret.RIGHT);
                            break;
                        case "TURRET HOME":
                            outputStream.write(TURRET + Turret.HOME);
                            break;
                        default:
                            outputStream.write(MOBILE + Mobile.HALT);
                            outputStream.write(TURRET + Turret.HALT);
                            break;
                    }
                    new Timer().schedule(new TimerTask(){
                        @Override
                        public void run(){
                            try {
                                outputStream.write(MOBILE + Mobile.HALT);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    },MOBILE_DELAY);
                    new Timer().schedule(new TimerTask(){
                        @Override
                        public void run(){
                            try {
                                outputStream.write(TURRET + Turret.HALT);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    },TURRET_DELAY);
                }catch(Exception e){ Log.i("VoiceControlActivity", "failed with exception: "+e);}


            }
            MIC_ON = !MIC_ON;
            set_mic_image(MIC_ON);
        }
    }

    private void set_mic_image(boolean onoff) {
        if (onoff) {
            mic.setImageResource(R.drawable.fyp002_ic_mic_on_press);
        } else{
            mic.setImageResource(R.drawable.fyp002_ic_mic_on);
        }
    }
}
