package com.example.fyp_controller;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class fyp002_controller_application extends Application{

    private String socketaddress;                                       /*ip address*/
    private int socketport;                                             /*port*/

    private Socket socket = null;                                       /*socket*/
    private OutputStream outputStream = null;                           /*outputs stream from socket*/

    private Timer timertask = null;
    int MOBILE_DELAY = 1000;                                              /*delay for timer*/
    int MOBILE_INTERVAL = 1000;                                           /*interval for timer*/

    private byte sendbyte0, sendbyte1;                                    /*byte0 - mobile or turret*/
                                                                          /*byte 1 - instructions*/
    /*
    Public function definitions

    Function Name: void set_sendbyte

    Description:
        access byte 0 adn byte 1 from other activities

    Import:
        b0, byte, indicate robot(0) or robot arm(1)
        b1, byte, instruction

    Export:
        no export

    Return:
        no return
    */
    public synchronized void set_sendbyte(byte b0, byte b1) {
        sendbyte0 = b0;sendbyte1 = b1;
    }

    /*
    Public function definitions

    Function Name: void get_sendbyte0

    Description:
        return byte 0 value

    Import:
        no import

    Export:
        no export

    Return:
        return sendbyte0
    */
    public byte get_sendbyte0() {
        return sendbyte0;
    }

    /*
    Public function definitions

    Function Name: void get_sendbyte1

    Description:
        return byte 1 value

    Import:
        no import

    Export:
        no export

    Return:
        return sendbyte1
    */
    public byte get_sendbyte1() {
        return sendbyte1;
    }

    /*
    Public function definitions

    Function Name: void set_connection_socket

    Description:
        create a application long socket connection to the server socket

    Import:
        add, String, IPV4 address of the server socket
        port, int, port of the server socket

    Export:
        no export

    Return:
        return success(true)/ fail(false)
    */
    public synchronized boolean set_connection_socket(String add, int port) {
        //handle socket in UI thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        socketaddress = add;
        socketport = port;
        try {
            socket = new Socket(add,port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    Public function definitions

    Function Name: void start_handler_timer_task

    Description:
        start running the timer task for connection
        will not start running if there is already a existing one

    Import:
        no import

    Export:
        no export

    Return:
        no return
    */
    public void start_handler_timer_task() {
        if (timertask != null) {
            return;
        }
        timertask = new Timer();
        timertask.scheduleAtFixedRate(new fyp002_Handler(), MOBILE_DELAY/2, MOBILE_INTERVAL/2);
    }

    public class fyp002_Handler extends TimerTask {
        /*
        Public function definitions

        Function Name: void run

        Description:
            sending b0, b1 to another phone at regular time interval

        Import:
            no import

        Export:
            no export

        Return:
            no return
        */
        @Override
        public void run() {
            if (socket != null) {
                byte b0 = get_sendbyte0();
                byte b1 = get_sendbyte1();
                Log.e("handler", String.valueOf(b0) + String.valueOf(b1));
                if (outputStream == null) {
                    try {
                        outputStream = socket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    outputStream.write(new byte[]{(byte) 1, b0, b1, (byte) 1});         /*1, b0, b1, 1*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
