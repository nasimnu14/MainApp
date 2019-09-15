package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SignUp extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText password;
    private EditText confirmpassword;
    private Socket socket;
    private TextView create;
    private String msg = "";
    private EditText userid;
    private ListView gender;
    private EditText bdate;
    private EditText location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String []Gender={"Male", "Female","Other"};
        create=(TextView) findViewById(R.id.fstTxt);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        confirmpassword = (EditText) findViewById(R.id.confirmpass);
        userid=(EditText) findViewById(R.id.userid);
        bdate=(EditText) findViewById(R.id.birthdate);
        location=(EditText) findViewById(R.id.location);
        Button register = (Button) findViewById(R.id.login);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userid.setTextColor(Color.BLACK);
                if (password.getText().toString().matches(confirmpassword.getText().toString())) {

                    Thread t1 = new Thread(new ClientThread());
                    t1.start();
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (msg.contains("200")) {
                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        userid.setTextColor(Color.RED);
                       
                    }


                } else {
                    password.setTextColor(Color.rgb(255, 0, 0));
                    confirmpassword.setTextColor(Color.rgb(255, 0, 0));
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(SignUp.this, FirstPage.class);
        startActivity(intent);
        finish();
        return true;
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                socket = new Socket(FirstPage.ip, FirstPage.port);
                String messagesend = "messagetype=signin#";
                messagesend += "name=" + name.getText().toString();
                messagesend += "#email=" + email.getText().toString();
                messagesend += "#phone=" + phone.getText().toString();
                messagesend += "#password=" + password.getText().toString();
                messagesend += "#userid=" + userid.getText().toString();
                messagesend += "#birthdate="+bdate.getText().toString();
                messagesend += "#location="+location.getText().toString();

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

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
