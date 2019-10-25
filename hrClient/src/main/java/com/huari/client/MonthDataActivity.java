package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huari.dataentry.DateString;
import com.huari.tools.FileOsImpl;
import com.huari.ui.CalendarItemView;
import com.huari.ui.CalendarLayout;
import com.huari.ui.CalendarView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MonthDataActivity extends AppCompatActivity {
    CalendarView calendarView;
    CalendarLayout calendarLayout;
    CalendarItemView calendarItemView;
    List<DateString> childList;//存储着去重后的日期数据，返回一个字符串String
    List<CalendarItemView> itemViewList;//存储着一个包含了添加到日历下部的小item图
    List<File> files;
    RelativeLayout addView;
    TextView showText;
    Button searchButton;
    LinearLayout linearLayout;
    float px;
    private int[] m;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_data);
        initView();
    }

    private void initView() {
        px = getResources().getDimension(R.dimen.dp_30);
        calendarView = findViewById(R.id.calendar);
        calendarView.setTouchDisallowFlag(false);
        calendarLayout = findViewById(R.id.item_layout);
        addView = findViewById(R.id.add_date);
        addView.setOnClickListener(v -> addViewClick());
    }

    private void collect() {
//        collectAllInformation();
        searchInSaveFloder();
        if (m[0] != -1) {
            calendarLayout.removeView(linearLayout);
            linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.data_search_textbottom, null);
            showText = linearLayout.findViewById(R.id.data_search);
            showText.setTextColor(Color.parseColor("#99FFFFFF"));
            searchButton = linearLayout.findViewById(R.id.search_button);
            calendarLayout.addView(linearLayout, 3000, (int) px);
        }
        SpannableString spanString = new SpannableString("在上述时间段内，共查询到" + num + "条数据");
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        spanString.setSpan(span, 13, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        showText.setText(spanString);
        searchButton.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(files);
            startActivity(new Intent(MonthDataActivity.this, FindFileActivity.class));
        });
    }

    public void searchInSaveFloder() {
        num = 0;
        files = new ArrayList<>();
        File file = new File(FileOsImpl.forSaveFloder + File.separator + "data");
        if (file.exists() && file.length() != 0) {
            List<String> intList = new ArrayList<>();
            for (DateString dateString : childList) {
                intList.add(dateString.getDate());
            }
            for (File file1 : file.listFiles()) {
                String namePart = file1.getName();
                if (namePart.length() > 13) {
                    namePart = namePart.substring(3, 13);
                    if (intList.contains(namePart)) {
                        files.add(file1);
                        num++;
                    }
                }
            }
        }
    }

    private void addViewClick() {
        m = calendarView.getCheckPosition();//多少号
        if (m.length == 1) {
            int position = m[0];
            if (position != -1) {
                String s = String.valueOf(position);
                addViewtoParent(s, m);
            }
        } else if (m.length == 2) {
            String s = (m[0] > m[1] ? m[1] : m[0]) + "-" + (m[0] < m[1] ? m[1] : m[0]);
            addViewtoParent(s, m);
        }
        collect();
    }

    private void addViewtoParent(String s, int[] m) {
        calendarItemView = new CalendarItemView(this, s, calendarView.getYear(), calendarView.getMonth(), m);
        addIntoRecord();
        calendarItemView.setDeleteOwn(view -> removeView(view));
        if (s.length() <= 2) {
            calendarLayout.addView(calendarItemView, (int) px, (int) px);
        } else {
            calendarLayout.addView(calendarItemView, (int) px * 2, (int) px);
        }
        calendarView.deletePosition();
    }

    private void addIntoRecord() {
        if (childList == null) {
            childList = new ArrayList<>();
        }
        if (itemViewList == null) {
            itemViewList = new ArrayList<>();
        }
        if (m.length == 1) {
            DateString dateString = new DateString(calendarView.getYear(),
                    calendarView.getMonth()
                    , m[0]);
            if (!childList.contains(dateString)) {
                childList.add(dateString);
            } else {
                childList.get(childList.indexOf(dateString)).addNum();
            }
        } else if (m.length == 2) {
            int min = m[0] > m[1] ? m[1] : m[0];
            int max = m[0] < m[1] ? m[1] : m[0];
            for (int i = min; i <= max; i++) {
                DateString dateString = new DateString(calendarView.getYear(),
                        calendarView.getMonth()
                        , i);
                if (!childList.contains(dateString)) {
                    childList.add(dateString);
                } else {
                    childList.get(childList.indexOf(dateString)).addNum();
                }
            }
        }
        itemViewList.add(calendarItemView);
    }

    private void removeView(View view) {
        calendarLayout.removeView(view);
        itemViewList.remove(view);
        List<DateString> dateStringList = new ArrayList<>();
        if (itemViewList.size() != 0) {
            if (((CalendarItemView)view).getM().length == 1) {
                DateString dateString = new DateString(((CalendarItemView)view).getYear(),
                        ((CalendarItemView)view).getMonth(), ((CalendarItemView)view).getM()[0]);
                dateStringList.add(dateString);
            } else if (((CalendarItemView)view).getM().length == 2) {
                int min = ((CalendarItemView)view).getM()[0] > ((CalendarItemView)view).getM()[1] ? ((CalendarItemView)view).getM()[1] : ((CalendarItemView)view).getM()[0];
                int max = ((CalendarItemView)view).getM()[0] < ((CalendarItemView)view).getM()[1] ? ((CalendarItemView)view).getM()[1] : ((CalendarItemView)view).getM()[0];
                for (int i = min; i <= max; i++) {
                    DateString dateString = new DateString(((CalendarItemView)view).getYear(),((CalendarItemView)view).getMonth(),i);
                    dateStringList.add(dateString);
                }
            }
            reduceDataStringNum(dateStringList);
            collect();
        } else {
            calendarLayout.removeView(linearLayout);
        }
    }

    private void reduceDataStringNum(List<DateString> dateStrings) {
        for(DateString dateString:dateStrings){
            if(childList.contains(dateString)){
                int i = childList.indexOf(dateString);
                childList.get(i).reduceNum();
                if(childList.get(i).getNum()==0){
                    childList.remove(i);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarView.setSystemUiVisibility(View.INVISIBLE);
    }
}
