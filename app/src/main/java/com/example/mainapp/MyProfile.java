package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyProfile extends AppCompatActivity {
    private TextView name;
    private TextView email;
    private TextView phone;
    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);

        Thread t1 = new Thread(new ClientThread());
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {

        }

        String[] tokens = msg.split("#");
        if (tokens.length > 1) {
            name.append(":   " + tokens[1]);
            email.append("  " + tokens[2]);
            phone.setText(" Phone Number is: " + tokens[3]);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(MyProfile.this, Options.class);
        startActivity(intent);
        finish();
        return true;
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                Socket socket = new Socket(FirstPage.ip, FirstPage.port);
                String messagesend = "messagetype=myprofile#";
                messagesend += "email=" + Login.userid;
                messagesend += "#password=" + Login.userpassword;
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(messagesend);
                out.flush();
                msg = in.readUTF();
                in.close();
                out.close();
                socket.close();

            } catch (UnknownHostException ex) {

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }
}

