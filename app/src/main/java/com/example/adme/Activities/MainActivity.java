package com.example.adme.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.adme.R;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView bannerLogo = findViewById(R.id.banner_logo);

        mPreferences = getSharedPreferences(String.valueOf(R.string.SHARED_PREFERENCE_FIRST_USE),MODE_PRIVATE);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,bannerLogo,"banner_logo");
                            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
                            loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginActivity,activityOptionsCompat.toBundle());
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
