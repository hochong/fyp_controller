package com.example.fyp_controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class fyp002_MainActivity extends AppCompatActivity {
    private String socket_server_address = "";
    private int socket_server_port = 9020;
    private EditText server_address;
    private EditText server_port;
    fyp002_controller_application fypCA;

    /*
    Protected function definitions

    Function Name: void onCreate
                    Bundle savedInstanceState

    Description:
        first called when activity launched
        set the layout and references EditTexts

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
        setContentView(R.layout.activity_main);
        server_address = (EditText)findViewById(R.id.server_address_text);
        server_port = (EditText) findViewById(R.id.server_port_text);
        fypCA = (fyp002_controller_application) getApplication();
    }

    /*
    Public function definitions

    Function Name: void connect_and_go_to_joystick_activity
                    View view

    Description:
        button listener
        connect to other phone by collecting inputs from users
        display toast message on success/fail to notify users

    Import:
        view View

    Export:
        no export

    Return:
        no return
    */
    public void connect_and_go_to_joystick_activity(View view) {
        socket_server_address = server_address.getText().toString();
        socket_server_port = Integer.parseInt(server_port.getText().toString());

        if(fypCA.set_connection_socket(socket_server_address,socket_server_port)) {
            fypCA.start_handler_timer_task();
            Toast.makeText(this, "Connection successful!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, fyp002_activity_joystick_control.class);
            startActivity(i);
        }
        else {
            Toast.makeText(this, "Connection failed!", Toast.LENGTH_LONG).show();
        }
    }
}
