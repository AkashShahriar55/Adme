package com.cookietech.adme.Activities.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cookietech.adme.Helpers.Service;
import com.cookietech.adme.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment implements ServiceSearchAdapter.ServiceSearchAdapterListener {
    private static final String TAG = "SearchFragment";
    private RecyclerView search_service_rv;
    private List<Service> serviceProvidersList = new ArrayList<>();
    private ServiceSearchAdapter serviceSearchAdapter;
    private SearchView serviceSearchView;
    HomeViewModel homeViewModel;
    FirebaseFirestore db;
    int waitingTime = 500;
    CountDownTimer cntr;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_service, container, false);
        initializeFields(view);
        return view;
    }

    private void initializeFields(View view) {
        search_service_rv = view.findViewById(R.id.rv_service_result);
        serviceSearchAdapter = new ServiceSearchAdapter(getContext(), serviceProvidersList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        search_service_rv.setLayoutManager(mLayoutManager);
        search_service_rv.setItemAnimator(new DefaultItemAnimator());
        search_service_rv.setAdapter(serviceSearchAdapter);

        serviceSearchView = view.findViewById(R.id.searchView);
//        serviceSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    // searchView expanded
////                    serviceProvidersList.clear();
//                    serviceSearchAdapter.notifyDataSetChanged();
//                    search_service_rv.setVisibility(View.VISIBLE);
//                } else {
//                    // searchView not expanded
//                    serviceSearchView.setIconified(true);
//                    serviceSearchView.clearFocus();
//                }
//            }
//        });
        serviceSearchView.setIconifiedByDefault(true);
        serviceSearchView.setFocusable(true);
        serviceSearchView.setIconified(false);
        serviceSearchView.requestFocusFromTouch();


        serviceSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<String> queryList = new ArrayList<String>(Arrays.asList(query.split(" ")));
//                getFirebaseQueryList(queryList);
                homeViewModel.setQueryValue(queryList);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange getQueryResult: "+newText);
                List<String> queryList = new ArrayList<String>(Arrays.asList(newText.split(" ")));
//                getFirebaseQueryList(queryList);
//                homeViewModel.setQueryValue(queryList);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getQueryResult().observe(getViewLifecycleOwner(), new Observer<List<Service>>() {
            @Override
            public void onChanged(List<Service> services) {
                Log.d(TAG, services.size()+" onEvent getQueryResult: called "+homeViewModel.getQueryResult().getValue().size());
                serviceProvidersList.clear();
                serviceProvidersList.addAll(services);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        serviceSearchAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private void getFirebaseQueryList(List<String> queryArray) {
        Log.d(TAG, " Current4 data: " + queryArray);
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_Service_list")
//                .startAt(queryArray.get(0))
//                .endAt(queryArray.get(0)+"\uf8ff")
                .whereArrayContainsAny("tags", queryArray)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        serviceProvidersList.clear();
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                        } else {
                            for (QueryDocumentSnapshot doc : value) {
                                Service sv = doc.toObject(Service.class);
                                serviceProvidersList.add(sv);
                            }
                        }
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                serviceSearchAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
    }

    @Override
    public void onServiceProviderSelected(Service serviceProvider) {
        Intent intent = new Intent(getContext(), ServiceProviderDetailsActivity.class);
        intent.putExtra("serviceProviderObject", serviceProvider);
        getContext().startActivity(intent);
    }
}
