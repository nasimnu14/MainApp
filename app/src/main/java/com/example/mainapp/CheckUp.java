package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class CheckUp extends AppCompatActivity {
    private EditText editText;
    private TextView textView;
    private String text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_up);


    }
    class Clients implements Runnable{

        @Override
        public void run() {
            textView.setText(text);
        }
    }
}
