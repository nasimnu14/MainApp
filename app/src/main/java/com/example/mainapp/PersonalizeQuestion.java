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
    /**
     * first initialize the items that will be used in the choose quiz page.
     * they are initialized private so that other page can't be able to use the items.
     * text views are used for showing ques and answer
     * edit text is used for submitting ans
     * check button is for checking solution
     * submit button is for submitting ans
     * next button is for showing next ques
     * the strings are used for string parsing
     */
    private String msg;
    private Button submit;
    private Button next;
    private Button check;
    private EditText answertext;
    private TextView question;
    private TextView answer;
    private String str;
    private String questionno;
    private String iscorrect;
    private TextView notify;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * all the items are connected with the items in the xml page
         * */

        submit=(Button) findViewById(R.id.submit);
        next=(Button) findViewById(R.id.next);
        next.setEnabled(false);
        check=(Button) findViewById(R.id.check);
        check.setEnabled(false);
        submit.setEnabled(false);

        answertext=(EditText) findViewById(R.id.answertext);
        answertext.setEnabled(false);
        question=(TextView) findViewById(R.id.question);
        answer=(TextView) findViewById(R.id.answer);
        notify = (TextView) findViewById(R.id.notifiy);

        Thread t1=new Thread(new ClientThread());
        t1.start();
        try{
            t1.join();
        }
        catch (Exception ecx){

        }

        /**
         * new thread is opened to gain question and answer from the server
         * the message sent by server are splited by character '#'
         * 200 message is sent by the server for correct message
         * questions are set in the text view
         * */

        String []tokens=msg.split("#");
        if(tokens[0].contains("200")) {
            question.setText(tokens[1] + ")" + tokens[2]);
            str=tokens[3].trim();
            if(tokens[4].contains("1"))
            {
                notify.setText("(You have got this incorrect last time.)");
                notify.setTextColor(Color.RED);

            }else {

                notify.setTextColor(Color.BLUE);
            }
            questionno=tokens[1];
            submit.setEnabled(true);
            check.setEnabled(true);
            answertext.setEnabled(true);

        }
        submit.setOnClickListener(new View.OnClickListener() {
            /**
             * this function is for when submit button is clicked
             * it will take what answer is written and match with the correct answer
             * if the answer is correct it will enable the next button and disable the next and submit button
             * if the answer is wrong then it will show by the text view answer
             * new thread is created for submitting the information to the  server
             * */
            @Override
            public void onClick(View v) {
                if(answertext.getText().toString().equalsIgnoreCase(str)){
                    answer.setTextColor(Color.GREEN);
                    answer.setText("Correct Answer");
                    iscorrect="true";
                    next.setEnabled(true);
                    check.setEnabled(false);
                    submit.setEnabled(false);
                }
                else {
                    answer.setTextColor(Color.RED);
                    answer.setText("Incorrect");
                    iscorrect="false";

                }
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            /**
             * this function will work when the check solution button is clicked
             * this button will show the  correct answer
             * then it will disable the submit and check button and enable the next button
             * */
            @Override
            public void onClick(View v) {
                answer.setTextColor(Color.BLUE);
                answer.setText(str);
                iscorrect="false";
                submit.setEnabled(false);
                next.setEnabled(true);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            /**
             * this function will work when the next button is clicked
             * it will create a new thread to gain next ques from the server
             * then it will go on the next question page
             * */
            @Override
            public void onClick(View v) {
                Thread t1=new Thread(new NextThread());
                t1.start();
                try{
                    t1.join();
                }
                catch (Exception ex){

                }
                Intent intent=new Intent(PersonalizeQuestion.this,PersonalizeQuestion.class);
                startActivity(intent);
                finish();
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp() {
        /**
         * this function is for the default back button of a android app
         * by this button it will go to the previous quiz page
         * */
        Intent intent = new Intent(PersonalizeQuestion.this, Quiz.class);
        startActivity(intent);
        finish();
        return true;
    }
    class NextThread implements  Runnable{
        /*
         *  this message wiil tell the server to save whether the user is correct or wrong
         * and server will make personalise question using this data
         *
         * */
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
                /*
                 *
                 * this sending message manage a protocol
                 * where messsagetype will declare what type of message it is
                 * here message type is choosequiz
                 * server will understand what should do after getting the message
                 *
                 * */
                String sendmessage = "messagetype=personalquiz#";
                sendmessage += "username=" + Login.userid;
                sendmessage += "#password=" + Login.userpassword;

                out.write(sendmessage.getBytes("UTF8"));
                out.flush();
                byte[] b = new byte[5164];
                /*
                 *
                 *  read message from the server which will direct the message to the layout for quiz
                 * */

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
