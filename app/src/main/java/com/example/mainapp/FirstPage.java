package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;
/*
*
*  Author Nasim
* This is the first static page
*
*
* */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.Socket;

public class FirstPage extends AppCompatActivity {
    /*
    *  This ip is the server ip
    *   This port is the server port where server is listening
    * */
    public static final String ip = "192.168.43.233";
    public static final int port = 8080;

    public Socket socket;
    public TextView txt;
    public String message;
    public static String pathmesg = " Nothing ";
    public static int nowSynonym =1;
    public static int nowblank =1;
    public static int nowIdiom =1;
    public static int nowChoosebest =1;


    public static String[] data = new String[5164];
    public static int count = 0;
    /*
    *  there are some random variable used for garbage collection and other uses except ip & port
    * Any variable which will be accessed from different class must be declared static if the user doesn't want to create object
    *
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        Button login = (Button) findViewById(R.id.login);
        Button signup = (Button) findViewById(R.id.signup);
        final TextView textView = (TextView) findViewById(R.id.fstTxt);
/*
*
* log in button will direct the user to the login page
*
* */

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstPage.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        /*
         *
         *sign up  button will direct the user to the sign up page
         *
         * */
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstPage.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });


    }

}
