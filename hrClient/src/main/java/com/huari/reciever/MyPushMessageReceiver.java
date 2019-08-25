package com.huari.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.huari.client.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("xiao","xiaoxilaile");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "1")
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("消息内容：")
                    .setContentText(
//                            gets(intent.getStringExtra("msg"))
                            intent.getStringExtra("msg")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
// notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, builder.build());
        }
    }
}
