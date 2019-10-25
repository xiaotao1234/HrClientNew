package com.huari.client;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import struct.JavaStruct;
import struct.StructException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.NetMonitor.WindowController;
import com.huari.NetMonitor.WindowHelper;
import com.huari.adapter.HistoryShowWindowAdapter;
import com.huari.commandstruct.PPFXRequest;
import com.huari.commandstruct.PinPuParameter;
import com.huari.commandstruct.StopTaskFrame;
import com.huari.dataentry.ForADataInformation;
import com.huari.dataentry.GlobalData;
import com.huari.dataentry.LogicParameter;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Parameter;
import com.huari.dataentry.Station;
import com.huari.dataentry.Type;
import com.huari.tools.ByteFileIoUtils;
import com.huari.tools.MyTools;
import com.huari.tools.Parse;
import com.huari.tools.RealTimeSaveAndGetStore;
import com.huari.tools.SysApplication;
import com.huari.ui.CustomProgress;
import com.huari.ui.DataSave;
import com.huari.ui.Disk;
import com.huari.ui.HColumns;
import com.huari.ui.MyData;
import com.huari.ui.VColumns;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryDFActivity extends AppCompatActivity {

    public static final int VCFRESH = 1;
    public static final int DISKREFRESH = 2;
    public static final int NETREFRESH = 3;
    public static final int FOURREFRESH = 4;
    public static final int PARTFRESH = 8;
    public static final int PARAMETERSREFRESH = 10;// 有参数更新了

    String prefixion = "prefixion";
    SharedPreferences sharepre;
    SharedPreferences.Editor shareEditor;
    private String[] datas = {"选项1", "选项2", "选项3", "选项4", "选项5"};

    int i = 0;
    int m = 0;// 用以控制发送开始命令时，如果异常，多试几次的次数
    float lan, lon;

    Timer timer;
    HColumns hcs;
    CustomProgress customProgress;
    TextView alllength;
    TextView readnow;
    VColumns vcs;
    com.huari.ui.FourModeView fmv; //表格View
    com.huari.ui.ShowWaveViewOfDDF showwaveview;
    Disk disks;
    View p;
    ActionBar actionbar;
    LinearLayout l;
    boolean pause = true;
    int e, q, dianping;
    float absolutedegree, relativedegree;
    String showmode, tongjimode, jiaodumode;
    float startFreq = 0f, endFreq = 0, pStepFreq = 0f, centerFreq = 0f,
            daikuan = 0f;
    float halfSpectrumsWide = 0.075f;// 频谱带宽的一半

    public static Handler handler;
    String stationname = null, devicename = null, stationKey = null;
    String logicId;
    String txname;
    String logicindex;// 理论上逻辑参数的类型应该是“DDF”，但是由于需要填满后面的空余字节，所以实际上得到的类型是“DDF$%&#*&#”。
    // 此变量即用来表示这个带有乱码的字符串。
    ArrayList<Parameter> ap;

    Socket s;// 用来接收数据
    OutputStream outs;
    InputStream ins;
    MyThread mythread;
    IniThread inithread;
    boolean runmyThread = true;

    MenuItem mitem;
    TextView stationtextview;
    TextView devicetextview;
    ImageView showStation;

    Parameter centerParameter;
    Parameter filterSpanParameter;// 次选带宽
    Parameter spectrumParameter;// 首选带宽

    public static boolean saveFlag = false;
    ExecutorService executorService = Executors.newCachedThreadPool();
    public static Queue<byte[]> queue;
    private String filename;
    private ImageView contorl;
    ImageView previousButton;
    ImageView nextButton;
    private List<String> names;
    private List<String> defaultValues;
    private List<Parameter> parameterList;
    private ImageView back;

    class IniThread extends Thread {
        public void run() {
            try {
                // s = new Socket(GlobalData.mainIP, 5012);
                s = new Socket(GlobalData.mainIP, GlobalData.port2);
                ins = s.getInputStream();
                outs = s.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private void startcmd() {

        byte[] bbb = iRequestInfo();
        // System.out.println("客户端发送的数据长度是"+bbb.length);
        try {
            outs.write(bbb);
            outs.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendClose() {
        StopTaskFrame st = new StopTaskFrame();
        st.functionNum = 46;
        st.length = 2;
        st.tail = 22;
        byte[] b;
        try {
            b = JavaStruct.pack(st);
            outs.write(b);
            outs.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyThread extends Thread {

        private void sendStartCmd() {
            try {
                m++;
                byte[] bbb = iRequestInfo();
                outs.write(bbb);
                outs.flush();
            } catch (NullPointerException e) {
                System.out.println("sendStartCmd内部的NullPointException");
                ByteFileIoUtils.runFlag = false;
                if (m < 10) {
                    sendStartCmd();
                } else {
                    Toast.makeText(HistoryDFActivity.this,
                            "发送开始命令失败，请退出该功能并重试一次吧", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                // System.out.println("sendStartCmd内部发生了异常");
                ByteFileIoUtils.runFlag = false;
            }
        }

        private void sendEndCmd() {
            ByteFileIoUtils.runFlag = false;
            m = 0;
            StopTaskFrame st = new StopTaskFrame();
            st.functionNum = 46;
            st.length = 2;
            byte[] b;
            try {
                b = JavaStruct.pack(st);
                outs.write(b);
                outs.flush();
                // Log.i("发送了","停止命令");
            } catch (Exception e) {
                e.printStackTrace();
                // Log.i("停止","出问题了");
            }
        }

        public void run() {
            try {
                int available = 0;
                int segment = 0;
                byte[] info = null;
                long time = 0;

                while (available == 0 && runmyThread) {
//                    SysApplication.byteFileIoUtils.readFile("nba");
                    available = ins.available();
                    if (available > 0) {
                        try {
                            info = new byte[available];
//                            ParseLocalDdfData("nba");
                            ins.read(info);
                            Parse.parseDDF(info);
                        } catch (Exception e) {
                            // System.out.println("开始接收DDF数据，解析发生了异常，定位到DDF的Activity的225行");
                        }
                        if (saveFlag == true) {
                            time = RealTimeSaveAndGetStore.SaveAtTime(available, info, time, 1);//给数据加一个时间的包头后递交到缓存队列中
                        }
                        available = 0;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showStation.setSystemUiVisibility(View.INVISIBLE);
//        startWindow();
        pause = false;
        RealTimeSaveAndGetStore.ParseFlg = false;
        RealTimeSaveAndGetStore.StopFlag = false;
        RealTimeSaveAndGetStore.progressFlg = false;
        RealTimeSaveAndGetStore.ParseLocalWrap(filename, 1, handler);
    }

    private void popWindow(View view) {
        // TODO: 2016/5/17 构建一个popupwindow的布局
        View popupView = HistoryDFActivity.this.getLayoutInflater().inflate(R.layout.popupwindow, null);
        // TODO: 2016/5/17 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
        RecyclerView lsvMore = popupView.findViewById(R.id.lsvMore);
        List<String> list = new ArrayList<>();
        list.add("ddada");
        list.add("ddada");
        list.add("ddada");
        list.add("ddada");
        list.add("ddada");
        HistoryShowWindowAdapter historyShowWindowAdapter = new HistoryShowWindowAdapter(names, defaultValues);
        lsvMore.setLayoutManager(new LinearLayoutManager(this));
        lsvMore.setAdapter(historyShowWindowAdapter);
        // TODO: 2016/5/17 创建PopupWindow对象，指定宽度和高度
        PopupWindow window = new PopupWindow(popupView, 500, 600);
        // TODO: 2016/5/17 设置动画
        window.setAnimationStyle(R.style.popup_window_anim);
        // TODO: 2016/5/17 设置背景颜色
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // TODO: 2016/5/17 设置可以获取焦点
        window.showAsDropDown(view, 0, 0, Gravity.BOTTOM);
        window.setFocusable(true);
        // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(true);
        // TODO：更新popupwindow的状态
        window.update();
        // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
    }

    @Override
    protected void onPause() {
        super.onPause();
        RealTimeSaveAndGetStore.StopFlag = false;
        RealTimeSaveAndGetStore.ParseFlg = false;
        Parse.setHandler(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_history);
            back = findViewById(R.id.back);
            back.setOnClickListener(v -> finish());
            sharepre = getSharedPreferences("myclient", MODE_PRIVATE);
            shareEditor = sharepre.edit();
            inithread = new IniThread();
            inithread.start();
            SysApplication.getInstance().addActivity(this);
            Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);// 捕获UncaughtException异常！！！！！！！！！！！
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int twidth = metric.widthPixels;
            int theight = metric.heightPixels;
            float density = metric.density;
            int densityDpi = metric.densityDpi;
            double dui = Math.sqrt(twidth * twidth + theight * theight);
            double x = dui / densityDpi;

            if (x >= 6.5) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
            }
            l = (LinearLayout) getLayoutInflater().inflate(
                    R.layout.actionbarview, null);
            Intent intent = getIntent();
            filename = intent.getStringExtra("filename");
//            String mm = sharepre.getString(filename, null);
//
//            stationname = mm.substring(0, mm.indexOf("|"));
//            devicename = mm.substring(mm.indexOf("|") + 1, mm.indexOf("||"));
//            stationKey = mm.substring(mm.indexOf("||") + 2, mm.indexOf("|||"));
//            mm.substring(mm.indexOf("|||") + 3, mm.indexOf("||||"));
//            mm.substring(mm.indexOf("||||") + 4, mm.indexOf("|||||"));
//            logicId = mm.substring(mm.indexOf("|||||") + 5, mm.length());
            stationtextview = l.findViewById(R.id.name1);
            devicetextview = l.findViewById(R.id.name2);
            customProgress = findViewById(R.id.video_progress);
            alllength = findViewById(R.id.music_length);
            readnow = findViewById(R.id.play_plan);
            contorl = findViewById(R.id.play_control);
            showStation = findViewById(R.id.station_button);
            showStation.setOnClickListener(v -> {
                popWindow(showStation);
//                View view = LayoutInflater.from(HistoryDFActivity.this).inflate(R.layout.pop_window_view,null,false);
//                PopupWindow popupWindow = new PopupWindow(view,100,200,true);
//                popupWindow.showAtLocation(LayoutInflater.from(HistoryDFActivity.this).inflate(R.layout.activity_history,null,false),1,200,200);
            });
            contorl.setOnClickListener(v -> RealTimeSaveAndGetStore.pauseOrResume(contorl));
            customProgress.setProgress(0);
            customProgress.setProgressListener(progress -> {
                pause = false;
                Log.d("xiaotao", String.valueOf(progress));
                if (RealTimeSaveAndGetStore.thread.isAlive()) {
                    RealTimeSaveAndGetStore.progressFlg = true;
                    RealTimeSaveAndGetStore.progress = (int) progress;
                    if (RealTimeSaveAndGetStore.thread.getState() == Thread.State.WAITING) {
                        synchronized (RealTimeSaveAndGetStore.person) {
                            RealTimeSaveAndGetStore.person.notify();
                        }
                    }
                } else {
                    RealTimeSaveAndGetStore.ParseLocalWrap(filename, 1, handler);
                    RealTimeSaveAndGetStore.progressFlg = true;
                    RealTimeSaveAndGetStore.progress = (int) progress;
                }
            });
            fmv = findViewById(R.id.buildcusli);
            fmv.setSystemUiVisibility(View.INVISIBLE);
            showwaveview = findViewById(R.id.ddfshowwaveview);
            vcs = findViewById(R.id.buildvcs);
            disks = findViewById(R.id.builddisk);
            previousButton = findViewById(R.id.previous_button_bn);
            nextButton = findViewById(R.id.next_button_bn);
            previousButton.setOnClickListener(v -> RealTimeSaveAndGetStore.previousFrame(HistoryDFActivity.this));
            nextButton.setOnClickListener(v -> RealTimeSaveAndGetStore.nextFrame(HistoryDFActivity.this));
            p = LayoutInflater.from(this).inflate(
                    R.layout.activity_frequencyscanning, null);
            vcs.setOnex0(75);
            tongjimode = getResources().getString(R.string.tongjimode);
            showmode = getResources().getString(R.string.showmode);
            jiaodumode = getResources().getString(R.string.jiaodumode);
            if (tongjimode.equals("幅度")) {
                fmv.tongjimodeswitch("fudu");
            } else if (tongjimode.equals("质量")) {
                fmv.tongjimodeswitch("zhiliang");
            } else if (tongjimode.equals("概率")) {
                fmv.tongjimodeswitch("gailv");
            }

            if (showmode.equals("图形")) {
                fmv.setGraphModeShow(true);
            } else if (showmode.equals("表格")) {
                fmv.setGraphModeShow(false);
            }

            if (jiaodumode.equals("正北")) {
                disks.setRischecked(false);
            } else if (jiaodumode.equals("相对")) {
                disks.setRischecked(true);
            }

//            Station stationF = GlobalData.stationHashMap.get(stationKey);
//            Iterator<String> it = GlobalData.stationHashMap.keySet().iterator();

            handler = new Handler() {
                @SuppressWarnings("unused")
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case NETREFRESH:// 来了频谱数据/
                            showwaveview.setHave(true);// 以下三行被屏蔽掉的地方3
                            showwaveview.setM(GlobalData.pinpu);
                            // showwaveview.setFandC((float)GlobalData.startfreq,(float)GlobalData.endfreq,GlobalData.count);
                            showwaveview.setFandC((float) GlobalData.startfreq,
                                    (float) GlobalData.endfreq, GlobalData.count);
                            break;
                        case PARTFRESH:
                            run();
                            break;

                        case PARAMETERSREFRESH:
                            showwaveview.setHave(false);
                            startFreq = (float) (Math.floor(Float
                                    .parseFloat(centerParameter.defaultValue)
                                    * 1000f - halfSpectrumsWide * 1000)) / 1000;
                            endFreq = (Float
                                    .parseFloat(centerParameter.defaultValue) * 1000f + halfSpectrumsWide * 1000) / 1000;
                            showwaveview.setF(startFreq, endFreq, pStepFreq);
                            break;
                        case 121:
                            if (first == true) {
                                first = false;
                                alllength.setText(String.valueOf(RealTimeSaveAndGetStore.allLength));
                            }
                            readnow.setText(String.valueOf(RealTimeSaveAndGetStore.allLength - RealTimeSaveAndGetStore.available));
                            customProgress.setProgress((Integer) msg.obj);
                            if ((Integer) msg.obj == 100) {
                                contorl.setBackgroundResource(R.drawable.stop_icon);
                                RealTimeSaveAndGetStore.StopFlag = true;
                            }
                            break;
                        case 34:
                            if (StationForViewDo((ForADataInformation) msg.obj)) return;
                            break;
                    }
                }
            };
            RealTimeSaveAndGetStore.deserializeFlyPig(filename, handler);//开始反序列化来完成Station这一数据结构的重建以用来初始化视图
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean first = true;

    private boolean StationForViewDo(ForADataInformation stationF) {//在反序列化完成后，开始用取到的Station信息来初始化视图
        try {
            if (stationF == null) {
                return true;
            }
            names = new ArrayList<>();
            defaultValues = new ArrayList<>();
            HashMap<String, LogicParameter> list = stationF.getDevice().getLogic();
            parameterList = list.get(stationF.getLogicId()).getParameterlist();
            if (parameterList != null) {
                for (Parameter parameter : parameterList) {
                    names.add(parameter.name);
                    defaultValues.add(parameter.defaultValue);
                }
            }
            logicId = stationF.getLogicId();
            LogicParameter currentLP = stationF.getDevice().logic.get(logicId);// 获取频谱分析相关的数据，以便画出初始界面
            ap = currentLP.parameterlist;
            stationtextview.setText(stationF.getStationName());
            devicetextview.setText(stationF.getDeviceName());
            for (Parameter p : ap) {
                if (p.name.equals("DemodulationSpan")) {
                    daikuan = Float.parseFloat(p.defaultValue);
                } else if (p.name.equals("StepFreq")) {
                    pStepFreq = Float.parseFloat(p.defaultValue);
                } else if (p.name.equals("CenterFreq")) {
                    centerFreq = Float.parseFloat(p.defaultValue);
                    centerParameter = p;
                } else if (p.name.equals("AntennaSelect")) {
                    txname = p.defaultValue;
                }
                if (p.name.equals("FilterSpan")) {
                    halfSpectrumsWide = Float.parseFloat(p.defaultValue) / 2000f;
                    filterSpanParameter = p;
                }
                if (p.name.equals("SpectrumSpan")) {
                    halfSpectrumsWide = Float.parseFloat(p.defaultValue) / 2000f;
                    spectrumParameter = p;
                }
            }
            startFreq = (float) (Math.floor(Float
                    .parseFloat(centerParameter.defaultValue)
                    * 1000f
                    - halfSpectrumsWide * 1000)) / 1000;
            endFreq = (Float.parseFloat(centerParameter.defaultValue) * 1000f + halfSpectrumsWide * 1000) / 1000;
            showwaveview.setF(startFreq, endFreq, pStepFreq);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void willExit() {
        DataSave.datamap.clear();
        fmv.clear();
        try {
            sendClose();
            Thread.sleep(50);
            s.close();
            if (mythread != null) {
                runmyThread = false;
                mythread.join();
                mythread = null;
            }
            if (disks != null) {
                disks.gc();
            }
            System.gc();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        willExit();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.singlefreactionbar, menu);
        MenuItem i11 = menu.findItem(R.id.single11);// 相对
        MenuItem i12 = menu.findItem(R.id.single12);// 正北
        MenuItem i21 = menu.findItem(R.id.single21);// 幅度
        MenuItem i22 = menu.findItem(R.id.single22);// 概率
        MenuItem i23 = menu.findItem(R.id.single23);// 质量
        MenuItem i24 = menu.findItem(R.id.single24);// 图像
        MenuItem i25 = menu.findItem(R.id.single25);// 表格
        mitem = menu.findItem(R.id.singlepause);
        if (tongjimode.equals("幅度")) {
            i21.setChecked(true);
        } else if (tongjimode.equals("质量")) {
            i23.setChecked(true);
        } else if (tongjimode.equals("概率")) {
            i22.setChecked(true);
        }
        ;
        if (showmode.equals("图像")) {
            i24.setChecked(true);
        } else if (showmode.equals("表格")) {
            i25.setChecked(true);
        }
        ;
        if (jiaodumode.equals("正北")) {
            i12.setChecked(true);
        } else if (jiaodumode.equals("相对")) {
            i11.setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private byte[] iRequestInfo() {
        byte[] request = null;
        boolean havetianxian = false;
        for (Parameter para : ap) {
            if ((para.name.contains("Anten"))) {
                havetianxian = true;
                break;
            }
        }
        PPFXRequest pr = null;
        try {
            pr = new PPFXRequest();
            // 帧体长度暂时跳过
            pr.functionNum = 18;
            if (stationKey == null) {
                // System.out.println("组装开始命令的方法中stationKey是空的，所以发生了异常");
            }
            pr.stationid = MyTools.toCountString(stationKey.trim(), 76)
                    .getBytes();
            pr.logicid = MyTools.toCountString(logicId.trim(), 76).getBytes();

            pr.devicename = MyTools.toCountString(devicename.trim(), 36)
                    .getBytes();
            pr.pinduancount = 0;
            pr.logictype = MyTools.toCountString("DDF", 16).getBytes();

            PinPuParameter[] parray = null;
            if (havetianxian) {
                parray = new PinPuParameter[ap.size() - 1];
            } else {
                parray = new PinPuParameter[ap.size()];
            }

            int z = 0;
            for (Parameter para : ap) {
                PinPuParameter pin = new PinPuParameter();
                if (!para.name.contains("AntennaSele")) {
                    pin.name = MyTools.toCountString(para.name.trim(), 36)
                            .getBytes();
                    pin.value = MyTools.toCountString(para.defaultValue.trim(),
                            36).getBytes();
                    parray[z] = pin;
                    z++;
                } else {
                    pr.tianxianname = MyTools.toCountString(
                            para.defaultValue.trim(), 36).getBytes();
                }

            }
            if (havetianxian) {
                pr.parameterslength = 72 * (ap.size() - 1);
            } else {
                pr.parameterslength = 72 * ap.size();
            }

            pr.length = pr.parameterslength + 247;
            pr.p = parray;
        } catch (NullPointerException e) {
            // System.out.println("组装开始命令的方法中出现空指针异常");
        } catch (Exception e) {
            // System.out.println("组装开始命令的方法中出现非空指针类异常");
        }

        try {
            request = JavaStruct.pack(pr);

        } catch (StructException e) {
            e.printStackTrace();
        }
        return request;
    }

    class ReceiveData extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            ProgressBar pb = new ProgressBar(getApplicationContext());
        }

        protected void onPostExecute(String result) {

        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep((long) params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "OK";
        }

        ;

    }

    public void run() {
        MyData mydata;
        try {
            if (pause == false) {
                dianping = GlobalData.DDFdianping;// new
                // Random().nextInt(121)-20;
                e = dianping;
                q = GlobalData.qua;
                absolutedegree = GlobalData.xiangdui + GlobalData.north;
                relativedegree = GlobalData.xiangdui;
                vcs.refresh(e, q);// 被屏蔽掉的地方2
                if (dianping >= vcs.getEthreshold() && q >= vcs.getQthreshold()) {
                    disks.refresh(relativedegree, absolutedegree);// 被屏蔽掉的地方4
                    DataSave.sum = DataSave.sum + 1;
                    if (DataSave.datamap.containsKey(absolutedegree)) {
                        mydata = DataSave.datamap.get(absolutedegree);
                        mydata.count = mydata.count + 1;
                        mydata.maxplitude = Math.max(mydata.maxplitude, e);
                        mydata.maxquality = Math.max(mydata.maxquality, q);
                    } else {
                        mydata = new MyData();
                        mydata.count = mydata.count + 1;
                        mydata.maxplitude = e;
                        mydata.maxquality = q;
                        mydata.reldegree = relativedegree;
                        DataSave.datamap.put(absolutedegree, mydata);
                    }
                    if (mydata.count >= DataSave.maxcount) {
                        DataSave.maxcount = mydata.count;
                        DataSave.MaxProdegree = absolutedegree;
                    }
                    if (mydata.maxplitude >= DataSave.maxpli) {
                        DataSave.maxpli = mydata.maxplitude;
                        DataSave.MaxPlidegree = absolutedegree;
                    }
                    if (mydata.maxquality >= DataSave.maxqua) {
                        DataSave.maxqua = mydata.maxquality;
                        DataSave.MaxQuadegree = absolutedegree;
                    }
                    fmv.refresh();// 被屏蔽掉的地方1
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("发生异常");
        }

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(
//                    this);
//            builder.setTitle("警告!");
//            builder.setMessage("确定要退出该功能吗？");
//            builder.setPositiveButton("确定",
//                    new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            willExit();
//                            Intent intent = new Intent(
//                                    this,
//                                    MainActivity.class);//CircleActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//            builder.setNegativeButton("取消", null);
//            builder.create();
//            builder.show();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg0 == 0 && arg1 == 1) {
            Bundle bundle = arg2.getExtras();
            stationtextview.setText(bundle.getString("stname"));
            devicetextview.setText(bundle.getString("dename"));
        }
    }
}
