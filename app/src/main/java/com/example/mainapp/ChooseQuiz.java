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

public class ChooseQuiz extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton[] radioButtons;
    private TextView question;
    private TextView answer;
    private Button submit;
    private String msg;
    private String str;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_quiz);getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            int cur=FirstPage.nowChoosebest;
            FirstPage.nowChoosebest+=1;


            int i=(cur-1)*8+1;
            question.setText(tokens[i]+". "+tokens[i+1]);
            radioButtons[0].setText(tokens[i+2]);
            radioButtons[1].setText(tokens[i+3]);
            radioButtons[2].setText(tokens[i+4]);
            radioButtons[3].setText(tokens[i+5]);
            str=tokens[i+6];

        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int select=radioGroup.getCheckedRadioButtonId();
                if(select!=-1){
                    next.setEnabled(true);
                    RadioButton selectbutton=(RadioButton) findViewById(select);
                    if(str.equalsIgnoreCase(selectbutton.getText().toString())){
                        answer.setTextColor(Color.GREEN);
                        answer.setTextSize(20);
                        answer.setText(str+"  is correct ");
                    }
                    else {
                        answer.setTextColor(Color.RED);
                        answer.setTextSize(15);
                        answer.setText(selectbutton.getText().toString()+"  is not correct answer\n Correct answer is  "+str);
                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseQuiz.this,ChooseQuiz.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ChooseQuiz.this, Quiz.class);
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
                String sendmessage = "messagetype=synonymquiz#";
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
}
