package com.example.adme.Activities.ui.income;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.ui.invoice.CustomerDetails;
import com.example.adme.Activities.ui.invoice.Invoice;
import com.example.adme.Activities.ui.invoice.Services;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity implements InvoiceCreateAdapter.SelectInvoiceCreateAdapterListener {
    private static final String TAG = "InvoiceActivity";
    private RecyclerView select_service_recyclerView;
    private InvoiceCreateAdapter services_adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView img_back,img_add;
    private TextView tv_item_add,tv_total_price;
    private TextInputLayout til_service_price,til_service_title,til_service_count;
    private EditText tv_service_title, tv_service_money, tv_service_count;
    private Button bt_ok,bt_delete,bt_create;
    private View bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private ArrayList<Services> selectServicesList = new ArrayList<>();
    Services currentItem;
    Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invoice);

        appointment = new Gson().fromJson(getIntent().getStringExtra("appointment"), Appointment.class);

        initializeFields();
    }

    private void initializeFields() {
        bottomSheet = findViewById(R.id.bottom_details);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        til_service_title = findViewById(R.id.textInputLayout51);
        til_service_price = findViewById(R.id.textInputLayout2);
        til_service_count = findViewById(R.id.textInputLayout5);

        tv_item_add = findViewById(R.id.tv_item_add);
        tv_total_price = findViewById(R.id.tv_total_price);

        tv_service_title = findViewById(R.id.tv_service_title);
        tv_service_money = findViewById(R.id.tv_service_money);
        tv_service_count = findViewById(R.id.tv_service_count);

        bt_ok = findViewById(R.id.bt_ok);
        bt_delete = findViewById(R.id.bt_delete);
        bt_create = findViewById(R.id.bt_create);

        img_back = (ImageView) findViewById(R.id.img_back);
        img_add = (ImageView) findViewById(R.id.img_add);

        select_service_recyclerView = (RecyclerView) findViewById(R.id.rv_edit_invoice);
        layoutManager = new LinearLayoutManager(this);
        select_service_recyclerView.setLayoutManager(layoutManager);
        select_service_recyclerView.setHasFixedSize(true);
        services_adapter = new InvoiceCreateAdapter(InvoiceActivity.this, selectServicesList,this);
        select_service_recyclerView.setAdapter(services_adapter);

        img_back.setOnClickListener(v -> onBackPressed());
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                tv_item_add.setText("Add Service/Item Price");
                tv_total_price.setText("");
                tv_service_title.setText(null);
                tv_service_money.setText(null);
                tv_service_count.setText(null);
                til_service_title.setError(null);
                til_service_price.setError(null);
                til_service_count.setError(null);
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInputError()){
                    if(!tv_item_add.getText().toString().equals("Add Service/Item Price")){
                        selectServicesList.remove(currentItem);
                    }
                    selectServicesList.add(new Services(
                            tv_service_title.getText().toString(),
                            Double.parseDouble(tv_service_money.getText().toString()),
                            Integer.parseInt(tv_service_count.getText().toString())
                    ));
                    services_adapter.notifyDataSetChanged();

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    bottomSheet.clearFocus();
                    bottomSheet.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            closeKeyboard();
                        }
                    }, 500);
                }
            }
        });
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet.clearFocus();
                bottomSheet.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeKeyboard();
                    }
                }, 500);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if(currentItem!=null){
                    selectServicesList.remove(currentItem);
                    services_adapter.notifyDataSetChanged();
                }
            }
        });
        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerDetails detailsForServices = new CustomerDetails();

                detailsForServices.setAppointment_id(appointment.getAppointmentID());
                detailsForServices.setService_id(appointment.getServiceID());
                detailsForServices.setDue_date(CookieTechUtilityClass.getTimeDate(appointment.getService_provider_time(), "dd MMM yyyy"));
                detailsForServices.setCustomer_name(appointment.getClint_name());
                detailsForServices.setCustomer_ref(appointment.getClint_ref());
                detailsForServices.setCustomer_phone(appointment.getClint_phone());
                detailsForServices.setCustomer_email("");
                detailsForServices.setCustomer_address(appointment.getClint_location().getName());
                detailsForServices.setService_provider(appointment.getService_provider_name());
                detailsForServices.setVat(0.0);
                detailsForServices.setDiscount(0.0);

                Intent intent = new Intent(InvoiceActivity.this, Invoice.class);
                intent.putExtra("service_details", detailsForServices);
                intent.putExtra("service_list", selectServicesList);
                intent.putExtra("appointment", new Gson().toJson(appointment));
                intent.putExtra("mode", FirebaseUtilClass.ENTRY_EDITABLE);
                intent.putExtra("from", "InvoiceActivity");
                startActivity(intent);
                finish();
            }
        });

        MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                updateView();
                return false;
            }
        };
        Looper.myQueue().addIdleHandler(handler);
    }

    public void updateView() {
        if(appointment!=null) {
            String servicesss = appointment.getServices();
            List<String> list = new ArrayList<String>(Arrays.asList(servicesss.split(",,,")));
            for (String item : list) {
                String first = item.substring(3).split("\\(")[0];
                String second = item.split("\\(")[1];
                String thrd = second.substring(1, second.length() - 1);
                Log.d(TAG, "onCreate: Name=" + first);
                Log.d(TAG, "onCreate: Price=" + thrd);
                selectServicesList.add(new Services(first, Double.parseDouble(thrd)));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    services_adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private boolean isInputError(){
        if(tv_service_title.getText().toString().trim().equals("")){
            til_service_title.setError("Write Service/Item Name");
            return true;
        } else if(tv_service_money.getText().toString().trim().equals("")){
            til_service_price.setError("Write price");
            return true;
        } else if(tv_service_count.getText().toString().trim().equals("")){
            til_service_count.setError("Write count");
            return true;
        } else {
            return false;
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onServicesSelected(Services selectServicesItem) {
        currentItem=selectServicesItem;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        tv_item_add.setText(selectServicesItem.getService_name());
        tv_total_price.setText("$ "+(selectServicesItem.getService_cost()*selectServicesItem.getService_quantity()));
        tv_service_title.setText(selectServicesItem.getService_name());
        tv_service_money.setText(selectServicesItem.getService_cost()+"");
        tv_service_count.setText(selectServicesItem.getService_quantity()+"");
        til_service_title.setError(null);
        til_service_price.setError(null);
        til_service_count.setError(null);
    }
}
