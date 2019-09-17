package com.example.mainapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Login extends AppCompatActivity {

    public Socket socket;

    private EditText email;
    private EditText password;
    private Button login;
    private TextView signin;
    private String msg = "";
    /*

     this user id and password will work as session id for the app

     */
    public static String userid = "";
    public static String userpassword = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);/* will redirect to the FirstPage */
        /*
        *
        * email password login signin is used for taking input from the layout
        *
        * */
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        signin = (TextView) findViewById(R.id.textView6);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                *
                * Check whether email and password is not null
                * */
                if (email.getText().toString().isEmpty() == false || password.getText().toString().isEmpty()==false) {
                    Thread t1 = new Thread(new clientThread());
                    t1.start();
                    /*
                    *
                    * Start the client thread for authentication
                    * */
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                        /*
                        * check message received from the server
                        * if it contains 200 that is valid user id and password
                        * if it is 404 then invalid  user id and password
                        * otherwise Internet connection is not on
                        * */

                    if (msg.contains("200")) {
                        /* if it is a valid userid and password then this page will direct to the options page */
                        userid = email.getText().toString();
                        userpassword = password.getText().toString();
                        Intent intent = new Intent(Login.this, Options.class);
                        startActivity(intent);
                        finish();
                    } else if (msg.contains("404")){
                        msg="";
                        /*
                        *  Authentication failed
                        * Generate an alert box to ensure the user about authentication failure
                        *
                        * */
                        new AlertDialog.Builder(Login.this)

                                .setTitle("Wrong user id and password")
                                .setMessage("Do you want to create new account?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        /*
                                        * if the user wants to sign up he will be directed to the signup page
                                        *
                                        * */
                                        Intent intent=new Intent(Login.this,SignUp.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }).setNegativeButton("No", null).show();
                    }
                    else {
                        msg="";
                        /*
                        * this else statements means no iunternet connection for either server or the app
                        * but we take server will always be opened
                        * an alert will be generate for turning on the internet
                        * */
                        new AlertDialog.Builder(Login.this)
                                .setTitle("Please turn on your data connection")
                                .setMessage("Do you want to exit?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                }).setNegativeButton("No", null).show();
                    }


                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {


        Intent intent = new Intent(Login.this, FirstPage.class);
        startActivity(intent);
        finish();
        return true;
    }

    class clientThread implements Runnable {
        /*
        *
        *
        * This thread is used for authentication from the server
        *
        * */

        @Override
        public void run() {
            try {


                socket = new Socket(FirstPage.ip, FirstPage.port);


                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                /*
                *
                * this sending message manage a protocol
                * where messsagetype will declare what type of message it is
                *
                * */
                String sendmsg = "messagetype=login#";
                sendmsg += "userid=" + email.getText().toString() + "#" + "password=" + password.getText().toString();
                out.write(sendmsg.getBytes("UTF8"));
                out.flush();
                byte[] b = new byte[5164];
                in.read(b);
                msg = "";
                msg = new String(b, StandardCharsets.UTF_8);


                in.close();
                out.close();
                socket.close();

            } catch (UnknownHostException ex) {
                //txt.setText(ex.toString());

            } catch (IOException ex) {
                ////txt.setText(ex.toString());

            } catch (Exception ex) {

                //txt.setText(ex.toString());
                ex.printStackTrace();
            }
            //txt.setText(" Client Thread has stoped ");


        }
    }
}
