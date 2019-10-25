package com.huari.client;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.dataentry.MessageEvent;
import com.huari.dataentry.MusicFileList;
import com.huari.renderer.BarGraphRenderer;
import com.huari.tools.FileOsImpl;
import com.huari.tools.SysApplication;
import com.huari.ui.CustomProgress;
import com.huari.ui.VisualizerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    volatile boolean playOrNotTab = true;
    MediaPlayer mediaPlayer;
    String fileName;
    int filePosition = -1;
    Thread thread;
    private CustomProgress customProgress;
    private VisualizerView visualizerView;
    private TextView musicLength;
    private TextView playPlan;
    private TextView videoName;
    private ImageView previousButton;
    private ImageView playControl;
    private ImageView mainBack;
    private ImageView nextButton;
    private ImageView back;
    private List<File> file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("xiaoxiao", "oncreate invoke");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ui();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("xiaoxiao", "onstop invoke");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("xiaoxiao", "onpause invoke");
        releaseResource();
        mediaPlayer.pause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        Log.d("xiaoxiao", "onstart invoke");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d("xiaoxiao", "ondestory invoke");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d("xiaoxiao", "onresume invoke");
        super.onResume();
        init(fileName);
        SysApplication.fileOs.save(fileName,new File(fileName).getName(),4);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMoonEvent(MessageEvent messageEvent) {
        fileName = messageEvent.getMessageString();
        Log.d("xiaoxiao", fileName);
//        SysApplication.fileOs.addRecentFile(fileName,0);
        Log.d("xiaoxiao", "comecome");
        filePosition = messageEvent.getFilePosition();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void FileListCome(MusicFileList musicFileList) {
        file = musicFileList.getStringList();
    }

    public void releaseResource() {
        if (thread != null) {
            thread.interrupt();
        }
        if (visualizerView != null) {
            visualizerView.release();
        }
    }

    public void previousMusic() {
        releaseResource();
        if (file != null && filePosition != -1) {
            int i = 0;
            do{
                filePosition = (filePosition - 1 < 0 ? file.size() - 1 : filePosition - 1);
                i--;
            }while (!file.get(filePosition).getName().contains("RE")&&i<file.size());
            if(i<file.size()){
                videoName.setText(file.get(filePosition).getAbsolutePath());
                SysApplication.fileOs.setCurrentFile(file.get(filePosition));
                init(SysApplication.fileOs.getCurrentFile().getAbsolutePath());
            }else {
                Toast.makeText(this,"未找到上一首",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"未找到上一首",Toast.LENGTH_SHORT).show();
        }
//        List<File> currentFiles = SysApplication.fileOs.getFiles();
//        filePosition = filePosition - 1 < 0 ? currentFiles.size() - 1 : filePosition - 1;
//        SysApplication.fileOs.setCurrentFile(currentFiles.get(filePosition));
//        init(SysApplication.fileOs.getCurrentFile().getAbsolutePath());
    }

    public void nextMusic() {
        thread.interrupt();
        visualizerView.release();
        if (file != null && filePosition != -1) {
            int i = 0;
            do{
                filePosition = (filePosition + 1 > (file.size()-1) ? 0 : filePosition + 1);
                i++;
            }while (!file.get(filePosition).getName().contains("RE")&&i<file.size());
            if(i<file.size()){
                videoName.setText(file.get(filePosition).getAbsolutePath());
                SysApplication.fileOs.setCurrentFile(file.get(filePosition));
                init(SysApplication.fileOs.getCurrentFile().getAbsolutePath());
            }else {
                Toast.makeText(this,"未找到下一首",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"未找到下一首",Toast.LENGTH_SHORT).show();
        }
//        List<File> currentFiles = SysApplication.fileOs.getFiles();
//        filePosition = filePosition + 1 >= currentFiles.size() ? 0 : filePosition + 1;
//        SysApplication.fileOs.setCurrentFile(currentFiles.get(filePosition));
//        init(SysApplication.fileOs.getCurrentFile().getAbsolutePath());
    }

    public void playControl() {
        if (playOrNotTab == false) {
            playclick();
            playControl.setImageResource(R.drawable.play_icon);
            playOrNotTab = true;
        } else {
            mediaPlayer.pause();
            playControl.setImageResource(R.drawable.stop_icon);
            playOrNotTab = false;
        }
    }

    private void ui() {
        mainBack = findViewById(R.id.main_back);
        previousButton = findViewById(R.id.previous_button);
        playControl = findViewById(R.id.play_control);
        nextButton = findViewById(R.id.next_button);
        back = findViewById(R.id.back);
        videoName = findViewById(R.id.video_name);
        playPlan = findViewById(R.id.play_plan);
        musicLength = findViewById(R.id.music_length);
        visualizerView = findViewById(R.id.visualizerView);
        customProgress = findViewById(R.id.video_progress);
        videoName.setSystemUiVisibility(View.INVISIBLE);
        customProgress.setProgress(0);
        back.setOnClickListener(v -> finish());
        customProgress.setProgressListener(progress -> mediaPlayer.seekTo((int) (progress * mediaPlayer.getDuration() / 100)));
        previousButton.setOnClickListener(v -> previousMusic());
        nextButton.setOnClickListener(v -> nextMusic());
        playControl.setOnClickListener(v -> playControl());
    }

    private void playclick() {
        startplayer();
        videoName.setText(SysApplication.fileOs.getCurrentFile().getAbsolutePath());
        thread = new Thread(runnable);
        thread.start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Thread.sleep(100);
                    Message message = Message.obtain();
                    message.what = 1;
                    if (mediaPlayer != null) {
                        message.obj = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(message);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    playPlan.setText(String.valueOf(SysApplication.timeTools.timetrans((Integer) msg.obj / 1000)));
                    if (mediaPlayer.getDuration() != 0) {
                        customProgress.setProgress((Integer) msg.obj / (mediaPlayer.getDuration() / 100));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(PlayerActivity.this, "没有获得相关权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(PlayerActivity.this, "没有获得相关权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(PlayerActivity.this, "没有获得相关权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    visualizerView.link(mediaPlayer, playControl);
                    // Start with just line renderer
                    addBarGraphRenderers();
                } else {
                    Toast.makeText(PlayerActivity.this, "没有获得相关权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void init(String fileName) {
        mediaPlayerInit(fileName);
        permissionCheck();
        playclick();
    }

    private void permissionCheck() {
        SysApplication.permissionManager.requestPermission(PlayerActivity.this, Manifest.permission.RECORD_AUDIO, 3, () -> {
            visualizerView.link(mediaPlayer, playControl);
            addBarGraphRenderers();
        });
    }

    private void mediaPlayerInit(String fileName) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            SysApplication.fileOs.setCurrentFile(new File(fileName));
        } catch (IOException e) {
            Toast.makeText(PlayerActivity.this, "音频文件打开失败，请检查文件格式是否正确", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void addBarGraphRenderers() {
        //底部柱状条
        Paint paint = new Paint();
        paint.setStrokeWidth(12f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 56, 138, 252));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(4, paint, false);
        visualizerView.addRenderer(barGraphRendererBottom);

        //顶部柱状条
//        Paint paint2 = new Paint();
//        paint2.setStrokeWidth(30f);
//        paint2.setAntiAlias(true);
//        paint2.setColor(Color.argb(200, 181, 111, 233));
//        BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(9, paint2, true);
//        visualizerView.addRenderer(barGraphRendererTop);
    }

    private void startplayer() {
        if (mediaPlayer != null) {
            Log.d("xiao", "start");
        }
        mediaPlayer.start();
        musicLength.setText(SysApplication.timeTools.timetrans(mediaPlayer.getDuration() / 1000));
    }
}
