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

import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/*basically a copy of fyp001_VoiceControl*/
public class fyp002_activity_voice_control extends AppCompatActivity {
    private static final int MOBILE_DELAY = 1000;                           /*delay for robot timer*/
    private static final int TURRET_DELAY = 1000;                           /*delay for robot arm timer*/

    private fyp002_controller_application fypCA;                            /*helper class*/

    private SpeechRecognizer sr;                                            /*variables for speechrecognizer*/
    private Intent srIntent;                                                /*variables for speechrecognizer*/
    private int SpeechRecognizerInt = 3000;                                 /*variables for speechrecognizer*/

    private boolean MIC_ON = false;                                         /*variables for mic icon*/
    private ImageView mic;                                                  /*variables for mic icon*/

    private final byte MOBILE = 0;                                          /*byte for robot*/
    private final byte TURRET = 1;                                          /*byte for robot arm*/

    /*
    Protected function definitions

    Function Name: void onCreate
                    Bundle savedInstanceState

    Description:
        first called when activity launched
        set up the layout and references to layout components

    Import:
        savedInstanceState, Bundle, initial or state of this activity before it is paused

    Export:
        no export

    Return:
        no return value
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fyp002_activity_voice_control);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        mic = findViewById(R.id.micImg);

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
    }
    /*
    Public function definitions

    Function Name: void toJoystickControl

    Description:
        go to joystick control

    Import:
        no import

    Export:
        go to joystick control

    Return:
        no return value
     */
    public void toJoystickControl(View view) {
        Intent i = new Intent(this, fyp002_activity_joystick_control.class);
        startActivity(i);
    }

    /*
    Private function definitions

    Function Name: void startListening

    Description:
        call the speech recognizer

    Import:
        no import

    Export:
        call speech recognizer

    Return:
        no return value
     */
    public void startListening () {
        srIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        srIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        sr.startListening(srIntent);
        startActivityForResult(srIntent, SpeechRecognizerInt);
    }
    /*
    Protected function definitions

    Function Name: void onActivityResult
                        int requestCode
                        int resultCde
                        Intent data

    Description:
        handle the data returned after the speechrecognizer has processed the voice input of the user

    Import:
        requestCode, int, integer that specify what the request is
        resultCode, int, integer that specify whether the result has been successfully proceeded
        data, Intent, intent that contains data

    Export:
        identify the user's word from the data then call the helper functions from the application class bcra
        the helper function will then send instructions to the robot
        the mic icon will also be changed as well

    Return:
        no return value
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SpeechRecognizerInt){
            if (resultCode==RESULT_OK){
                String command = "init";
                try {
                    command = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                    Log.i("VoiceControlActivity", "result = " + command);
                    switch (command.toUpperCase()){
                        case "FORWARD" :
                            fypCA.set_sendbyte(MOBILE,Mobile.SIDEWAY_UP);
                            break;
                        case "BACKWARD" :
                            fypCA.set_sendbyte(MOBILE,Mobile.SIDEWAY_DOWN);
                            break;
                        case "LEFT" :
                            fypCA.set_sendbyte(MOBILE,Mobile.SIDEWAY_LEFT);
                            break;
                        case "RIGHT" :
                            fypCA.set_sendbyte(MOBILE,Mobile.SIDEWAY_RIGHT);
                            break;
                        case "STOP" :
                            fypCA.set_sendbyte(MOBILE,Mobile.HALT);
                            break;
                        case "UP RIGHT":
                            fypCA.set_sendbyte(MOBILE,Mobile.DIAG_DOWN_RIGHT);
                            break;
                        case "UPRIGHT":
                            fypCA.set_sendbyte(MOBILE,Mobile.DIAG_DOWN_RIGHT);
                            break;
                        case "UP LEFT":
                            fypCA.set_sendbyte(MOBILE,Mobile.DIAG_UP_RIGHT);
                            break;
                        case "DOWN RIGHT":
                            fypCA.set_sendbyte(MOBILE,Mobile.DIAG_DOWN_LEFT);
                            break;
                        case "DOWN LEFT":
                            fypCA.set_sendbyte(MOBILE,Mobile.DIAG_UP_LEFT);
                            break;
                        case "TURRET UP":
                            fypCA.set_sendbyte(TURRET,Turret.UP);
                            break;
                        case "TURRET DOWN":
                            fypCA.set_sendbyte(TURRET,Turret.DOWN);
                            break;
                        case "TURRET LEFT":
                            fypCA.set_sendbyte(TURRET,Turret.LEFT);
                            break;
                        case "TURRET RIGHT":
                            fypCA.set_sendbyte(TURRET,Turret.RIGHT);
                            break;
                        case "TURRET HOME":
                            fypCA.set_sendbyte(TURRET,Turret.HOME);
                            break;
                        default:
                            fypCA.set_sendbyte(MOBILE,Mobile.HALT);
                            break;
                    }
                    new Timer().schedule(new TimerTask(){
                        @Override
                        public void run(){
                            fypCA.set_sendbyte(MOBILE,Mobile.SIDEWAY_UP);
                        }
                    },MOBILE_DELAY);
                    new Timer().schedule(new TimerTask(){
                        @Override
                        public void run(){
                            fypCA.set_sendbyte(TURRET,Turret.HALT);
                        }
                    },TURRET_DELAY);
                }catch(Exception e){ Log.i("VoiceControlActivity", "failed with exception: "+e);}


            }
            MIC_ON = !MIC_ON;
            set_mic_image(MIC_ON);
        }
    }
    /*
    private function definitions

    Function Name: void set_mic_image
                        boolean onoff

    Description:
        set the mic image according to the input boolean

    Import:
        onoff, boolean, determine which image should be used

    Export:
        set the image using seImageResource

    Return:
        no return value
     */
    private void set_mic_image(boolean onoff) {
        if (onoff) {
            mic.setImageResource(R.drawable.fyp002_ic_mic_on_press);
        } else{
            mic.setImageResource(R.drawable.fyp002_ic_mic_on);
        }
    }
}
