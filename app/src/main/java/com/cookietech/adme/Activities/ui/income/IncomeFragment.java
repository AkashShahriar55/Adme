package com.cookietech.adme.Activities.ui.income;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.cookietech.adme.R;
import com.hadiidbouk.charts.BarData;
import com.hadiidbouk.charts.ChartProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class IncomeFragment extends Fragment {

    private Calendar myCalendar;
    private TextView tv_calender1,tv_calender2;
    private ImageView img_calender1, img_calender2;
    ChartProgressBar mChart;
    CardView cv_money,cv_rating;
    private int mYear, mMonth, mDay;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        IncomeViewModel notificationsViewModel = new ViewModelProvider(this).get(IncomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_income, container, false);

//        final TextView textView = root.findViewById(R.id.tv_history);
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        initializeFields(root);
        return root;
    }

    private void initializeFields(View root) {
        cv_money = (CardView) root.findViewById(R.id.cv_1);
        cv_rating = (CardView) root.findViewById(R.id.cv_2);
        createChart(root);
        img_calender1 = (ImageView) root.findViewById(R.id.img_calender1);
        img_calender2 = (ImageView) root.findViewById(R.id.img_calender2);
        tv_calender1 = (TextView) root.findViewById(R.id.tv_calender1);
        tv_calender2 = (TextView) root.findViewById(R.id.tv_calender2);

        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate1();
            }
        };
        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate2();
            }
        };

        img_calender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog1 =new DatePickerDialog(getContext(), date1,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
//                mDatePickerDialog1.getDatePicker().setMaxDate(myCalendar.get(Calendar.DAY_OF_MONTH));
//                mDatePickerDialog1.getDatePicker().setMinDate((myCalendar.get(Calendar.DAY_OF_MONTH)-10));
                mDatePickerDialog1.show();
            }
        });
        img_calender2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog2 =new DatePickerDialog(getContext(), date2,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialog2.show();
            }
        });

        cv_rating.setOnClickListener(v -> startActivity(new Intent(getContext(), RatingAndHistoryActicity.class)));
        cv_money.setOnClickListener(v -> startActivity(new Intent(getContext(), MoneyHistoryActicity.class)));

    }

    private void updateDate1() {
        String myFormat = "dd MMM''yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tv_calender1.setText(sdf.format(myCalendar.getTime()));
//        Toast.makeText(getApplicationContext(), "date : "+sdf.format(myCalendar.getTime()), Toast.LENGTH_SHORT).show();
    }
    private void updateDate2() {
        String myFormat = "dd MMM''yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tv_calender2.setText(sdf.format(myCalendar.getTime()));
//        Toast.makeText(getApplicationContext(), "date : "+sdf.format(myCalendar.getTime()), Toast.LENGTH_SHORT).show();
    }

    private void createChart(View root) {
        ArrayList<BarData> dataList = new ArrayList<>();

        BarData data = new BarData("Sep", 30.4f, "300.4$");
        dataList.add(data);

        data = new BarData("Oct", 42f, "420$");
        dataList.add(data);

        data = new BarData("Nov", 10.8f, "100.8$");
        dataList.add(data);

        data = new BarData("Dec", 87.3f, "870.3$");
        dataList.add(data);

        data = new BarData("Jan", 36.2f, "360.2$");
        dataList.add(data);

        data = new BarData("Feb", 99.3f, "990.3$");
        dataList.add(data);

        data = new BarData("Nov", 71.8f, "710.8$");
        dataList.add(data);

        mChart = (ChartProgressBar) root.findViewById(R.id.ChartProgressBar);
        mChart.setDataList(dataList);
//        mChart.build();
//        Toast.makeText(getApplicationContext(), "date : "+sdf.format(myCalendar.getTime()), Toast.LENGTH_SHORT).show();
    }

    private boolean isViewLoaded=false;
    public void updateView() {
        if (!isViewLoaded) {
            MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    mChart.build();
                    isViewLoaded=true;
                    Log.d("LeaderBoardFragment", "queueIdle: updateView");
                    return false;
                }
            };
            Looper.myQueue().addIdleHandler(handler);
        }
    }
}
