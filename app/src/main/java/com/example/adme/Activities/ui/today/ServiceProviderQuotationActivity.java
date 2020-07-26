package com.example.adme.Activities.ui.today;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.adme.Activities.ui.income.InvoiceActivity;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Architecture.UserDataModel;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.AppointmentRef;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.MyPlaces;
import com.example.adme.Helpers.Notification;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceProviderQuotationActivity  extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "QuotationDetails";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private GoogleMap mMap;
    private TextView tv_distance,tv_clint_time,tv_clint_money,tv_clint_name,tv_clint_address,tv_clint_text,tv_service_title,tv_service_list,tv_money,tv_state;
    private EditText tv_service_time, tv_service_date, tv_service_money,tv_service_quotation;
    private Button send_button,bt_approve,bt_decline,bt_create_invoice,bt_cancel_appointment;
    private FloatingActionButton fab_back,fab_call;
    private ImageView im_state;
    private Calendar myCalendar;
    private TextInputLayout til_service_time,til_service_date;
    ConstraintLayout inputField;
    FirebaseFirestore db;
    Appointment appointment;
    LatLng currentLocation;
    private View bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    String appointmentID="";
    UserDataModel userDataModel;
    User currentUser;
    String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quotation_details_fragment);
        userDataModel = new ViewModelProvider(this).get(UserDataModel.class);
        userDataModel.getCurrentUser().observe(this, user -> { currentUser = user; });

        String from = getIntent().getStringExtra("from");
        if(from.equals("AppointmentAdapter")){
            appointment = new Gson().fromJson(getIntent().getStringExtra("appointment"), Appointment.class);
            appointmentID = appointment.getAppointmentID();
            MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    updateView();
                    updateMap();
                    seenNotification();
                    return false;
                }
            };
            Looper.myQueue().addIdleHandler(handler);
        } else {
            appointmentID = getIntent().getStringExtra("reference");
            getFirebaseData();
        }

//        checkPermission();
        initializeFields();
    }

    private void initializeFields() {
        db = FirebaseFirestore.getInstance();

        bottomSheet = findViewById(R.id.appointment_bottom_details);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        fab_back = findViewById(R.id.fab_back);
        fab_call = findViewById(R.id.fab_call);

        tv_distance = findViewById(R.id.tv_distance);
        tv_clint_time = findViewById(R.id.tv_clint_time);
        tv_clint_money = findViewById(R.id.tv_clint_money);
        tv_clint_name = findViewById(R.id.tv_clint_name);
        tv_clint_address = findViewById(R.id.tv_clint_address);
        tv_clint_text = findViewById(R.id.tv_clint_text);
        tv_service_title = findViewById(R.id.tv_service_title);
        tv_service_list = findViewById(R.id.tv_service_list);
        tv_money = findViewById(R.id.tv_money);
        tv_state = findViewById(R.id.tv_state);
        im_state = findViewById(R.id.im_state);
        inputField = findViewById(R.id.constraintLayout6);

        tv_service_time = findViewById(R.id.tv_service_time);
        tv_service_date = findViewById(R.id.tv_service_date);
        tv_service_money = findViewById(R.id.tv_service_money);
        tv_service_quotation = findViewById(R.id.tv_service_quotation);
        til_service_time = (TextInputLayout) findViewById(R.id.textInputLayout2);
        til_service_date = (TextInputLayout) findViewById(R.id.textInputLayout5);

        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tv_service_date.setText(sdf.format(myCalendar.getTime()));
                til_service_date.setError(null);
            }
        };

        tv_service_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(ServiceProviderQuotationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);
                        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        tv_service_time.setText(sdfs.format(myCalendar.getTime()));
                        til_service_time.setError(null);
                        Log.d(TAG, myCalendar.getTimeInMillis() + " getTimeInMillis");
                    }
                }, hour, minute, false);
                mTimePicker.show();
            }
        });

        tv_service_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog1 = new DatePickerDialog(ServiceProviderQuotationActivity.this, date1,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialog1.show();
            }
        });

        //send button setting
        send_button = findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInputError()){
                    setFirebaseData();
                }
            }
        });

        //approve button setting
        bt_approve = findViewById(R.id.bt_approve);
        bt_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appointment.setState(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_APPROVED);
                db.collection("Adme_Appointment_list").document(appointmentID)
                        .set(appointment)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "appointment successfully written!");

//                                AppointmentRef appointmentRef = new AppointmentRef();
//                                appointmentRef.setReference(appointmentID);
//                                appointmentRef.setMode(FirebaseUtilClass.MODE_SERVICE_PROVIDER);

//                                db.collection("Adme_User/"+ appointment.getService_provider_ref() +"/appointment_list")
//                                        .document(appointmentID)
//                                        .set(appointmentRef)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d(TAG, "appointmentID successfully written "+appointmentID);
////                                                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w(TAG, "Error writing document appointmentID", e);
//                                            }
//                                        });

                                createNotification(
                                        appointment.getClint_name()+" approved your request",
                                        FirebaseUtilClass.MODE_SERVICE_PROVIDER+"",
                                        appointment.getService_provider_ref()+"",
                                        "Appointment approved successful."
                                );

                                createNotification(
                                        "You've successfully created an appointment",
                                        FirebaseUtilClass.MODE_CLIENT+"",
                                        appointment.getClint_ref()+"",
                                        "Appointment approved successful."
                                );

                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        });

        //decline button setting
        bt_decline = findViewById(R.id.bt_decline);
        bt_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appointment.setState(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_CANCELED);
                db.collection("Adme_Appointment_list").document(appointmentID)
                        .set(appointment)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "appointment successfully written!");

                                createNotification(
                                        appointment.getClint_name()+" decline your request",
                                        FirebaseUtilClass.MODE_SERVICE_PROVIDER+"",
                                        appointment.getService_provider_ref()+"",
                                        "Appointment declined successful."
                                );

                                createNotification(
                                        "You've declined an appointment",
                                        FirebaseUtilClass.MODE_CLIENT+"",
                                        appointment.getClint_ref()+"",
                                        "Appointment declined successful."
                                );

                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

            }
        });

        //bt_cancel_appointment button setting
        bt_cancel_appointment = findViewById(R.id.bt_cancel_appointment);
        bt_cancel_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isClientMode()){
                    appointment.setState(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_CANCELED);
                    db.collection("Adme_Appointment_list").document(appointmentID)
                            .set(appointment)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "appointment successfully written!");

                                    createNotification(
                                            appointment.getClint_name()+" canceled your request",
                                            FirebaseUtilClass.MODE_SERVICE_PROVIDER+"",
                                            appointment.getService_provider_ref()+"",
                                            "Appointment canceled successful."
                                    );

                                    createNotification(
                                            "You've canceled an appointment",
                                            FirebaseUtilClass.MODE_CLIENT+"",
                                            appointment.getClint_ref()+"",
                                            "Appointment canceled successful."
                                    );

                                    onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                } else {
                    appointment.setState(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_CANCELED);
                    db.collection("Adme_Appointment_list").document(appointmentID)
                            .set(appointment)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "appointment successfully written!");

                                    createNotification(
                                            appointment.getService_provider_name() + " canceled your appointment",
                                            FirebaseUtilClass.MODE_CLIENT + "",
                                            appointment.getClint_ref() + "",
                                            "Appointment canceled"
                                    );

                                    createNotification(
                                            "You've canceled an appointment",
                                            FirebaseUtilClass.MODE_SERVICE_PROVIDER + "",
                                            appointment.getService_provider_ref() + "",
                                            "Appointment canceled successful."
                                    );

                                    onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                }
            }
        });

        //bt_create_invoice button setting
        bt_create_invoice = findViewById(R.id.bt_create_invoice);
        bt_create_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceProviderQuotationActivity.this, InvoiceActivity.class);
                intent.putExtra("appointment", new Gson().toJson(appointment));
                startActivity(intent);
                finish();
            }
        });

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phone.equals("")){
                    Toast.makeText(getApplicationContext(), "Phone number not found", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            }
        });

        if(mMap == null){
            setUpMap();
        }
    }

    public void updateView() {
        runOnUiThread(new Runnable(){
            public void run() {
                if(!isClientMode()) {
                    if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_SEND)) {
                        String servicetext = appointment.getServices();
                        if (servicetext.contains(",,,")) {
                            servicetext = servicetext.replace(",,,", "\n");
                        }
                        tv_distance.setText(appointment.getDistance() + " Miles");
                        tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa, dd MMM yyyy"));
                        tv_clint_money.setText("$ " + appointment.getPrice_requested());
                        tv_clint_name.setText(appointment.getClint_name());
                        String loc = appointment.getClint_location().getName().substring(0, appointment.getClint_location().getName().lastIndexOf(","));
                        tv_clint_address.setText(loc);
                        if (appointment.getClint_text().trim().equals("")) {
                            tv_clint_text.setText("No quotation written");
                        } else {
                            tv_clint_text.setText(appointment.getClint_text());
                        }
                        tv_service_list.setText(servicetext);
                        tv_money.setText("Requested Money : $" + appointment.getPrice_requested());
                        tv_service_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa"));
                        tv_service_date.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "dd MMM yyyy"));
                        tv_state.setText("State : Request sent to service provider");
                    } else {
                        phone = appointment.getClint_phone();
                        inputField.setVisibility(View.GONE);
                        tv_state.setVisibility(View.VISIBLE);
                        im_state.setVisibility(View.VISIBLE);
                        String servicetext = appointment.getServices();
                        if (servicetext.contains(",,,")) {
                            servicetext = servicetext.replace(",,,", "\n");
                        }
                        tv_service_list.setText(servicetext);
                        tv_distance.setText(appointment.getDistance() + " Miles");
                        tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa, dd MMM yyyy"));
                        tv_clint_money.setText("$ " + appointment.getPrice_needed());
                        tv_clint_name.setText(appointment.getClint_name());
                        String loc = appointment.getClint_location().getName().substring(0, appointment.getClint_location().getName().lastIndexOf(","));
                        tv_clint_address.setText(loc);
                        if (appointment.getClint_text().trim().equals("")) {
                            tv_clint_text.setText("No quotation written");
                        } else {
                            tv_clint_text.setText(appointment.getClint_text());
                        }
                        tv_money.setText("Needed Money : $" + appointment.getPrice_needed());
                        if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_FINISHED)){
                            tv_state.setText("State : Finished");
                        }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_CANCELED)){
                            tv_state.setText("State : Canceled by client");
                        }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_CANCELED)){
                            tv_state.setText("State : Canceled by service provider");
                        }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_SEND)){
                            tv_state.setText("State : Quotation sent to client");
                        }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_INVOICE_SEND)){
                            tv_state.setText("State : Invoice sent to client");
                            fab_call.setVisibility(View.VISIBLE);
                        }else if (appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_TIMEOUT_CANCELED)) {
                            tv_state.setText("State : Service provider did not respond");
                        }else{
                            tv_state.setText("State : Active Appointment");
                            fab_call.setVisibility(View.VISIBLE);
                            bt_create_invoice.setVisibility(View.VISIBLE);
                            bt_cancel_appointment.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_SEND)) {
                        inputField.setVisibility(View.GONE);
                        tv_state.setVisibility(View.VISIBLE);
                        im_state.setVisibility(View.VISIBLE);
                        bt_cancel_appointment.setVisibility(View.VISIBLE);
                        String servicetext = appointment.getServices();
                        if (servicetext.contains(",,,")) {
                            servicetext = servicetext.replace(",,,", "\n");
                        }
                        tv_distance.setText(appointment.getDistance() + " Miles");
                        tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa, dd MMM yyyy"));
                        tv_clint_money.setText("$ " + appointment.getPrice_requested());
                        tv_clint_name.setText(appointment.getService_provider_name());
                        String loc = appointment.getService_provider_location().getName().substring(0, appointment.getService_provider_location().getName().lastIndexOf(","));
                        tv_clint_address.setText(loc);
                        if (appointment.getClint_text().trim().equals("")) {
                            tv_clint_text.setText("No quotation written");
                        } else {
                            tv_clint_text.setText(appointment.getClint_text());
                        }
                        tv_service_list.setText(servicetext);
                        tv_money.setText("Requested Money : $" + appointment.getPrice_requested());
                        tv_state.setText("State : Request sent to service provider");
                    } else if (appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_SEND)) {
                        bt_approve.setVisibility(View.VISIBLE);
                        bt_decline.setVisibility(View.VISIBLE);
                        inputField.setVisibility(View.GONE);
                        String servicetext = appointment.getServices();
                        if (servicetext.contains(",,,")) {
                            servicetext = servicetext.replace(",,,", "\n");
                        }
                        tv_distance.setText(appointment.getDistance() + " Miles");
                        tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getService_provider_time(), "hh:mm aa, dd MMM yyyy"));
                        tv_clint_money.setText("$ " + appointment.getPrice_needed());
                        tv_clint_name.setText(appointment.getService_provider_name());
                        String loc = appointment.getService_provider_location().getName().substring(0, appointment.getService_provider_location().getName().lastIndexOf(","));
                        tv_clint_address.setText(loc);
                        if (appointment.getService_provider_text().trim().equals("")) {
                            tv_clint_text.setText("No quotation written");
                        } else {
                            tv_clint_text.setText(appointment.getService_provider_text());
                        }
                        tv_service_list.setText(servicetext);
                        tv_money.setText("Needed Money : $" + appointment.getPrice_needed());
                        tv_state.setText("State : Quotation sent to client");
                    } else {
                        phone = appointment.getService_provider_phone();
                        inputField.setVisibility(View.GONE);
                        tv_state.setVisibility(View.VISIBLE);
                        im_state.setVisibility(View.VISIBLE);
                        String servicetext = appointment.getServices();
                        if (servicetext.contains(",,,")) {
                            servicetext = servicetext.replace(",,,", "\n");
                        }
                        tv_service_list.setText(servicetext);
                        tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getService_provider_time(), "hh:mm aa, dd MMM yyyy"));
                        tv_distance.setText(appointment.getDistance() + " Miles");
                        tv_clint_money.setText("$ " + appointment.getPrice_needed());
                        tv_clint_name.setText(appointment.getService_provider_name());
                        String loc = appointment.getService_provider_location().getName().substring(0, appointment.getService_provider_location().getName().lastIndexOf(","));
                        tv_clint_address.setText(loc);
                        if (appointment.getService_provider_text().trim().equals("")) {
                            tv_clint_text.setText("No quotation written");
                        } else {
                            tv_clint_text.setText(appointment.getService_provider_text());
                        }
                        tv_money.setText("Needed Money : $" + appointment.getPrice_needed());
                        if (appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_FINISHED)) {
                            tv_state.setText("State : Finished");
                        } else if (appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_CANCELED)) {
                            tv_state.setText("State : Canceled by client");
                        } else if (appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_CANCELED)) {
                            tv_state.setText("State : Canceled by service provider");
                        } else if (appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_TIMEOUT_CANCELED)) {
                            tv_state.setText("State : Service provider did not respond");
                        } else {
                            tv_state.setText("State : Active Appointment");
                            fab_call.setVisibility(View.VISIBLE);
                            bt_cancel_appointment.setVisibility(View.VISIBLE);
                        }
                    }
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    public void seenNotification(){
        String notiID = CookieTechUtilityClass.getSharedPreferences("notification", this);
        String mUserId = CookieTechUtilityClass.getSharedPreferences("mUserId", this);

        db = FirebaseFirestore.getInstance();
        db.collection("Adme_User/"+ mUserId +"/notification_list")
                .document(notiID)
                .update("seen", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void getFirebaseData() {
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_Appointment_list").document(appointmentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        appointment = documentSnapshot.toObject(Appointment.class);
                        appointment.setAppointmentID(appointmentID);
                        MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
                            @Override
                            public boolean queueIdle() {
                                updateView();
                                updateMap();
                                seenNotification();
                                return false;
                            }
                        };
                        Looper.myQueue().addIdleHandler(handler);
                    }
                });
    }

    private void setFirebaseData(){
        String requestedMoney = "";
        if(tv_service_money.getText().toString().trim().equals("")){
            requestedMoney = appointment.getPrice_requested();
        } else {
            requestedMoney = tv_service_money.getText().toString();
        }
        appointment.setPrice_needed(requestedMoney);
        appointment.setService_provider_text(tv_service_quotation.getText().toString());
        appointment.setState(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_SEND);
        appointment.setService_provider_phone(currentUser.getContacts().get(FirebaseUtilClass.ENTRY_PHONE_NO));

        String givenDateString = tv_service_time.getText().toString()+" "+tv_service_date.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa dd MMM yyyy");
        try {
            Date mDate = sdf.parse(givenDateString);
            long timeInMilliseconds = mDate.getTime();
            appointment.setService_provider_time(String.valueOf(timeInMilliseconds));
//            Log.d(TAG, CookieTechUtilityClass.getTimeDate(String.valueOf(timeInMilliseconds),"hh:mm aa, dd MMM yyyy")+" in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        db.collection("Adme_Appointment_list").document(appointmentID)
                .set(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        createNotification(
                                appointment.getService_provider_name()+" response to your request",
                                FirebaseUtilClass.MODE_CLIENT+"",
                                appointment.getClint_ref()+"",
                                "Successfully send quotation to client."
                        );
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private boolean isInputError(){
        if(tv_service_time.getText().toString().trim().equals("")){
            til_service_time.setError("Select a time");
            return true;
        } else if(tv_service_date.getText().toString().trim().equals("")){
            til_service_date.setError("Select a date");
            return true;
        } else {
            return false;
        }
    }

    private void checkPermission() {
        // Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(ServiceProviderQuotationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ServiceProviderQuotationActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProviderQuotationActivity.this);

                builder.setTitle("Location Permission").setMessage("Location permission is must for the map features")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(ServiceProviderQuotationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ServiceProviderQuotationActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            if(mMap == null){
                setUpMap();
            }else{
//                updateMap();
            }
        }
    }

    private void setUpMap() {
        new Thread(() -> {
            try {
                SupportMapFragment mf = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.map, mf)
                        .commit();
                runOnUiThread(() -> mf.getMapAsync(ServiceProviderQuotationActivity.this));
            }catch (Exception ignored){

            }
        }).start();
    }

    public void updateMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isClientMode()) {
                    FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(ServiceProviderQuotationActivity.this);
                    locationProviderClient.getLastLocation().addOnSuccessListener(ServiceProviderQuotationActivity.this, location -> {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.addMarker(new MarkerOptions().position(currentLocation).title(getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.service_provider, 90, 78))));
                                    mMap.addMarker(new MarkerOptions().position(appointment.getClintLatLng()).title(getString(R.string.client_current_location)).icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.client, 90, 78))));

                                    try {
                                        LatLngBounds zoomBound = LatLngBounds.builder().include(currentLocation).include(appointment.getClintLatLng()).build();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 300));
                                    }catch (Exception e){
                                        Log.d(TAG, "queueIdle: map load faild");
                                    }

                                    // Getting URL to the Google Directions API
                                    GoogleMapHelper helper = new GoogleMapHelper(mMap);
                                    String url = helper.getDirectionsUrl(currentLocation, appointment.getClintLatLng());
                                    Log.i(TAG, "run: " + url);
                                    helper.downloadJson(url);
                                }
                            });
                        }
                    }).addOnFailureListener(ServiceProviderQuotationActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else if(isClientMode()) {
                    if (appointment.getClintLatLng() != null && appointment.getServiceProviderLatLng() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions().position(appointment.getClintLatLng()).title(getString(R.string.your_location)).icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.client, 90, 78))));
                                mMap.addMarker(new MarkerOptions().position(appointment.getServiceProviderLatLng()).title(getString(R.string.sv_current_location)).icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.service_provider, 90, 78))));

                                try {
                                    LatLngBounds zoomBound = LatLngBounds.builder().include(appointment.getServiceProviderLatLng()).include(appointment.getClintLatLng()).build();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(appointment.getClintLatLng(), 10));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 300));
                                }catch (Exception e){
                                    Log.d(TAG, "queueIdle: map load faild");
                                }

                                // Getting URL to the Google Directions API
                                GoogleMapHelper helper = new GoogleMapHelper(mMap);
                                String url = helper.getDirectionsUrl(appointment.getServiceProviderLatLng(), appointment.getClintLatLng());
                                Log.i(TAG, "run: " + url);
                                helper.downloadJson(url);
                            }
                        });
                    } else {
                        Log.w(TAG, "Some ones loaction null");
                        Toast.makeText(getApplicationContext(), "Failed to get location info. Please, try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).start();
    }

    public void createNotification(String text, String mode, String reference, String toastText){
        String newDocumentID = String.valueOf(Calendar.getInstance().getTimeInMillis());
        Notification notification = new Notification();
        notification.setSeen(false);
        notification.setTime(newDocumentID);
        notification.setText(text);
        notification.setMode(mode);
        notification.setType(FirebaseUtilClass.NOTIFICATION_APPOINTMENT_TYPE);
        notification.setReference(appointmentID);

        db.collection("Adme_User/"+ reference +"/notification_list")
                .document(newDocumentID)
                .set(notification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Notification successfully written!");
                        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public Bitmap getIcon(int name,int height, int width){
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(name);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return marker;
    }

    public boolean isClientMode(){
        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        if(preferences.getBoolean("isClient",true)){
            String mUserId = CookieTechUtilityClass.getSharedPreferences("mUserId", this);
            return appointment.getClint_ref().equals(mUserId);
        } else {
            return false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}
