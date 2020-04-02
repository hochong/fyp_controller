package com.example.fyp_controller;

import android.app.Application;

import java.net.Socket;

public class fyp002_controller_application extends Application{
    private Socket socket;
    public synchronized void set_connection_socket(Socket s){socket = s;}
    public synchronized Socket get_connection_socket(){return socket;}
}
