package com.example.mainapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
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
    private TextView random;
    private TextView grammarquiz;
    //private ProgressBar progressBlank;
    private ProgressBar progessChoose;
    private ProgressBar progressIdioms;
    private ProgressBar progressSynonym;
    private ProgressBar progressPersonal;
    private String msg = "";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        blankquiz = (TextView) findViewById(R.id.blankpercentage);
        choosequiz = (TextView) findViewById(R.id.choosepercentage);
        idiomsquiz = (TextView) findViewById(R.id.idiomspercentage);
        synquiz = (TextView) findViewById(R.id.synonympercentage);
        personquiz = (TextView) findViewById(R.id.personalpercentage);

        ProgressBar progressBlank=(ProgressBar) findViewById(R.id.progressblank);
        progessChoose=(ProgressBar) findViewById(R.id.progresschoose);
        progressIdioms=(ProgressBar) findViewById(R.id.progressidioms);
        progressSynonym=(ProgressBar) findViewById(R.id.progresssynonym);
        progressPersonal=(ProgressBar) findViewById(R.id.progresspersonal);
        random=(TextView) findViewById(R.id.fstTxt);



        Thread t1 = new Thread(new Statistics.ClientThread());
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {

        }

        String []tokens=msg.split("#");

        if(tokens[0].contains("200")){

            try {
                blankquiz.setText(tokens[1] + "/" + tokens[2]);
                choosequiz.setText(tokens[3] + "/" + tokens[4]);
                idiomsquiz.setText(tokens[5] + "/" + tokens[6]);
                synquiz.setText(tokens[7] + "/" + tokens[8]);
                personquiz.setText(tokens[9] + "/" + tokens[10]);

            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        int blankpro = Integer.parseInt(tokens[1].trim());
        int blankmax = Integer.parseInt(tokens[2].trim());

        int choosepro = Integer.parseInt(tokens[3].trim());
        int choosemax = Integer.parseInt(tokens[4].trim());

        int idiomspro = Integer.parseInt(tokens[5].trim());
        int idiomsmax = Integer.parseInt(tokens[6].trim());

        int synpro = Integer.parseInt(tokens[7].trim());
        int synmax = Integer.parseInt(tokens[8].trim());

        int personpro = Integer.parseInt(tokens[9].trim());
        int personmax = Integer.parseInt(tokens[10].trim());
        progressBlank.setMax(blankmax);
        progressBlank.setProgress(blankpro);

        progessChoose.setMax(choosemax);
        progessChoose.setProgress(choosepro);

        progressIdioms.setMax(idiomsmax);
        progressIdioms.setProgress(idiomspro);

        progressSynonym.setMax(synmax);
        progressSynonym.setProgress(synpro);

        progressPersonal.setMax(personmax);
        progressPersonal.setProgress(personpro);
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
