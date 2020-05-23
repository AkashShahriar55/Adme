package com.example.adme.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.adme.Activities.ui.income.IncomeFragment;
import com.example.adme.Activities.ui.leaderboard.LeaderBoardFragment;
import com.example.adme.Activities.ui.profile.ProfileFragment;
import com.example.adme.Activities.ui.today.TodayBottomDetailsFragment;
import com.example.adme.Activities.ui.today.TodayFragment;
import com.example.adme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class LandingActivity extends AppCompatActivity {

    private static final String FRAGMENT_TODAY = "TODAY";
    private static final String FRAGMENT_GAMES = "Lead";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

    }




}
