package com.example.fyp_controller;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import com.makerlab.protocol.Mobile;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class fyp002_activity_joystick_control extends AppCompatActivity {
    private fyp002_controller_application fypCA;                        /*helper class*/
    private final byte MOBILE = 0;                                      /*byte for robot*/

    /*
    Protected function definitions

    Function Name: void onCreate
                    Bundle savedInstanceState

    Description:
        first called when activity launched
        set the layout and listeners

    Import:
        savedInstanceState, Bundle, initial or state of this activity before it is paused

    Export:
        no export

    Return:
        no return
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fyp002_activity_joystick_control);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Switch to Voice Control", Snackbar.LENGTH_LONG)
                        .setAction("Go", new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                toVoiceControl(v);
                            }
                        }).show();
            }
        });
        fypCA = (fyp002_controller_application)getApplication();

        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            //TODO implement actual moving through bluetooth connection
            public void onMove(int angle, int strength) {
                try{
                    if (angle > 70 && angle <= 110 && strength > 70){
                        fypCA.set_sendbyte(MOBILE,Mobile.SIDEWAY_UP);
                    }
                    else if (angle > 160 && angle <= 200 && strength > 70){
                        fypCA.set_sendbyte(MOBILE, Mobile.SIDEWAY_RIGHT);
                    }
                    else if ((angle > 340 || angle <= 20) && strength > 70){
                        fypCA.set_sendbyte(MOBILE, Mobile.SIDEWAY_LEFT);
                    }
                    else if (angle > 250 && angle <= 290 && strength > 70){
                        fypCA.set_sendbyte(MOBILE, Mobile.SIDEWAY_DOWN);
                    }
                    else if (angle > 120 && angle <= 160 && strength > 70){
                        fypCA.set_sendbyte(MOBILE, Mobile.DIAG_UP_RIGHT);
                    }
                    else if (angle > 20 && angle <= 70 && strength > 70){
                        fypCA.set_sendbyte(MOBILE, Mobile.DIAG_DOWN_RIGHT);
                    }
                    else if (angle > 200 && angle <= 250 && strength > 70){
                        fypCA.set_sendbyte(MOBILE, Mobile.DIAG_UP_LEFT);
                    }
                    else if (angle > 290 && angle <= 340 && strength > 70){
                        fypCA.set_sendbyte(MOBILE, Mobile.DIAG_DOWN_LEFT);
                    }
                    else{
                        fypCA.set_sendbyte(MOBILE, Mobile.HALT);
                    }
                } catch (Exception e) {
                    Log.i("activityjoystickcontrol", "failed with exception: "+e);
                }
            }
        });
    }

    /*
    Public function definitions

    Function Name: void toVoiceControl
                    View view

    Description:
        get user to VoiceControl activity

    Import:
        view View

    Export:
        no export

    Return:
        no return
    */
    public void toVoiceControl(View view) {
        Intent i = new Intent(this, fyp002_activity_voice_control.class);
        startActivity(i);
    }

}
