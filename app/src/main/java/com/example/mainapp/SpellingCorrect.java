package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SpellingCorrect extends AppCompatActivity {
    private EditText wrongline;
    private TextView correctText;
    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spelling_correct);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button submit = (Button) findViewById(R.id.submit);
        wrongline = (EditText) findViewById(R.id.wrongline);
        wrongline.setBackgroundColor(Color.WHITE);
        correctText = (TextView) findViewById(R.id.correctText);
        wrongline.setTextSize(20);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread t1 = new Thread(new ClientThread());
                t1.start();
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                correctText.setTextSize(20);

                String[] tokens = msg.split("#");
                if (tokens[0].contains("200")) {
                    if (tokens.length > 1) {
                        correctText.setText(tokens[1]);
                        FirstPage.data[FirstPage.count] = wrongline.getText().toString() + "#" + msg;
                        FirstPage.count++;
                    } else {
                        correctText.setText(" Server has send the message ");
                    }
                }


            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(SpellingCorrect.this, Options.class);
        startActivity(intent);
        finish();
        return true;
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {

                Socket socket = new Socket(FirstPage.ip, FirstPage.port);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String sendmessage = "messagetype=spelling#";


                sendmessage += wrongline.getText().toString();


                out.write(sendmessage.getBytes("UTF8"));
                out.flush();
                byte[] b = new byte[5164];
                in.read(b);


                msg = new String(b, StandardCharsets.UTF_8);
                in.close();
                out.close();
                socket.close();


                File file = new File("state.txt");

                BufferedWriter bout = new BufferedWriter(new FileWriter("state.txt", true));

                String wrong = wrongline.getText().toString();
                String[] tokens = wrongline.getText().toString().split(" ");
                int count1 = tokens.length;

                tokens = msg.split(" ");
                int count2 = tokens.length;

                bout.write(wrong + "#" + msg + "#" + count1 + "#" + count2);

                bout.close();


            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {


                Socket socket = new Socket(FirstPage.ip, FirstPage.port);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String sendmessage = "messagetype=state#";


                FirstPage.data[FirstPage.count] = wrongline.getText().toString() + "#" + msg;


                out.write((sendmessage + FirstPage.data[FirstPage.count]).getBytes("UTF8"));
                out.flush();
                byte[] b = new byte[5164];
                in.read(b);

                String msg2;
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
