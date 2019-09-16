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
import java.nio.charset.StandardCharsets;

public class Statistics extends AppCompatActivity {
    private TextView blankquiz;
    private TextView choosequiz;
    private TextView idiomsquiz;
    private TextView synquiz;
    private TextView personquiz;
    private TextView grammarquiz;
    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        blankquiz = (TextView) findViewById(R.id.blankquiz);
        choosequiz = (TextView) findViewById(R.id.choosequiz);
        idiomsquiz = (TextView) findViewById(R.id.idiomquiz);
        synquiz = (TextView) findViewById(R.id.synonymquiz);
        personquiz = (TextView) findViewById(R.id.personalquiz);
        grammarquiz = (TextView) findViewById(R.id.history);

        Thread t1 = new Thread(new Statistics.ClientThread());
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {

        }

        String []tokens=msg.split("#");
        if(tokens[0].contains("200")){
            blankquiz.setText("Fill in the blanks: "+tokens[1]+"%");
            choosequiz.setText("Choose the best answer: "+tokens[2]+"%");
            idiomsquiz.setText("Phrase and Idioms: "+tokens[3]+"%");
            synquiz.setText("Synonym and Antonym: "+tokens[4]+"%");
            personquiz.setText("Personalize Question: "+tokens[5]+"%");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(Statistics.this, MyProfile.class);
        startActivity(intent);
        finish();
        return true;
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                Socket socket = new Socket(FirstPage.ip, FirstPage.port);
                String messagesend = "messagetype=statistics#";
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
