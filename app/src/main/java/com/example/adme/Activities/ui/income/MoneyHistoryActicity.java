package com.example.adme.Activities.ui.income;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.R;

public class MoneyHistoryActicity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MoneyHistoryAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money);
        initializeFields();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initializeFields() {
        img_back =  findViewById(R.id.img_back);
        recyclerView = findViewById(R.id.rv_money);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MoneyHistoryAdapter();
        recyclerView.setAdapter(adapter);

        img_back.setOnClickListener(v -> onBackPressed());
    }
}
