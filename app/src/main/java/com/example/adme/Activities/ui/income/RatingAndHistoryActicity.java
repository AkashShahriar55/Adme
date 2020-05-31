package com.example.adme.Activities.ui.income;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.ui.leaderboard.LeaderBoardAdapter;
import com.example.adme.R;

import java.util.Objects;

public class RatingAndHistoryActicity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RatingHistoryAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rating);
        initializeFields();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initializeFields() {
        img_back = (ImageView) findViewById(R.id.img_back);
        recyclerView = (RecyclerView) findViewById(R.id.rv_rating);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RatingHistoryAdapter();
        recyclerView.setAdapter(adapter);

        img_back.setOnClickListener(v -> onBackPressed());
    }
}
