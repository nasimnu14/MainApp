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
    /**
     * first initialize the items that will be used in the choose quiz page.
     * they are initialized private so that other page can't be able to use the items.
     * radio group is used for the options
     * text views are used for showing ques and answer
     * edit text is used for submitting ans
     * check button is for checking solution
     * submit button is for submitting ans
     * next button is for showing next ques
     * the strings are used for string parsing
     */
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

        /**
         * all the items are connected with the items in the xml page
         * */

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

        /**
         * new thread is opened to gain question and answer from the server
         * the message sent by server are splited by character '#'
         * 200 message is sent by the server for correct message
         * questions and options are set in the radio button and question text view
         * */

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
            /**
             * this function is for when submit button is clicked
             * it will take which option is selected and match with the correct answer
             * if the answer is correct it will enable the next button and disable the next and submit button
             * if the answer is wrong then it will show by the text view answer
             * new thread is created for submitting the information to the  server
             * */
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
            /**
             * this function will work when the check solution button is clicked
             * this button will show the  correct answer
             * then it will disable the submit and check button and enable the next button
             * */
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
                Intent intent=new Intent(IdiomsQuiz.this,IdiomsQuiz.class);
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

                /*
                 *
                 * this sending message manage a protocol
                 * where messsagetype will declare what type of message it is
                 * here message type is choosequiz
                 * server will understand what should do after getting the message
                 *
                 * */

                String sendmessage = "messagetype=idiomquiz#";
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
