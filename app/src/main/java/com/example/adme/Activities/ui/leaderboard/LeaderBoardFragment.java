package com.example.adme.Activities.ui.leaderboard;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.ui.today.TodayFragment;
import com.example.adme.R;

public class LeaderBoardFragment extends Fragment {

    private RecyclerView recyclerView;
    private LeaderBoardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        /*final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        initializeFields(root);
        return root;
    }

    private void initializeFields(View root) {
        recyclerView = root.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new LeaderBoardAdapter();

    }

    private boolean isViewLoaded=false;
    public void updateView() {
        if (!isViewLoaded) {
            MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    recyclerView.setAdapter(adapter);
                    isViewLoaded=true;
                    Log.d("LeaderBoardFragment", "queueIdle: updateView");
                    return false;
                }
            };
            Looper.myQueue().addIdleHandler(handler);
        }
    }

}
