package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Quiz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button blank = (Button) findViewById(R.id.blank);
        Button antonyms = (Button) findViewById(R.id.antonym);
        Button idioms = (Button) findViewById(R.id.idioms);
        Button choose = (Button) findViewById(R.id.choose);
        Button exit = (Button) findViewById(R.id.exit);

        antonyms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Quiz.this, SynonymandAntonym.class);
                startActivity(intent);
                finish();
            }
        });
        blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Quiz.this, BlanksQuiz.class);
                startActivity(intent);
                finish();
            }
        });
        idioms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Quiz.this, IdiomsQuiz.class);
                startActivity(intent);
                finish();
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Quiz.this, ChooseQuiz.class);
                startActivity(intent);
                finish();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(Quiz.this, Options.class);
        startActivity(intent);
        finish();
        return true;
    }
}
