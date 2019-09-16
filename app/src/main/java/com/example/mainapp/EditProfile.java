package com.example.mainapp;

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
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {

        }
        String []tokens=msg1.split("#");
        if(tokens[0].contains("200")){
            username.setText(tokens[1]);
            email_id.setText(tokens[3]);
            location1.setText(tokens[4]);
            bday.setText(tokens[5]);
        }

        edited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass.getText().toString().matches(confirmpass.getText().toString())) {

                    Thread t1 = new Thread(new EditProfile.ServerThread());
                    t1.start();
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (msg2.contains("200")) {
                        Intent intent = new Intent(EditProfile.this, MyProfile.class);
                        startActivity(intent);
                        finish();

                    }


                } else {
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
                String msgrcv = "messagetype=myprofile#";
                msgrcv += "id=" + Login.userid;
                msgrcv += "#password=" + Login.userpassword;
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.write(msgrcv.getBytes("UTF8"));
                out.flush();
                byte[] b = new byte[5164];
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
                String msgsend = "messagetype=editprofile#";
                msgsend += "name=" + username.getText().toString();
                msgsend += "#email=" + email_id.getText().toString();
                msgsend += "#password=" + pass.getText().toString();
                msgsend += "#birthdate=" + bday.getText().toString();
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
