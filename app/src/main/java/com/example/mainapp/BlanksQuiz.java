package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class BlanksQuiz extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton[] radioButtons;
    private TextView question;
    private TextView answer;
    private EditText answertext;
    private Button submit;
    private String msg;
    private String str;
    private Button next;
    private String isCorrect;
    private String questionno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blanks_quiz);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question=(TextView) findViewById(R.id.question);
        answertext=(EditText) findViewById((R.id.answertext));
        answer=(TextView) findViewById(R.id.answer);
        submit=(Button) findViewById(R.id.submit);
        next=(Button) findViewById(R.id.next);
        next.setEnabled(false);

        Thread t1=new Thread(new ClientThread());

        t1.start();
        try{
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String []tokens=msg.split("#");
        if(tokens[0].contains("200")){
            questionno=tokens[1];
            question.setText(tokens[1]+". "+tokens[2]);
            str=tokens[3];

        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(answertext.getText().toString().trim().equalsIgnoreCase(str.trim())){
                   answer.setTextColor(Color.GREEN);
                   answer.setText("Correct");
                   next.setEnabled(true);
                   submit.setEnabled(false);
                   isCorrect="true";
               }
               else {
                   answer.setTextColor(Color.RED);
                   answer.setText("Wrong");
                   isCorrect="false";
               }
               Thread t2=new Thread(new NextThread());
               t2.start();
               try{
                   t2.join();
               }
               catch (Exception e){

               }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BlanksQuiz.this,BlanksQuiz.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(BlanksQuiz.this, Quiz.class);
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
                String sendmessage = "messagetype=blankquiz#";
                sendmessage += "username=" + Login.userid;
                sendmessage += "#password=" + Login.userpassword;

                out.write(sendmessage.getBytes("UTF8"));
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
    class NextThread implements  Runnable{
        @Override
        public void run(){
            try{
                Socket socket=new Socket(FirstPage.ip,FirstPage.port);
                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                String sendmessage="";
                sendmessage="messagetype=blankresult#";
                sendmessage+="username="+Login.userid;
                sendmessage+="#password="+Login.userpassword;
                sendmessage+="#questionno="+questionno;
                sendmessage+="#iscorrect="+isCorrect;
                out.write(sendmessage.getBytes("UTF8"));
                out.flush();
                out.close();
                socket.close();
            }
            catch (UnknownHostException ex){


            }
            catch (IOException ex){

            }
        }
    }
}
