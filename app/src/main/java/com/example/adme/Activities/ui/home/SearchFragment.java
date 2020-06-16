package com.example.adme.Activities.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.Notification_class;
import com.example.adme.Activities.ui.today.NotificationItemInventoryAdapter;
import com.example.adme.Activities.ui.today.NotificationViewModel;
import com.example.adme.Activities.ui.today.Notification_Fragment;
import com.example.adme.Helpers.Service;
import com.example.adme.R;
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
    private List<Service> serviceProvidersList;
    private ServiceSearchAdapter serviceSearchAdapter;
    private SearchView serviceSearchView;

    FirebaseFirestore db;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_service, container, false);

        search_service_rv = view.findViewById(R.id.rv_service_result);
        serviceProvidersList = new ArrayList<>();
        serviceSearchAdapter = new ServiceSearchAdapter(getContext(), serviceProvidersList, this);
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
                getFirebaseQueryList(queryList);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<String> queryList = new ArrayList<String>(Arrays.asList(newText.split(" ")));
                getFirebaseQueryList(queryList);
                return false;
            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
