package com.joey.update;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by Joey on 2016/10/11.
 * 检测更新的服务
 */
public class UpdateService extends Service {

    private final IBinder binder = new MyBinder();
    private UpdateReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdateConsts.ACTION_INSTALL);
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        KLog.a("开启更新服务监听");
        super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class MyBinder extends Binder {

        /*返回service服务，方便activity中得到*/
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    public class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent)
                return;
            String action = intent.getAction();
            boolean checkout = intent.getBooleanExtra(UpdateConsts.ACTION_CHECKED_KEY, false);
//            KLog.a("action = " + action + ",flag = " + checkout);
            if (UpdateConsts.ACTION_INSTALL.equals(action)) {
                intent.setClass(context,ApkInstallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }
    }
}
