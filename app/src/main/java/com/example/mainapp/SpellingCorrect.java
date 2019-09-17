package com.example.mainapp;

/*
*
* this is our main feature
* the grammar corrector
* Nasim
* The grammmar corrector will work  both on run time and clicking submit button
* */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private String runmsg="";

    private String text="";
    /*
     * these are some private varibale used for layout show
     * */

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

        wrongline.addTextChangedListener(new TextWatcher()
        {   /* this override function is for runtime error correction

            */
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String edittype=mEdit.toString();
                int l=edittype.length();
                if(l>1 && edittype.charAt(l-1)=='.')
                    new Thread(new RuntimeCheck(edittype)).start();


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        /*
         *
         * this button will work for error correction
         *
         * */

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
                   correctText.setText(tokens[1].toString());
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
                /*
                 *
                 * this sending message manage a protocol
                 * where messsagetype will declare what type of message it is
                 * here message type is spelling
                 * server will understand what should do after getting the message
                 * username and password are send for authentication and track the user
                 *
                 * */
                String sendmessage = "messagetype=spelling#";
                sendmessage+=Login.userid+"#";


                sendmessage += wrongline.getText().toString();


                out.write(sendmessage.getBytes("UTF8"));
                out.flush();
                /*
                *  receive correct message from the server
                *
                * */
                byte[] b = new byte[5164];
                in.read(b);


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
    class RuntimeCheck implements Runnable{
        String runtimewrongmessage;

        public RuntimeCheck(String s) {
            runtimewrongmessage=s;
        }



        @Override
        public void run() {
            try {
                Socket socket=new Socket(FirstPage.ip,FirstPage.port);
                DataInputStream in=new DataInputStream(socket.getInputStream());
                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                /*
                 *
                 * this sending message manage a protocol
                 * where messsagetype will declare what type of message it is
                 * here message type is spelling
                 * server will understand what should do after getting the message
                 * username and password are send for authentication and track the user
                 *
                 * */
                String sendmessage="messagetype=spelling#";
                sendmessage+=Login.userid+"#";
                sendmessage+=runtimewrongmessage;
                out.write(sendmessage.getBytes("UTF8"));
                out.flush();
                byte []b=new byte[5164];
                /*
                 *  receive correct message from the server
                 *
                 * */
                in.read(b);
                String runtimemessage=new String(b,StandardCharsets.UTF_8);
                in.close();
                out.close();
                socket.close();
                String []tokens=runtimemessage.split("#");
                if(runtimemessage.contains("200")){
                    correctText.setText(tokens[1]);
                }

            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
