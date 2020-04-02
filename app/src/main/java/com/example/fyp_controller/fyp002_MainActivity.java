package com.example.fyp_controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.Socket;

public class fyp002_MainActivity extends AppCompatActivity {
    private String socket_server_address = "";
    private int socket_server_port = 9020;
    private EditText server_address;
    private EditText server_port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText server_address = (EditText)findViewById(R.id.server_address_text);
        EditText server_port = (EditText) findViewById(R.id.server_port_text);
    }

    public void go_to_joystick_activity(View view) {
        socket_server_address = server_address.getText().toString();
        socket_server_port = Integer.parseInt(server_port.getText().toString());
        Socket s = null;
        try{
            s = new Socket(socket_server_address, socket_server_port);
        }catch (Exception e) {
            Log.e("MainActivity", "Connection error: " + e);
            Toast.makeText(getApplicationContext(), "Fail to connect to "+ socket_server_address + " at port "+ socket_server_port, Toast.LENGTH_LONG);
        }
        if (s != null) {
            Toast.makeText(getApplicationContext(), "Successfully connect to "+ socket_server_address + " at port "+ socket_server_port, Toast.LENGTH_LONG);
        }
        fyp002_controller_application fypCA = (fyp002_controller_application) getApplication();
        fypCA.set_connection_socket(s);
        Intent i = new Intent(this, fyp002_activity_joystick_control.class);
        startActivity(i);
    }
}
