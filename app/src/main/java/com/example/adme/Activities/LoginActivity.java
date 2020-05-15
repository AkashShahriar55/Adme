package com.example.adme.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.adme.R;

public class LoginActivity extends AppCompatActivity {

    private TextView txt_create_account;
    private Button login_skip_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeFields();


        //Goto Registration page when click on create an account text
        txt_create_account.setOnClickListener(v -> {

            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));

        });

        login_skip_btn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, LandingActivity.class)));
    }

    private void initializeFields() {
        txt_create_account = findViewById(R.id.txt_create_account);
        login_skip_btn = findViewById(R.id.login_skip_btn);
    }
}