package com.huari.ui;

import java.util.ArrayList;

import com.huari.diskactivity.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PinDuan extends LinearLayout implements View.OnClickListener {
    com.huari.ui.PinScanningShowWave pss;
    LinearLayout findlayout, freqinputLayout;
    LinearLayout linearlayout;
    EditText inputtext;


    private Button markfindButton, leftfindButton, rightfindButton, freqinputButton;
    ArrayList<Integer> datalist;
    MyAdapter adapter;
    ListView listview;
    Handler handler;

    TextView daneiNameTextView;
    String showinfo = "幅度（dBuV）";
    Context context;

    public void setShowInfo(String s) {
        showinfo = s;
    }

    public PinDuan(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        ini();
    }

    public PinDuan(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        ini();
    }

    public PinDuan(Context context) {
        super(context);
        this.context = context;
        ini();
    }

    public void setTiaoZhi(boolean booleanValue) {
        pss.setTiaoZhi(booleanValue);
    }

    public void setYuzhifudu(int value) {
        pss.yuzhifudu = value;
        pss.postInvalidate();
    }

    public void hideTable(boolean hide) {
        if (hide) {
            linearlayout.setVisibility(View.GONE);
        } else {
            linearlayout.setVisibility(View.VISIBLE);
        }
    }

    private void ini() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.BLACK);

        linearlayout = (LinearLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.pinduanlistview, null);
        daneiNameTextView = linearlayout.findViewById(R.id.pl);
        listview = linearlayout.findViewById(R.id.pinduanlistview);

        pss = new PinScanningShowWave(getContext());

        findlayout = (LinearLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.findbuttons, null);

        markfindButton = findlayout.findViewById(R.id.fast_left_find);
        leftfindButton = findlayout.findViewById(R.id.left_find);
        rightfindButton = findlayout.findViewById(R.id.right_find);
        freqinputButton = findlayout.findViewById(R.id.fast_right_find);

        //bind sticks' onClick event
        markfindButton.setOnClickListener(this);
        leftfindButton.setOnClickListener(this);
        rightfindButton.setOnClickListener(this);
        freqinputButton.setOnClickListener(this);
        datalist = pss.datalist;

        LinearLayout.LayoutParams s1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 9);
        LinearLayout.LayoutParams s3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, Dp2Px(context,40));
        LinearLayout.LayoutParams s2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 9);

        try {
            adapter = new MyAdapter();
        } catch (Exception e) {
        }
        ;
        listview.setAdapter(adapter);

        addView(linearlayout, s2);
        addView(pss, s1);
        addView(findlayout, s3);

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0x1)
                    pss.refreshListData();
                // daneiNameTextView.setText(showinfo);
                adapter.notifyDataSetChanged();
            }
        };

    }

    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //当前屏幕密度因子
        Log.d("xiaodp", String.valueOf(dp * scale + 0.5f));
        return (int) (dp * scale + 0.5f);
    }

    public void setTopViewLayoutParamsH() {
        Log.d("xiaopara","h");
        int dp_40 = Dp2Px(context,40);
        LinearLayout.LayoutParams s2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp_40);
        linearlayout.setLayoutParams(s2);
        linearlayout.invalidate();
        this.requestLayout();
    }

    public void setTopViewLayoutParamsV() {
        Log.d("xiaopara","v");
        LinearLayout.LayoutParams s2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 9);
        linearlayout.setLayoutParams(s2);
        linearlayout.invalidate();
        this.requestLayout();
    }

    public void setM(short[] m) {
        pss.setM(m);
    }

    public void setMax(short[] max) {
        pss.setMax(max);
    }

    public void setMin(short[] min) {
        pss.setMin(min);
    }

    public void setAvg(short[] avg) {
        pss.setAvg(avg);
    }

    public void setParameters(float fl, float fh, float bujin) {
        pss.setF(fl, fh, bujin);
        pss.postInvalidate();
    }

    public void set_ah_al(float ah, float al) {
        pss.set_ah_al(ah, al);
    }

    public void refreshWave() {
        pss.postInvalidate();
    }

    public void refreshTable() {
        Message msg = new Message();
        msg.what = 0x1;
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.fast_left_find) {
            pss.find_marker(0);
            pss.postInvalidate();
        }
        if (i == R.id.left_find) {
            pss.find_marker(1);
            pss.postInvalidate();
        }
        if (i == R.id.right_find) {
            pss.find_marker(2);
            pss.postInvalidate();
        }
        if (i == R.id.fast_right_find) {
            freq_input();
        }


    }

    class MyAdapter extends BaseAdapter {

        int max;

        class ViewHolder {
            TextView tv1, tv2, tv3;
        }

        @Override
        public int getCount() {
            max = Math.max(datalist.size(), 30);
            if (max < DataSave.pinduanshowpointcounts) {
                return max;
            } else {
                return 1;
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewholder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.pinduanlistviewdata, null);
                viewholder = new ViewHolder();
                viewholder.tv1 = convertView
                        .findViewById(R.id.listxuhao);
                viewholder.tv2 = convertView
                        .findViewById(R.id.listpinlv);
                viewholder.tv3 = convertView
                        .findViewById(R.id.listfudu);
                convertView.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
            }
            // viewholder.tv1.setText((Integer)datalist.get(position)+"");
            if (max < 500) {
                if (position < datalist.size()) {
                    viewholder.tv1.setText(position + 1 + "");
                    viewholder.tv3.setText(pss.getMValue((Integer) datalist
                            .get(position)) + "");
                    viewholder.tv2.setText(pss.indextof((Integer) datalist
                            .get(position)) + "");
                } else {
                    viewholder.tv1.setText("");
                    viewholder.tv2.setText("");
                    viewholder.tv3.setText("");
                }
            } else {
                viewholder.tv2.setText("将导致效率严重降低");
                viewholder.tv1.setText("限值过低");

                viewholder.tv3.setText("请重调限值");
            }

            return convertView;
        }
    }

    public void setDanWei(String name, String danwei) {
        pss.setDanwei(name, danwei);
        showinfo = name + "(" + danwei + ")";
        clear();
    }

    public void clear() {
        setM(null);
        setMax(null);
        setMin(null);
        setAvg(null);
        datalist.clear();
        refreshTable();
    }

    private void freq_input() {
        freqinputLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.freqinput, null);
        inputtext = (EditText) freqinputLayout.findViewById(R.id.editText);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("频率输入")
                .setView(freqinputLayout)
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        })
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                    float inputf = Float.valueOf(inputtext.getText().toString());

                                    pss.find_input(inputf);

                                } catch (Exception e) {
//                                    Toast.makeText(PinScanningShowWave.this,
//                                            "参数值输入格式错误或不可为空,请检查后重新输入",
//                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).create();
        dialog.show();
    }

}
