package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class PersonalizeQuestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(PersonalizeQuestion.this, Quiz.class);
        startActivity(intent);
        finish();
        return true;
    }
}
