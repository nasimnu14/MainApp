package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/*
*  this option page is the main service provider page
*
* */
public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button spelling = (Button) findViewById(R.id.spelling);
        Button quiz = (Button) findViewById(R.id.quiz);
        Button profile = (Button) findViewById(R.id.profile);
        Button exit = (Button) findViewById(R.id.exit);
        /*
        * this is used for grammatical check error
        * though the buttton name is spelling it is used for grammatical error
        * spelling is  used first
        * */
        spelling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Options.this, SpellingCorrect.class);
                startActivity(intent);
                finish();
            }
        });
        /*
        *  this is used for quiz
        * quiz will be directed by this button
        * every type of quiz will be shown
        *
        * */
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Options.this, Quiz.class);
                startActivity(intent);
                finish();
            }
        });
        /*
        *  this is used for viewing profile
        * */
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Options.this, MyProfile.class);
                startActivity(intent);
                finish();
            }
        });
        /*
        *  Exit from the app
        * */

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    /*

     going back to the previous page
    *
    * */
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(Options.this, Login.class);
        startActivity(intent);
        finish();
        return true;
    }
}
