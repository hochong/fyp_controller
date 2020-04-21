package com.example.fyp_controller;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import java.io.OutputStream;
import java.net.Socket;

import com.makerlab.protocol.Mobile;
import com.makerlab.protocol.Turret;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class fyp002_activity_joystick_control extends AppCompatActivity {
    private fyp002_controller_application fypCA;
    private Socket s;
    private OutputStream outputStream;
    private final byte MOBILE = 0;
    private final byte TURRET = 1;
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
        s = fypCA.get_connection_socket();
        try {
            outputStream = s.getOutputStream();
        }catch (Exception e) {
            outputStream = null;
        }
        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            //TODO implement actual moving through bluetooth connection
            public void onMove(int angle, int strength) {
                try{
                    if (angle > 70 && angle <= 110 && strength > 70){
                        Log.i("MainActivity", "move forward");
                        outputStream.write(MOBILE + Mobile.SIDEWAY_UP);
                    }
                    if (angle > 160 && angle <= 200 && strength > 70){
                        Log.i("MainActivity", "move left");
                        outputStream.write(MOBILE + Mobile.SIDEWAY_LEFT);
                    }
                    if ((angle > 340 || angle <= 20) && strength > 70){
                        Log.i("MainActivity", "move right");
                        outputStream.write(MOBILE + Mobile.SIDEWAY_RIGHT);
                    }
                    if (angle > 250 && angle <= 290 && strength > 70){
                        Log.i("MainActivity", "move backward");
                        outputStream.write(MOBILE + Mobile.SIDEWAY_DOWN);
                    }
                    if (angle > 120 && angle <= 160 && strength > 70){
                        Log.i("MainActivity", "move up + right");
                        outputStream.write(MOBILE + Mobile.DIAG_UP_RIGHT);
                    }
                    if (angle > 20 && angle <= 70 && strength > 70){
                        Log.i("MainActivity", "move up + left");
                        outputStream.write(MOBILE + Mobile.DIAG_UP_LEFT);
                    }
                    if (angle > 200 && angle <= 250 && strength > 70){
                        Log.i("MainActivity", "move down + right");
                        outputStream.write(MOBILE + Mobile.DIAG_DOWN_RIGHT);
                    }
                    if (angle > 290 && angle <= 340 && strength > 70){
                        Log.i("MainActivity", "move down + left");
                        outputStream.write(MOBILE + Mobile.DIAG_DOWN_LEFT);
                    }
                    else{
                        Log.i("MainActivity", "halt");
                        outputStream.write(MOBILE + Mobile.HALT);
                        outputStream.write(MOBILE + Turret.HALT);
                    }
                } catch (Exception e) {
                    Log.i("activityjoystickcontrol", "failed with exception: "+e);
                }
            }
        });
    }

    public void toVoiceControl(View view) {
        Intent i = new Intent(this, fyp002_activity_voice_control.class);
        startActivity(i);
    }

}
