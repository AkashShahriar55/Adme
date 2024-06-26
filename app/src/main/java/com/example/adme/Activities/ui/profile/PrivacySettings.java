package com.example.adme.Activities.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.adme.R;

public class PrivacySettings extends AppCompatActivity {


    ConstraintLayout backBtn;
    CardView manageProfileBtnLay, changePassBtnLay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);

        manageProfileBtnLay = findViewById(R.id.manageProfileBtnLay);
        changePassBtnLay = findViewById(R.id.changePassBtnLay);


        manageProfileBtnLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrivacySettings.this, ManageProfile.class));
            }
        });

        changePassBtnLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrivacySettings.this, ChangePassword.class));
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

