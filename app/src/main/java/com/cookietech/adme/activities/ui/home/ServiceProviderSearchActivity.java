package com.cookietech.adme.activities.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.adme.Helpers.Service;
import com.cookietech.adme.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceProviderSearchActivity extends AppCompatActivity  implements ServiceSearchAdapter.ServiceSearchAdapterListener {
    private static final String TAG = "SearchFragment";
    private RecyclerView search_service_rv;
    private List<Service> serviceProvidersList = new ArrayList<>();
    private ServiceSearchAdapter serviceSearchAdapter;
    private SearchView serviceSearchView;
    HomeViewModel homeViewModel;
    FirebaseFirestore db;
    int waitingTime = 500;
    CountDownTimer cntr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_service);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getQueryResult().observe(this, new Observer<List<Service>>() {
            @Override
            public void onChanged(List<Service> services) {
                Log.d(TAG, services.size()+" onEvent getQueryResult: called "+homeViewModel.getQueryResult().getValue().size());
                serviceProvidersList.clear();
                serviceProvidersList.addAll(services);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        serviceSearchAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        initializeFields();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initializeFields() {
        search_service_rv = findViewById(R.id.rv_service_result);
        serviceSearchAdapter = new ServiceSearchAdapter(this, serviceProvidersList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        search_service_rv.setLayoutManager(mLayoutManager);
        search_service_rv.setItemAnimator(new DefaultItemAnimator());
        search_service_rv.setAdapter(serviceSearchAdapter);

        serviceSearchView = findViewById(R.id.searchView);
        serviceSearchView.setIconifiedByDefault(true);
        serviceSearchView.setFocusable(true);
        serviceSearchView.setIconified(false);
        serviceSearchView.requestFocusFromTouch();

        serviceSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<String> queryList = new ArrayList<String>(Arrays.asList(query.split(" ")));
                homeViewModel.setQueryValue(queryList);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange getQueryResult: "+newText);
                List<String> queryList = new ArrayList<String>(Arrays.asList(newText.split(" ")));

                if(cntr != null){
                    cntr.cancel();
                }
                cntr = new CountDownTimer(waitingTime, 500) {

                    public void onTick(long millisUntilFinished) {
                        Log.d("TIME","seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        homeViewModel.setQueryValue(queryList);
                        Log.d("FINISHED","DONE");
                    }
                };
                cntr.start();
                return false;
            }
        });
    }

    @Override
    public void onServiceProviderSelected(Service serviceProvider) {
        Intent intent = new Intent(this, ServiceProviderDetailsActivity.class);
        intent.putExtra("serviceProviderObject", serviceProvider);
        startActivity(intent);
    }

}
