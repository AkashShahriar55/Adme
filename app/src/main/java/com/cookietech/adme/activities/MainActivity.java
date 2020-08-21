package com.cookietech.adme.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Architecture.MotherViewModel;
import com.cookietech.adme.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseUtilClass firebaseUtilClass = FirebaseUtilClass.getInstance();
    private SharedPreferences firstUsePreferences;
    private boolean isLocationSettingShowed = false;

    private MotherViewModel motherViewModel;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView bannerLogo = findViewById(R.id.banner_logo);

        motherViewModel = new ViewModelProvider(this).get(MotherViewModel.class);




        firstUsePreferences = getSharedPreferences(String.valueOf(R.string.SHARED_PREFERENCE_FIRST_USE),MODE_PRIVATE);
        isLocationSettingShowed = firstUsePreferences.getBoolean(String.valueOf(R.string.SP_IS_LOCATION_SETTING_SHOWED),false);

        Log.d("akash_debug", "onCreate: " + motherViewModel.isAlreadyLoggedIn());


        new Thread(() -> {
            try {
                Thread.sleep(1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(firebaseUtilClass.checkIfAlreadyLoggedIn()){
                            startLandingActivity();
                            //startUserInfoActivity();
                            //startAccessLocationActivity();

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

    private void startUserInfoActivity() {
        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
        startActivity(intent);
    }
}
