package com.example.mainapp;
/*
*
*  This is the first page for our app
* This page will stay 1 second after launching
*
* */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    public final static int wait = 1000;  // this wait is for time wait

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                 *After one second this page will move to the First page
                 *
                 * */
                Intent intent = new Intent(MainActivity.this, FirstPage.class);
                startActivity(intent);

                finish();
            }
        }, wait);


    }
}
