# MyJpush
Android极光8.0自定义通知声音

Android8.0以来通知确实用了NotificationChannel,项目中另一处的通知在8.0以上的手机有自定义的通知音,但是极光收到的推送则一直为默认的提示音。后来发现极光还未支持。从3.3.4才开始支持NotificationChannel。

Android极光8.0自定义通知声音:

1.升级极光sdk到3.3.4,并配置;
注意:
    1.项目的gradle文件里:
    //极光推送
    implementation 'cn.jiguang.sdk:jpush:3.3.6'
    implementation 'cn.jiguang.sdk:jcore:2.1.6'
    manifestPlaceholders = [
                JPUSH_PKGNAME: applicationId,
                JPUSH_APPKEY : "", //JPush上注册的包名对应的appkey.
                JPUSH_CHANNEL: "developer-default", //暂时填写默认值即可.
        ]
    2.manifest文件:
    <!-- 极光推送 -->
        <!-- 这个Service要继承JCommonService -->
        <service android:name=".jpush.PushService"
            android:process=":pushcore">
            <intent-filter>
                <action android:name="cn.jiguang.user.service.action" />
            </intent-filter>
        </service>
        <!-- User defined.  For test only  用户自定义接收消息器,3.0.7开始支持,目前新tag/alias接口设置结果会在该广播接收器对应的方法中回调-->
        <receiver android:name=".jpush.MyJPushMessageReceiver">
            <intent-filter>
                <action android:name="cn.jpush.android.intent.RECEIVE_MESSAGE" />
                <category android:name="com.example.myapplication"></category>
            </intent-filter>
        </receiver>
        <!-- User defined.  For test only  用户自定义的广播接收器-->
        <receiver
            android:name=".jpush.MyReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter>
                <action android:name="cn.jpush.android.intent.REGISTRATION" /> <!--Required  用户注册SDK的intent-->
                <action android:name="cn.jpush.android.intent.MESSAGE_RECEIVED" /> <!--Required  用户接收SDK消息的intent-->
                <action android:name="cn.jpush.android.intent.NOTIFICATION_RECEIVED" /> <!--Required  用户接收SDK通知栏信息的intent-->
                <action android:name="cn.jpush.android.intent.NOTIFICATION_OPENED" /> <!--Required  用户打开自定义通知栏的intent-->
                <action android:name="cn.jpush.android.intent.CONNECTION" /><!-- 接收网络变化 连接/断开 since 1.6.3 -->
                <category android:name="com.example.myapplication" />
            </intent-filter>
        </receiver>
        3.Application注册极光:
        JPushInterface.setDebugMode(false);
        JPushInterface.init(getApplicationContext());
2.在res文件夹下新建raw目录,放入MP3文件(提示音);
3.自定义广播接收器,在接收到通知时自定义声音:
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
        }
