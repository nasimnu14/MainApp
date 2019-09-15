package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MyProfile extends AppCompatActivity {
    private TextView name;
    private TextView userid;
    private TextView email;
    private TextView birthday;
    private TextView location;
    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = (TextView) findViewById(R.id.name);
        userid=(TextView) findViewById(R.id.userid);
        email = (TextView) findViewById(R.id.email);

        location = (TextView) findViewById(R.id.location);
        birthday = (TextView) findViewById(R.id.birth);

        Button edit = (Button) findViewById(R.id.editpro);
        Button stat = (Button) findViewById(R.id.statistics);

        Thread t1 = new Thread(new ClientThread());
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {

        }
        String []tokens=msg.split("#");
        if(tokens[0].contains("200")){
            name.setText("Name: "+tokens[1]);
            userid.setText("Id: "+tokens[2]);
            email.setText("Email: "+tokens[3]);
            location.setText("Location: "+tokens[4]);
            birthday.setText("BirthDay: "+tokens[5]);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfile.this, EditProfile.class);
                startActivity(intent);
                finish();
            }
        });

        stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfile.this, Statistics.class);
                startActivity(intent);
                finish();
            }
        });




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
                messagesend += "id=" + Login.userid;
                messagesend += "#password=" + Login.userpassword;
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.write(messagesend.getBytes("UTF8"));
                out.flush();
                byte[] b = new byte[5164];
                in.read(b);
                msg = "";
                msg = new String(b, StandardCharsets.UTF_8);

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

