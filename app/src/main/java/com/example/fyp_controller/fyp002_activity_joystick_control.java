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

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class fyp002_activity_joystick_control extends AppCompatActivity {
    private fyp002_controller_application fypCA;
    private Socket s;
    private OutputStream outputStream;
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
                    //move forwards 90+-20 activate over 70%
                    if (angle > 70 && angle < 110 && strength >70){
                        Log.i("activityjoystickcontrol", "move forward");
                            outputStream.write((byte) 0);

                    }
                    //move left 180+-20 activate over 70%
                    if (angle > 160 && angle < 200 && strength >70){
                        Log.i("activityjoystickcontrol", "move left");
                            outputStream.write((byte) 9);
                    }

                    //move right 0+-20 activate over 70%
                    if ((angle > 340 || angle < 20) && strength >70){
                        Log.i("activityjoystickcontrol", "move right");
                            outputStream.write((byte) 3);
                    }

                    //move backwards 270+-20 activate over 70%
                    if (angle > 250 && angle < 290 && strength >70){
                        Log.i("activityjoystickcontrol", "move backward");
                            outputStream.write((byte) 6);
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
