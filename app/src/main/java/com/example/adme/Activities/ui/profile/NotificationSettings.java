package com.example.adme.Activities.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.adme.R;

public class NotificationSettings extends AppCompatActivity {

    Switch aSwitch_notification, aSwitch_massage, aSwitch_alert;
    ConstraintLayout backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activaty_notification_settings);


        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        aSwitch_alert = findViewById(R.id.switch3);
        aSwitch_massage = findViewById(R.id.switch2);
        aSwitch_notification = findViewById(R.id.switch1);

        if (aSwitch_notification.isChecked()){
            //notification on / off
        }
        if (aSwitch_massage.isChecked()){
            // massage option on
        }
        if (aSwitch_alert.isChecked()){
            //alert option is on
        }

    }
}
