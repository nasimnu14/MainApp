package com.example.mainapp;

/*
* This is for editing profile
* Nasim
*
* */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class EditProfile extends AppCompatActivity {
    private EditText username;
    private EditText email_id;
    private EditText pass;
    private EditText confirmpass;
    private Socket socket;
    private String msg1 = "";
    private String msg2 = "";
    private EditText bday;
    private EditText location1;
    /*
     *
     * username email password etc are  used for taking input from the layout
     *
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = (EditText) findViewById(R.id.username);
        email_id = (EditText) findViewById(R.id.email_id);
        location1 = (EditText) findViewById(R.id.location1);
        bday = (EditText) findViewById(R.id.bday);
        pass = (EditText) findViewById(R.id.pass);
        confirmpass = (EditText) findViewById(R.id.confirmpassword);
        Button edited = (Button) findViewById(R.id.edited);

        Thread t1 = new Thread(new EditProfile.ClientThread());
        /*
        *
        * thuis thread is for connect to the server
        *
        *
        * */
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {

        }
        String []tokens=msg1.split("#");
        if(tokens[0].contains("200")){
            username.setText(tokens[1]);
            email_id.setText(tokens[3]);
            String str=tokens[4].split(",")[0];
            location1.setText(str);
            bday.setText(tokens[5]);
        }

        edited.setOnClickListener(new View.OnClickListener() {
            /*
            *
            * whenever this button is pressed
            * a thread will be start
            * that thread will submit if anything chaged during the session for editing his/her profile
            *
            *
            * */
            @Override
            public void onClick(View view) {

                String []date= bday.getText().toString().split("-");
                int flag=0;
                if (date.length==3) {
                    if (Integer.parseInt(date[0]) < 1900 || Integer.parseInt(date[0]) > 2019) {
                        flag = 1;
                    }
                    if (Integer.parseInt(date[1]) < 1 || Integer.parseInt(date[1]) > 12) {
                        flag = 1;
                    }
                    if (Integer.parseInt(date[2].trim()) < 1 || Integer.parseInt(date[2].trim()) > 31) {
                        flag = 1;
                    }
                }
                else {
                    flag=1;
                }
                if ( (flag==0 )&& pass.getText().toString().matches(confirmpass.getText().toString())) {

                    Thread t1 = new Thread(new EditProfile.ServerThread());
                    t1.start();
                    try {
                        t1.join();/*
                        thread waited for child thread
                        */
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (msg2.contains("200")) {
                        Intent intent = new Intent(EditProfile.this, MyProfile.class);
                        startActivity(intent);
                        finish();

                    }


                }
                else if(flag==1){

                    bday.setHintTextColor(Color.RED);
                    bday.setText("");
                    bday.setHint("please give valid birthday(yyyy-mm-dd)");
                }
                else {
                    pass.setTextColor(Color.rgb(255, 0, 0));
                    confirmpass.setTextColor(Color.rgb(255, 0, 0));
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(EditProfile.this, MyProfile.class);
        startActivity(intent);
        finish();
        return true;
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                Socket socket = new Socket(FirstPage.ip, FirstPage.port);
                /*
                 *
                 * this sending message manage a protocol
                 * where messsagetype will declare what type of message it is
                 * here message type is myprofile
                 * server will understand what should do after getting the message
                 * username and password are send for authentication and track the user
                 *
                 * */
                String msgrcv = "messagetype=myprofile#";
                msgrcv += "id=" + Login.userid;
                msgrcv += "#password=" + Login.userpassword;
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.write(msgrcv.getBytes("UTF8"));
                out.flush();
                byte[] b = new byte[5164];
                /*
                 *
                 *  read message from the server which will direct the message to the layout for quiz
                 * */
                in.read(b);
                msg1 = "";
                msg1 = new String(b, StandardCharsets.UTF_8);

                in.close();
                out.close();
                socket.close();

            } catch (UnknownHostException ex) {

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    class ServerThread implements Runnable {

        @Override
        public void run() {
            try {
                socket = new Socket(FirstPage.ip, FirstPage.port);

                /*
                *  this message for submitting the changes to the server
                *
                * */
                String msgsend = "messagetype=editprofile#";
                msgsend += "name=" + username.getText().toString();
                msgsend += "#email=" + email_id.getText().toString();
                if(pass.getText().toString().isEmpty()){
                    msgsend += "#password=" + Login.userpassword;
                }
                else {
                    msgsend+="#password=" + pass.getText().toString();
                }
                msgsend += "#birthdate=" + bday.getText().toString().trim();
                msgsend += "#location="+ location1.getText().toString();
                msgsend += "#userid=" + Login.userid;

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.write(msgsend.getBytes("UTF8"));
                out.flush();
                byte[] b = new byte[5164];
                in.read(b);
                msg2 = "";
                msg2 = new String(b, StandardCharsets.UTF_8);
                in.close();
                out.close();
                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
