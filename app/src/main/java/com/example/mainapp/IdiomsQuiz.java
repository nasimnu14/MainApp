package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class IdiomsQuiz extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton[] radioButtons;
    private TextView question;
    private TextView answer;
    private Button submit;
    private Button check;
    private String msg;
    private String str;
    private Button next;
    private String isCorrect;
    private String questionno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idioms_quiz);getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButtons = new RadioButton[4];
        radioButtons[0] = (RadioButton) findViewById(R.id.radioButton1);
        radioButtons[1] = (RadioButton) findViewById(R.id.radioButton2);
        radioButtons[2] = (RadioButton) findViewById(R.id.radioButton3);
        radioButtons[3] = (RadioButton) findViewById(R.id.radioButton4);
        question = (TextView) findViewById(R.id.question);
        answer = (TextView) findViewById(R.id.answer);

        submit = (Button) findViewById(R.id.submit);
        next=(Button) findViewById(R.id.next);
        next.setEnabled(false);
        check=(Button) findViewById(R.id.check);

        Thread t1=new Thread(new ClientThread());

        t1.start();
        try{
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String []tokens=msg.split("#");
        if(tokens[0].contains("200")){
            str=tokens[tokens.length-1];
            int cur=FirstPage.nowIdiom;
            FirstPage.nowIdiom+=1;


            int i=1;
            question.setText(tokens[i]+". "+tokens[i+1]);
            radioButtons[0].setText(tokens[i+2]);
            radioButtons[1].setText(tokens[i+3]);
            radioButtons[2].setText(tokens[i+4]);
            radioButtons[3].setText(tokens[i+5]);
            str=tokens[i+6];
            questionno=tokens[i];

        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int select=radioGroup.getCheckedRadioButtonId();
                if(select!=-1){
                    RadioButton selectbutton=(RadioButton) findViewById(select);
                    if(str.equalsIgnoreCase(selectbutton.getText().toString())){
                        answer.setTextColor(Color.GREEN);
                        answer.setTextSize(20);
                        submit.setEnabled(false);
                        answer.setText("Correct answer");
                        next.setEnabled(true);
                        check.setEnabled(false);
                        isCorrect="true";
                    }
                    else {
                        answer.setTextColor(Color.RED);
                        answer.setTextSize(20);
                        isCorrect="false";
                        answer.setText( "Incorrect");
                    }

                }
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer.setTextColor(Color.BLUE);
                submit.setEnabled(false);
                next.setEnabled(true);
                isCorrect="false";
                answer.setText("Correct answer: "+str);
                check.setEnabled(false);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t1=new Thread(new NextThread());
                t1.start();
                try{
                    t1.join();
                }
                catch (Exception ex){

                }
                Intent intent=new Intent(IdiomsQuiz.this,IdiomsQuiz.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(IdiomsQuiz.this, Quiz.class);
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
                String sendmessage = "messagetype=idiomquiz#";
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
                sendmessage="messagetype=idiomresult#";
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
