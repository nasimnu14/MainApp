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

public class PersonalizeQuestion extends AppCompatActivity {
    private String msg;
    private Button submit;
    private Button next;
    private EditText answertext;
    private TextView question;
    private TextView answer;
    private String str;
    private String questionno;
    private String iscorrect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit=(Button) findViewById(R.id.submit);
        next=(Button) findViewById(R.id.next);
        next.setEnabled(false);
        question=(TextView) findViewById(R.id.question);
        answer=(TextView) findViewById(R.id.answer);
        answertext=(EditText) findViewById(R.id.answertext);

        Thread t1=new Thread(new ClientThread());
        t1.start();
        try{
            t1.join();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String []tokens=msg.split("#");
        if(tokens[0].equalsIgnoreCase("200")){
            questionno=tokens[1];
            question.setText(tokens[1]+"."+tokens[2]);
            str=tokens[3];
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (str.equalsIgnoreCase(answertext.getText().toString())){
                    answer.setTextColor(Color.RED);
                    answer.setText("Correct");
                    iscorrect="true";
                    submit.setEnabled(false);
                    next.setEnabled(true);
                }
                else {
                    answer.setTextColor(Color.RED);
                    answer.setText("Wrong answer.Try again");
                    iscorrect="false";
                }
                Thread t2=new Thread(new NextThread());
                t2.start();
                try{
                    t2.join();
                }
                catch (Exception ex){

                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalizeQuestion.this, PersonalizeQuestion.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(PersonalizeQuestion.this, Quiz.class);
        startActivity(intent);
        finish();
        return true;
    }
    class NextThread implements  Runnable{
        @Override
        public void run(){
            try{
                Socket socket=new Socket(FirstPage.ip,FirstPage.port);
                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                String sendmessage="";
                sendmessage="messagetype=personalresult#";
                sendmessage+="username="+Login.userid;
                sendmessage+="#password="+Login.userpassword;
                sendmessage+="#questionno="+questionno;
                sendmessage+="#iscorrect="+iscorrect;
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
    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                Socket socket = new Socket(FirstPage.ip, FirstPage.port);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String sendmessage = "messagetype=personalquiz#";
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
