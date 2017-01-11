package com.joey.update;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

//import com.ibg100.shop.Utils.KLog;
//import com.ibg100.shop.Utils.ShardPreferenceUtils;
import com.joey.utils.ResourcesUtils;

import java.io.File;

/**
 * Created by 安装提示 on 2016/12/09.
 * 安装更新包
 * 在任何情况下都可以弹出该窗口
 */
public class ApkInstallActivity extends FragmentActivity {

    private AlertDialog dlgInstall;
    private String saveUrlString = Environment.getExternalStorageDirectory() + UpdateConsts.UPDATE_APK_PATH + File.separator + "ibg100_release.apk";
    private ApkInstallActivity mContext;
    private boolean forceUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(ApkInstallActivity.this));
        ResourcesUtils.register(this);
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    protected void initData() {
        mContext = this;
        forceUpdate = getIntent().getBooleanExtra("forceUpdate", false);
        if (forceUpdate) {
            installApk();
            return;
        }
        createInstallDialog().show();

    }

    public void release() {
        dismissDialog(dlgInstall);
        setCancelable(dlgInstall, true);
    }

    private void setCancelable(AlertDialog dialog, boolean flag) {
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(flag);
            dialog.setCancelable(true);
        }
    }

    private void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File file = new File(saveUrlString);
//        notificationUtils.onSuccess(file);
        if (!file.exists()) {
//            KLog.a("安装包不存在");
            return;
        }
//        KLog.a("开始安装apk");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
//        KLog.a("开始安装apk完毕");
        SharedPreferences preferences = getSharedPreferences("share",
                Context.MODE_APPEND);
        preferences.edit()
                .remove("downloadCode")
                .commit();
        // 如果不加上这句的话在apk安装完成之后点击单开会崩溃
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    private AlertDialog createInstallDialog() {
//        ToastUtil.show(mContext, "创建安装Dialog");
        if (dlgInstall != null)
            return dlgInstall;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        dlgInstall = builder.create();
        View view = View.inflate(mContext,
                ResourcesUtils.getLayoutId("dlg_update_apk"),
                null);
        TextView tvContent = (TextView) view.findViewById(ResourcesUtils.getId("txt_update_content"));
        tvContent.setText("安装包已经下载完毕，请您安装");

        Button btn_input_pwd_cancel = (Button) view.findViewById(ResourcesUtils.getId("btn_input_pwd_cancel"));
        btn_input_pwd_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgInstall.dismiss();
                onBackPressed();
//                notificationUtils.cancleNotifaction();
            }
        });
        Button btn_input_pwd_ok = (Button) view.findViewById(ResourcesUtils.getId("btn_input_pwd_ok"));
        btn_input_pwd_cancel.setText(android.R.string.cancel);
        btn_input_pwd_ok.setText("安装");
        btn_input_pwd_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgInstall.dismiss();
                installApk();
//                notificationUtils.cancleNotifaction();
            }
        });
        dlgInstall.setCancelable(true);
        dlgInstall.setView(view);
        return dlgInstall;
    }

}
