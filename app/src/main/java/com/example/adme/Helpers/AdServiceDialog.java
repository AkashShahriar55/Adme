package com.example.adme.Helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.adme.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AdServiceDialog extends AppCompatDialogFragment {
    private TextInputLayout edt_service_name,edt_service_description,edt_service_charge;
    private AdServiceDialogListener listener;
    private String calledFrom;

    public AdServiceDialog(String calledFrom) {
        this.calledFrom = calledFrom;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ad_service_dialog, null);
        builder.setView(view);
        edt_service_name = view.findViewById(R.id.edt_service_name);
        edt_service_description = view.findViewById(R.id.edt_service_description);
        edt_service_charge = view.findViewById(R.id.edt_service_charge);
        TextView cancel_btn = view.findViewById(R.id.cancel_btn);
        TextView add_btn = view.findViewById(R.id.add_btn);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        cancel_btn.setOnClickListener(v -> {
           dialog.dismiss();
        });

        add_btn.setOnClickListener(v -> {

            String service_name = Objects.requireNonNull(edt_service_name.getEditText()).getText().toString();
            String service_description = Objects.requireNonNull(edt_service_description.getEditText()).getText().toString();
            String service_charge = Objects.requireNonNull(edt_service_charge.getEditText()).getText().toString();


            if (validate(service_name,service_description,service_charge)){
                listener.dialogText(service_name,service_description,service_charge);
                dialog.dismiss();

            }


        });

        return dialog;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (calledFrom.equals("fragment")){
                listener = (AdServiceDialogListener) getTargetFragment();
            }
            else{
                listener = (AdServiceDialogListener) getActivity();
            }
        } catch (ClassCastException e) {
            Log.e("Error AdServiceDialog", Objects.requireNonNull(e.getMessage()));
        }
    }

    private boolean validate(String service_name, String service_description, String service_charge) {
        if (service_name.isEmpty()){
            edt_service_name.setErrorEnabled(true);
            edt_service_name.setError("Field can't be empty");
            return false;
        }
        else if (service_description.isEmpty()){
            edt_service_name.setErrorEnabled(false);
            edt_service_description.setErrorEnabled(true);
            edt_service_description.setError("Field can't be empty");
            return false;

        }

        else if (service_charge.isEmpty()){
            edt_service_description.setErrorEnabled(false);
            edt_service_charge.setErrorEnabled(true);
            edt_service_charge.setError("Field can't be empty");
            return false;

        }
        else {
            return  true;
        }

    }


    public interface AdServiceDialogListener{
        void dialogText(String service_name, String service_description, String service_charge);
    }
}
