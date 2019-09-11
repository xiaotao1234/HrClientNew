package com.huari.client;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;
import struct.JavaStruct;
import struct.StructException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.Base.AnalysisBase;
import com.huari.adapter.ItuAdapterOfListView;
import com.huari.adapter.PagerAdapterOfSpectrum;
import com.huari.commandstruct.PPFXRequest;
import com.huari.commandstruct.PinPuParameter;
import com.huari.commandstruct.StopTaskFrame;
import com.huari.dataentry.GlobalData;
import com.huari.dataentry.LogicParameter;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Parameter;
import com.huari.dataentry.Station;
import com.huari.tools.ByteFileIoUtils;
import com.huari.tools.MyTools;
import com.huari.tools.Parse;
import com.huari.tools.RealTimeSaveAndGetStore;
import com.huari.tools.SysApplication;
import com.huari.ui.CustomProgress;
import com.huari.ui.ShowWaveView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class HistoryAnalysisActivity extends AnalysisBase {

    PowerManager pm;
    PowerManager.WakeLock wl;

    boolean cq;// 是否显示场强


    public static int IQDATA = 0x4;
    public static int AUDIODATA = 0x5;
    public static int PARAMETERREFRESH = 0x6;
    public static int FIRSTAUDIOCOME = 0x9;
    public static int tempLength = 409600;
    public static Queue<byte[]> queue;

    private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";

    private static long AUDIO_SAMPLE_RATE = 44100;
    private static int AUDIO_CHANNL = 2;

    private String AudioName = "";        //原始音频数据文件 ，麦克风
    private String NewAudioName = "";     //可播放的音频文件
    private static File recordFile;

    ShowWaveView waveview;
    com.huari.ui.PartWaveShowView showwave;
    ViewPager viewpager;
    ItuAdapterOfListView listAdapter;
    PagerAdapterOfSpectrum spectrumAdapter;
    ListView itulistview;
    LinearLayout ituLinearLayout;
    ArrayList<View> viewlist;

    public static ArrayList<byte[]> audiolist1, audiolist2;
    public static boolean firstaudio = true;

    boolean partispause, fullispause = true;
    ArrayList<Parameter> ap;
    float startFreq = 0f, endFreq = 0f, pStepFreq = 0f, centerFreq = 0f,
            daikuan = 0f;
    float halfSpectrumsWide;// 频谱带宽的一半
    String logicId;
    String txname;
    MenuItem mitem;

    static byte[] info;


    View parentview;
    String[] namesofitems, advanceditems, generalparent, generaletdata;
    //private int generalindex;
    String[][] generalchild;
    TextView normaltextview, advancedtextview, titlebarname, stationtextview,
            devicetextview;

    int offset, displaywidth, barwidth;
    String stationname = null, devicename = null, stationKey = null;
    //static String mydevicename;// 仅在播放声音创建声音播放器时使用。
    ActionBar actionbar;

    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1);
    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 2);
    String tempstationname, tempdevicename;

    Socket socket;// 用来接收数据
    OutputStream outs;
    InputStream ins;
    HistoryAnalysisActivity.IniThread inithread;

    static Parameter centerParameter;
    static Parameter filterSpanParameter;
    static Parameter spectrumParameter;

    boolean showMax, showMin, showAvg;

    // 解析声音相关的东西
    private String filename;
    private SharedPreferences sharepre;
    private SharedPreferences.Editor shareEditor;
    private CustomProgress customProgress;
    private ImageView contorl;
    ImageView previousButton;
    ImageView nextButton;

    //public static PlayAudioThread playAudioThread;

    @SuppressWarnings("deprecation")
    public static void createAudioPlay(int frequency, byte bitcounts,
                                       short channelcount, int framelength) {

        AUDIO_SAMPLE_RATE = frequency;
        AUDIO_CHANNL = channelcount;

        if (bitcounts == 0 && channelcount == 1) {
            audioBuffersize = AudioTrack
                    .getMinBufferSize(frequency, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_8BIT);
            audioBuffersize = Math.max(audioBuffersize, framelength);

            at = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT, audioBuffersize * 4,
                    AudioTrack.MODE_STREAM);
            audioBuffer = new byte[audioBuffersize];
            tempAudioBuffer = new byte[tempLength];
        } else if (bitcounts == 1 && channelcount == 1) {
            audioBuffersize = AudioTrack.getMinBufferSize(frequency,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audioBuffersize = Math.max(audioBuffersize, framelength);

            at = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, audioBuffersize,
                    AudioTrack.MODE_STREAM);
            audioBuffer = new byte[audioBuffersize];
            tempAudioBuffer = new byte[tempLength];
            at.play();
        } else if (bitcounts == 0 && channelcount == 2) {
            audioBuffersize = AudioTrack.getMinBufferSize(frequency,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_8BIT);
            audioBuffersize = Math.max(audioBuffersize, framelength);

            at = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_8BIT, audioBuffersize,
                    AudioTrack.MODE_STREAM);
            audioBuffer = new byte[audioBuffersize];
            tempAudioBuffer = new byte[tempLength];
        } else if (bitcounts == 1 && channelcount == 2) {
            audioBuffersize = AudioTrack.getMinBufferSize(frequency,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audioBuffersize = Math.max(audioBuffersize, framelength);

            at = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT, audioBuffersize,
                    AudioTrack.MODE_STREAM);
            audioBuffer = new byte[audioBuffersize];
            tempAudioBuffer = new byte[tempLength];
        }
    }

    class IniThread extends Thread {
        public void run() {
            try {
                socket = new Socket(GlobalData.mainIP, GlobalData.port2);
                socket.setSoTimeout(5000);
                ins = socket.getInputStream();
                outs = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendClose() {
        Thread thread = new Thread(() -> {
            StopTaskFrame st = new StopTaskFrame();
            st.length = 2;
            st.functionNum = 16;
            st.tail = 22;
            byte[] b;
            try {
                b = JavaStruct.pack(st);
                outs.write(b);
                outs.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_analysis);
        customProgress = findViewById(R.id.video_progress);
        sharepre = getSharedPreferences("myclient", MODE_PRIVATE);
        shareEditor = sharepre.edit();
        inithread = new HistoryAnalysisActivity.IniThread();
        inithread.start();
        SysApplication.getInstance().addActivity(this);
        GlobalData.willplay = false;
        Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
        pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
        ituLinearLayout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.listviewwithitu, null);
        itulistview = ituLinearLayout.findViewById(R.id.itulistview);
        viewlist = new ArrayList<>();
        spectrumAdapter = new PagerAdapterOfSpectrum(viewlist);
        if (GlobalData.ituHashMap == null) {
            GlobalData.ituHashMap = new HashMap<>();
        }
        listAdapter = new ItuAdapterOfListView(HistoryAnalysisActivity.this,
                GlobalData.ituHashMap);
        viewpager = findViewById(R.id.firstviewpager);
        itulistview.setAdapter(listAdapter);
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
        Intent intent = getIntent();
        filename = intent.getStringExtra("filename");
        String mm = sharepre.getString(filename, null);
        stationname = mm.substring(0, mm.indexOf("|"));
        devicename = mm.substring(mm.indexOf("|") + 1, mm.indexOf("||"));
        stationKey = mm.substring(mm.indexOf("||") + 2, mm.indexOf("|||"));
        mm.substring(mm.indexOf("|||") + 3, mm.indexOf("||||"));
        mm.substring(mm.indexOf("||||") + 4, mm.indexOf("|||||"));
        logicId = mm.substring(mm.indexOf("|||||") + 5, mm.length());
        LinearLayout titlebar = (LinearLayout) getLayoutInflater().inflate(
                R.layout.actionbarview, null);
        stationtextview = (TextView) titlebar.findViewById(R.id.name1);
        devicetextview = (TextView) titlebar.findViewById(R.id.name2);
        // Button bn=(Button)titlebar.findViewById(R.id.zhuanxiang);
        customProgress.setProgress(0);
        customProgress.setProgressListener(progress -> {
            Log.d("xiaotao", String.valueOf(progress));
            if (RealTimeSaveAndGetStore.thread.isAlive()) {
                RealTimeSaveAndGetStore.progressFlg = true;
                RealTimeSaveAndGetStore.progress = (int) progress;
            } else {
                RealTimeSaveAndGetStore.ParseLocalWrap(filename, 2, handle);
                RealTimeSaveAndGetStore.progressFlg = true;
                RealTimeSaveAndGetStore.progress = (int) progress;
            }
        });
        contorl = findViewById(R.id.play_control);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        previousButton.setOnClickListener(v -> RealTimeSaveAndGetStore.previousFrame(HistoryAnalysisActivity.this));
        nextButton.setOnClickListener(v -> RealTimeSaveAndGetStore.nextFrame(HistoryAnalysisActivity.this));
        contorl.setOnClickListener(v -> RealTimeSaveAndGetStore.pauseOrResume());
        stationtextview.setText(stationname);
        devicetextview.setText(devicename);
        showwave = (com.huari.ui.PartWaveShowView) getLayoutInflater().inflate(
                R.layout.a, null);
        viewlist.add(showwave);
        viewlist.add(ituLinearLayout);
        viewpager.setAdapter(spectrumAdapter);
        parentview = getLayoutInflater().inflate(
                R.layout.activity_spectrums_analysis, null);
        waveview = findViewById(R.id.buildshowwaveview);
        handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                fullispause = false;
                partispause = false;
                try {
                    if (msg.what == DIANPINGDATA && fullispause == false
                            && partispause == false) {
                        if (cq == false) {
                            showwave.refresh(GlobalData.dianping);
                        } else {
                            try {
                                showwave.refresh(GlobalData.dianping
                                        + GlobalData.yinzi[0]);
                            } catch (NullPointerException e) {
                                showwave.refresh(GlobalData.dianping);
                            }
                        }
                    } else if (msg.what == 0x6)// 参数更新了，可能涉及到坐标的变化，所以需要刷新界面
                    {
                        if (filterSpanParameter != null) {
                            halfSpectrumsWide = Float
                                    .parseFloat(filterSpanParameter.defaultValue) / 2000f;
                        }
                        if (spectrumParameter != null) {
                            halfSpectrumsWide = Float
                                    .parseFloat(spectrumParameter.defaultValue) / 2000f;
                        }
                        startFreq = (float) (Math.floor(Float
                                .parseFloat(centerParameter.defaultValue)
                                * 1000f - halfSpectrumsWide * 1000)) / 1000;
                        endFreq = (Float
                                .parseFloat(centerParameter.defaultValue) * 1000f + halfSpectrumsWide * 1000) / 1000;
                        waveview.setM(null);
                        waveview.setMax(null);
                        waveview.setMin(null);
                        waveview.setAvg(null);
                        waveview.setF(startFreq, endFreq, pStepFreq);
                        GlobalData.ituHashMap.clear();
                        listAdapter.notifyDataSetChanged();
                    } else if (msg.what == 0x10) {
                        waveview.setHave(true);
                        waveview.setM(GlobalData.Spectrumpinpu);
                        if (showMax) {
                            waveview.setMax(GlobalData.Spectrummax);
                        } else {
                            waveview.setMax(null);
                        }
                        if (showMin) {
                            waveview.setMin(GlobalData.Spectrummin);
                        } else {
                            waveview.setMin(null);
                        }
                        if (showAvg) {
                            waveview.setAvg(GlobalData.Spectrumavg);
                        } else {
                            waveview.setAvg(null);
                        }
                        waveview.postInvalidate();
                        waveview.setFandC(startFreq, endFreq,
                                GlobalData.Spectrumpinpu.length);
                    } else if (msg.what == AUDIODATA)  //=0x5   声音
                    {
                        synchronized (synObject) {
                            at.write(audioBuffer, 0, audioBuffer.length);
                            at.flush();
                            at.play();
                            at.stop();
                            while (at.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                                Thread.sleep(1);
                            }
                            audioindex = 0;
                        }
                    } else if (msg.what == ITUDATA) {
                        listAdapter.notifyDataSetChanged();
                    } else if (msg.what == 121) {
                        customProgress.setProgress((Integer) msg.obj);
                    }else if(msg.what ==34){
                        AfterGetStation((Station) msg.obj);
                    }
                } catch (Exception e) {

                }


            }
        };
        RealTimeSaveAndGetStore.deserializeFlyPig(filename,handle);
    }

    private void AfterGetStation(Station stationF) {
        ArrayList<MyDevice> am = stationF.devicelist;
        HashMap<String, LogicParameter> hsl = null;
        for (MyDevice md : am) {
            if (md.name.equals(devicename)) {
                hsl = md.logic;
            }
        }
        LogicParameter currentLP = hsl.get(logicId);// 获取频谱分析相关的数据，以便画出初始界面
        ap = currentLP.parameterlist;

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
        startFreq = (float) (Math.floor(centerFreq * 1000f - halfSpectrumsWide
                * 1000)) / 1000;
        endFreq = (centerFreq * 1000f + halfSpectrumsWide * 1000) / 1000;
        waveview.setF(startFreq, endFreq, pStepFreq);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 1) {
            Bundle bundle = data.getExtras();
            stationtextview.setText(bundle.getString("stname"));
            devicetextview.setText(bundle.getString("dename"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        wl.acquire();
//        cq = true;
        fullispause = false;
        partispause = false;
        RealTimeSaveAndGetStore.ParseLocalWrap(filename, 2, handle);
        super.onResume();
    }

    @Override
    protected void onPause() {
        wl.release();
        super.onPause();
        ByteFileIoUtils.runFlag = false;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取麦克风输入的原始音频流文件路径
     *
     * @return
     */
    public static String getRawFilePath() {
        String mAudioRawPath = "";
        if (isSdcardExit()) {
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioRawPath = fileBasePath + "/" + AUDIO_RAW_FILENAME;
        }

        return mAudioRawPath;
    }

    public static String getWavFilePath() {
        String mAudioWavPath = "";
        if (isSdcardExit()) {
            String fileBasePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fileName = "rec|" + df.format(new Date()).replaceAll(" ", "|") + ".wav";
            mAudioWavPath = fileBasePath + File.separator + "Audio" + File.separator + fileName;
            if (!(new File(mAudioWavPath).exists())) {
                new File(mAudioWavPath).mkdirs();
            }
        }
        return mAudioWavPath;
    }

    /**
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
     * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
     * 自己特有的头文件。
     */
}
