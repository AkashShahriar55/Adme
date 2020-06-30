package com.example.adme.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.adme.Activities.ui.home.HomeFragment;
import com.example.adme.Activities.ui.home.HomeViewModel;
import com.example.adme.Activities.ui.income.IncomeFragment;
import com.example.adme.Activities.ui.leaderboard.LeaderBoardFragment;
import com.example.adme.Activities.ui.profile.ProfileFragment;
import com.example.adme.Activities.ui.servicehistory.ServiceHistoryFragment;
import com.example.adme.Activities.ui.today.Notification_Fragment;
import com.example.adme.Activities.ui.today.QuotationDetails;
import com.example.adme.Activities.ui.today.TodayFragment;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Constants;
import com.example.adme.Helpers.User;
import com.example.adme.Architecture.UserDataModel;
import com.example.adme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctionsException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

public class LandingActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "LandingActivity";

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private NavController navController;
    private String label = null;
    private boolean isClient = false;
    private User mCurrentUser;
    private FirebaseUtilClass firebaseUtilClass = new FirebaseUtilClass();

    final Fragment fragment1 = new TodayFragment();
    final Fragment fragment2 = new LeaderBoardFragment();
    final Fragment fragment3 = new IncomeFragment();
    final Fragment fragment4 = new ProfileFragment();
    final Fragment fragment5 = new HomeFragment();
    final Fragment fragment6 = new ServiceHistoryFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;



    public boolean isClient() {
        return isClient;
    }

    public User getmCurrentUser() {
        return mCurrentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMode();
        mCurrentUser = getIntent().getParcelableExtra(FirebaseUtilClass.CURRENT_USER_ID);


        if(isClient){
            setContentView(R.layout.activity_landing_client);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            navView.setOnNavigationItemSelectedListener(LandingActivity.this);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.nav_host_fragment, fragment5, "5");
                    ft.add(R.id.nav_host_fragment, fragment6, "6").hide(fragment6);
                    ft.add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3);
                    ft.add(R.id.nav_host_fragment, fragment4, "4").hide(fragment4);
                    ft.commit();
                    active = fragment5;
                }
            });

        }else{
            setContentView(R.layout.activity_landing);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            navView.setOnNavigationItemSelectedListener(LandingActivity.this);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").commit();
                    fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(fragment2).commit();
                    fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3).commit();
                    fm.beginTransaction().add(R.id.nav_host_fragment, fragment4, "4").hide(fragment4).commit();
                    active = fragment1;
                }
            });

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Constants.REQUEST_CODE_ADD_SERVICE_ACTIVITY:

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public boolean isTodayVisible() {
        return active == fragment1;
    }

    public void changeMode(){
        if(isClient){
            isClient = false;
            setMode(false);
        } else {
            isClient = true;
            setMode(true);
        }
//        recreate();
        finish();
        startActivity(getIntent());
    }

    public void setMode(boolean mode){
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putBoolean("isClient", mode);
        editor.apply();
    }

    public void getMode(){
        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        if(preferences.getBoolean("isClient",true)){
            isClient = true;
        } else {
            isClient = false;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = fm.findFragmentById(R.id.nav_host_fragment);

        if(currentFragment instanceof QuotationDetails || currentFragment instanceof Notification_Fragment){
            fm.beginTransaction().remove(currentFragment).commit();
            Log.d(TAG, "onBackPressed: appointmentFragment");
        } else if (currentFragment instanceof TodayFragment ||
                currentFragment instanceof LeaderBoardFragment ||
                currentFragment instanceof IncomeFragment ||
                currentFragment instanceof ProfileFragment ||
                currentFragment instanceof HomeFragment ||
                currentFragment instanceof ServiceHistoryFragment) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                if(Build.VERSION.SDK_INT>=16){
                    finishAffinity();
                } else {
                    finish();
                    System.exit(0);
                }
                return;
            } else {
                Toast.makeText(getBaseContext(), "Tap back again to exit", Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }  else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: "+item.getTitle());
        switch (item.getItemId()) {
            case R.id.navigation_today: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                        TodayFragment fragment = (TodayFragment)fm.findFragmentByTag("1");
                        fragment.updateView();
                    }
                });
                return true;
            }
            case R.id.navigation_leaderboard: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fm.beginTransaction().hide(active).show(fragment2).commit();
                        active = fragment2;
                        LeaderBoardFragment fragment = (LeaderBoardFragment)fm.findFragmentByTag("2");
                        fragment.updateView();
                    }
                });
                return true;
            }
            case R.id.navigation_income: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fm.beginTransaction().hide(active).show(fragment3).commit();
                        active = fragment3;
                        IncomeFragment fragment = (IncomeFragment)fm.findFragmentByTag("3");
                        fragment.updateView();
                    }
                });
                return true;
            }
            case R.id.navigation_profile: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fm.beginTransaction().hide(active).show(fragment4).commit();
                        active = fragment4;
                    }
                });
                return true;
            }
            case R.id.navigation_home: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("akash_debug", "navigation selected: ");
                        fm.beginTransaction().hide(active).show(fragment5).commit();
                        active = fragment5;
                    }
                });
                return true;
            }
            case R.id.navigation_service_history: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fm.beginTransaction().hide(active).show(fragment6).commit();
                        active = fragment6;
                    }
                });
                return true;
            }
        }
        return false;
    }
}