package com.example.adme.Activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.adme.Activities.ui.home.HomeFragment;
import com.example.adme.Activities.ui.income.IncomeFragment;
import com.example.adme.Activities.ui.leaderboard.LeaderBoardFragment;
import com.example.adme.Activities.ui.profile.ProfileFragment;
import com.example.adme.Activities.ui.today.TodayBottomDetailsFragment;
import com.example.adme.Activities.ui.today.TodayFragment;
import com.example.adme.Helpers.FirebaseUtilClass;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

public class LandingActivity extends AppCompatActivity {

    private static final String TAG = "LandingActivity";

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private NavController navController;
    private String label = null;
    private boolean isClient = true;
    private User mCurrentUser;

    public User getmCurrentUser() {
        return mCurrentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentUser = getIntent().getParcelableExtra(FirebaseUtilClass.CURRENT_USER_ID);

        if(isClient){
            setContentView(R.layout.activity_landing_client);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.

            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(navView, navController);
        }else{
            setContentView(R.layout.activity_landing);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.

            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(navView, navController);
        }


    }




    @Override
    public void onBackPressed()
    {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

        if((currentFragment instanceof TodayFragment) || (currentFragment instanceof HomeFragment) ){
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
            {
                if(Build.VERSION.SDK_INT>=16){
                    finishAffinity();
                } else{
                    finish();
                    System.exit(0);
                }
                return;
            }
            else { Toast.makeText(getBaseContext(), "Tap back again to exit", Toast.LENGTH_SHORT).show(); }

            mBackPressed = System.currentTimeMillis();
        }else{
            super.onBackPressed();
        }

    }
}
