package com.cookietech.adme.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseUtilClass firebaseUtilClass = new FirebaseUtilClass();
    private SharedPreferences firstUsePreferences;
    private boolean isLocationSettingShowed = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView bannerLogo = findViewById(R.id.banner_logo);






        firstUsePreferences = getSharedPreferences(String.valueOf(R.string.SHARED_PREFERENCE_FIRST_USE),MODE_PRIVATE);
        isLocationSettingShowed = firstUsePreferences.getBoolean(String.valueOf(R.string.SP_IS_LOCATION_SETTING_SHOWED),false);


        new Thread(() -> {
            try {
                Thread.sleep(1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(firebaseUtilClass.checkIfAlreadyLoggedIn()){
                            startLandingActivity();

                        }else{
                            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,bannerLogo,"banner_logo");
                            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
                            loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginActivity,activityOptionsCompat.toBundle());
                        }

                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


    }

    private void startAccessLocationActivity() {
        Intent intent = new Intent(MainActivity.this, AccessLocationActivity.class);
        startActivity(intent);
    }

    private void startLandingActivity() {
        Intent intent = new Intent(MainActivity.this, LandingActivity.class);
        startActivity(intent);
    }
}
