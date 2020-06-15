package com.example.adme.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.adme.R;

import org.w3c.dom.Text;


public class LoadingDialog extends Dialog {
    private String title;
    private String progress;
    TextView titleTV,progressTV;

    public LoadingDialog(@NonNull Context context,String title,String progress) {
        super(context);
        this.title = title;
        this.progress = progress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);
        setCancelable(false);
         titleTV = findViewById(R.id.custom_dialog_title);
        titleTV.setText(title);
        progressTV = findViewById(R.id.tv_progress);
        progressTV.setText(progress);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void updateProgress(String progress){
        progressTV.setText(progress);
    }

    public void updateTitle(String title){
        titleTV.setText(title);
    }


}
