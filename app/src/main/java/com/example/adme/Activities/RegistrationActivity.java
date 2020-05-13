package com.example.adme.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.adme.R;

public class RegistrationActivity extends AppCompatActivity {

    private Button reg_continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeFields();


        reg_continue_btn.setOnClickListener(v -> {

            startActivity(new Intent(RegistrationActivity.this, SecondRegistrationActivity.class));

        });
    }

    private void initializeFields() {
        reg_continue_btn = findViewById(R.id.reg_continue_btn);
    }
}
