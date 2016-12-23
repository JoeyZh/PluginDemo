package com.joey.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.joey.utils.ResourcesUnusualUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/5/10 0010.
 * modified by Joey  重命名，之前单词拼写错误 2016-10-11
 */
public class NotificationUtils {

    private NotificationManager mNotificationManager = null;
    private Context context;

    public NotificationUtils(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context = context;
        ResourcesUnusualUtil.register(context);
    }

    Notification notification;
    RemoteViews remoteViews;

    /**
     * 显示通知
     *
     * @param name 文件名称
     */
    public void showNotifation(int icon,String name) {
        notification = new Notification(icon, name, System.currentTimeMillis());
        //滚动文字
        notification.tickerText = name + "开始下载";
//        //显示时间
        notification.when = System.currentTimeMillis();
        //设置图图标
        notification.icon = icon;
        //设置通知特性
        notification.flags = Notification.FLAG_NO_CLEAR;

        // 创建 远程试图
        remoteViews = new RemoteViews(context.getPackageName(),ResourcesUnusualUtil.getLayoutId("item_notifaction"));
        notification.contentView = remoteViews;
        //   发出通知
        mNotificationManager.notify(1, notification);
    }

    /**
     * 取消通知
     */

    public void cancleNotifaction() {
        mNotificationManager.cancel(1);
    }

    /**
     * 更新下载进度
     */
    public void upDateProgress(int progress, int max) {
        if (notification != null) {
            notification.contentView.setProgressBar(ResourcesUnusualUtil.getId("pb_progress"), max, progress, false);
            notification.contentView.setTextViewText(ResourcesUnusualUtil.getId("tv_progress"),  progress*100 / max + "%");
            mNotificationManager.notify(1, notification);
        }
    }

    /**
     * 下载完成时
     */
    public void onSuccess(File file) {
        //设置点击通知栏操作
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notification.contentIntent = pendingIntent;
        notification.contentView.setTextViewText(ResourcesUnusualUtil.getId("tv_loging"), "          点击安装");
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notification);
    }
}
