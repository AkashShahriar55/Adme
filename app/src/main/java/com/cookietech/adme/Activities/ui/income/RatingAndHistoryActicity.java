package com.cookietech.adme.Activities.ui.income;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.RatingItem;
import com.cookietech.adme.Helpers.User;
import com.cookietech.adme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RatingAndHistoryActicity extends AppCompatActivity {
    private static final String TAG = "RatingAndHstoryActicity";
    private RecyclerView recyclerView;
    private RatingHistoryAdapter ratingHistoryAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView img_back;
    private List<RatingItem> ratingItemList = new ArrayList<>();
    IncomeViewModel incomeViewModel;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rating);
        incomeViewModel = new ViewModelProvider(this).get(IncomeViewModel.class);
        incomeViewModel.getUserData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
                List<String> serviceIdList = new ArrayList<>();
                for(Map<String,String> reference : user.getService_reference()){
                    serviceIdList.add(reference.get(FirebaseUtilClass.ENTRY_SERVICE_REFERENCE));
                }
                if(serviceIdList.size()>0){
                    incomeViewModel.setAllRating_list(serviceIdList);
                }
            }
        });
        incomeViewModel.getRating_list().observe(this, new Observer<List<RatingItem>>() {
                    @Override
                    public void onChanged(List<RatingItem> ratingItems) {
                        Log.d(TAG, " onEvent getRating_list: "+ratingItems.size());
                        ratingItemList.clear();
                        ratingItemList.addAll(ratingItems);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ratingHistoryAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

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
        ratingHistoryAdapter = new RatingHistoryAdapter(this, ratingItemList);
        recyclerView.setAdapter(ratingHistoryAdapter);

        img_back.setOnClickListener(v -> onBackPressed());

    }
}
