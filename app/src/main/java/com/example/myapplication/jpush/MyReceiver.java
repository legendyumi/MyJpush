package com.example.myapplication.jpush;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: ");

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");
            String rid = JPushInterface.getRegistrationID(context);
            if (!rid.isEmpty()) {
                Log.e("rid", rid);
            } else {
                Toast.makeText(context, "Get registration fail, JPush init failed!", Toast.LENGTH_SHORT).show();
            }
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知");
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            openNotification(context, bundle);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    //自定义声音
    private void processCustomMessage(Context context, Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            //注意id,保持唯一,跟手机里其他APP的channelid一样时,声音会失效
            NotificationChannel channel = new NotificationChannel("TEST", "自定义通知", NotificationManager.IMPORTANCE_HIGH);
            String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e("返回信息", extras);
            if (alert != null && !alert.equals("")) {
                String type = "";
                try {
                    JSONObject extrasJson = new JSONObject(extras);
                    if (extrasJson.has("type")) {
                        type = extrasJson.getString("type");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Unexpected: extras is not a valid json", e);
                    return;
                }
                if (type.equals("0") || type.equals("1") || type.equals("3") || type.equals("4") || type.equals("7")) {
                    channel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notice),
                            new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());
                }
            }
            manager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(context, "TEST")
                    .setOnlyAlertOnce(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
            //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
            notification.setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher);
            String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e("返回信息", extras);
            if (alert != null && !alert.equals("")) {
                String type = "";
                try {
                    JSONObject extrasJson = new JSONObject(extras);
                    if (extrasJson.has("type")) {
                        type = extrasJson.getString("type");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Unexpected: extras is not a valid json", e);
                    return;
                }
                if (type.equals("0")) {
                    notification.setSound(
                            Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notice));
                } else if (type.equals("1")) {
                    notification.setSound(
                            Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notice));
                } else if (type.equals("3") || type.equals("4") || type.equals("7")) {
                    notification.setSound(
                            Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notice));
                }
            }
            //最后刷新notification是必须的
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification.build());
        }
    }

    private void openNotification(Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.e(extras, "返回信息");
        String type = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            type = extrasJson.optString("type");
        } catch (Exception e) {
            Log.w(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }
        if ("0".equals(type)) {
           /* Intent mIntent = new Intent(context, SupplyDetailActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);*/
        } else if ("1".equals(type)) {
           /* Intent mIntent = new Intent(context, OrderActivity.class);
            mIntent.putExtras(bundle);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);*/
        }
    }
}
