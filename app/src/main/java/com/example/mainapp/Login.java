package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
    public static String userid = "";
    public static String userpassword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        signin = (TextView) findViewById(R.id.textView6);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t1 = new Thread(new clientThread());
                t1.start();
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if (msg.contains("200")) {
                    userid = email.getText().toString();
                    userpassword = password.getText().toString();
                    Intent intent = new Intent(Login.this, Options.class);
                    startActivity(intent);
                    finish();
                } else {
                    signin.setTextSize(12);
                    signin.setTextColor(Color.RED);
                    signin.setText("Please enter valid userid and password");
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

        @Override
        public void run() {
            try {


                socket = new Socket(FirstPage.ip, FirstPage.port);


                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
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
