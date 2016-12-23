package com.joey.update;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joey.net.protocol.NetUtils;
import com.joey.update.CheckBean;
import com.joey.update.UpdateCheckListener;
import com.joey.update.UpdateConsts;
import com.joey.update.UpdateProgressListener;
import com.joey.update.UpdateProtocol;
import com.joey.utils.NetWorkUtil;
import com.joey.utils.ResourcesUnusualUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/12/8.
 */

public class UpdateManager {


    /**
     * Created by Joey on 2016/10/11.
     * 检测更新的服务
     */
    private CheckBean checkBean;
    private NetUtils mNetUtils;
    private AlertDialog updateDialog;
    private String mDownloadUrl;
    private int mVersionCode;
    private File apkFile;
    private String saveUrlString = Environment.getExternalStorageDirectory() + UpdateConsts.UPDATE_APK_PATH + File.separator + "ibg100_release.apk";
    private AlertDialog progressDlg;
    private boolean forceUpdate = false;
    private UpdateProtocol protocol;
    private ProgressBar update_progress;
    private TextView update_text;
    private Activity mContext;
    public final static int STATUS_FORCE_UPDATE = 1;
    public final static int STATUS_NORMAL_UPDATE = 2;
    public final static int STATUS_NO_UPDATE = 3;
    public AlertDialog netDlg;

    private UpdateProgressListener progressListener = new UpdateProgressListener() {
        @Override
        public void onProgress(long current, long total) {
            if (mContext.isFinishing())
                return;
            if (total < current)
                return;
            updateProgress((int) current, (int) total);
        }

        @Override
        public void onStartProgress() {
            if (mContext.isFinishing())
                return;
            // TODO 显示加载进度的 dialog
            Intent intent = new Intent(UpdateConsts.ACTION_DOWNLOAD);
            intent.putExtra(UpdateConsts.ACTION_CHECKED_KEY, true);
            mContext.sendBroadcast(intent);
            if (forceUpdate)
                createProgressDialog().show();
        }

        @Override
        public void onFinish() {
            if (mContext.isFinishing())
                return;
            dismissDialog(progressDlg);
        }

        @Override
        public void onError() {
            if (mContext.isFinishing())
                return;
            dismissDialog(progressDlg);
        }
    };

    public UpdateManager(Activity context, UpdateProtocol protocol) {
        mContext = context;
        this.protocol = protocol;
        mNetUtils = NetUtils.getInstance(context);
    }

    public void startCheckout() {
        release();
        checkVersionCode();
    }

    public void release() {
        dismissDialog(updateDialog);
        dismissDialog(progressDlg);
        setCancelable(updateDialog, true);
        setCancelable(progressDlg, true);
        protocol.release();
    }

    private void setCancelable(AlertDialog dialog, boolean flag) {
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(flag);
            dialog.setCancelable(true);
        }
    }

    /**
     * 检测是否需要跟新按转包
     * <p/>
     * LoginBean status
     * status 1 : 强制更新
     * status 2 : 有新版本但是不需要强制更新
     * status 3 ：已经是最新版本了
     *
     * @return
     */
    private void checkVersionCode() {
        PackageManager packageManager = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(),
                    0);
            mVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mNetUtils = NetUtils.getInstance(mContext);
        protocol.checkVersionCode(mNetUtils, mVersionCode, new UpdateCheckListener() {
            @Override
            public void update(int status, CheckBean bean) {
                checkBean = bean;
                mDownloadUrl = checkBean.getUrl();
                switch (status) {
                    case STATUS_FORCE_UPDATE:
                        forceUpdate = true;
                        noticeUpdate(true);
                        createUpdateDialog().show();
                        break;
                    case STATUS_NORMAL_UPDATE:
                        forceUpdate = false;
                        noticeUpdate(true);
                        createUpdateDialog().show();
                        break;
                    default:
                        noticeUpdate(false);
                        break;
                }
            }

            @Override
            public void onError(int status, String msg) {

            }
        });
    }

    private void noticeUpdate(boolean checked) {
        Intent intent = new Intent(UpdateConsts.ACTION_UPDATE);
        intent.putExtra(UpdateConsts.ACTION_CHECKED_KEY, checked);
        // 通知广播更新状态
        mContext.sendBroadcast(intent);
    }

    private void noticeInstall() {
        Intent intent = new Intent(UpdateConsts.ACTION_INSTALL);
        intent.putExtra("forceUpdate", forceUpdate);
        mContext.sendBroadcast(intent);
    }

    public void showNetWarnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        long fileSize = checkBean.getSize();
        //接口中没有该参数，直接返回就好了
        if (fileSize == 0) {
            downloadApk();
            return;
        }
        String tag = "B";
        if (fileSize > 1024) {
            fileSize = fileSize >> 10;
            tag = "KB";
        }
        if (fileSize > 1024) {
            fileSize = fileSize >> 10;
            tag = "MB";
        }
        netDlg = builder.setMessage(String.format("新的安装包需要消耗您%s%s的流量，您是否还要更新呢？", fileSize + "", tag))
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("我是土豪", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadApk();
                    }
                }).create();
        netDlg.setCancelable(false);
        netDlg.setCanceledOnTouchOutside(false);
        netDlg.show();
    }

    /**
     * 下载安装包
     */
    private void downloadApk() {
        Intent intent = new Intent(UpdateConsts.ACTION_DOWNLOAD);
        intent.putExtra(UpdateConsts.ACTION_CHECKED_KEY, true);
        mContext.sendBroadcast(intent);
        protocol.download(checkBean.getUrl(), progressListener, saveUrlString);
    }

    private void updateProgress(int current, int total) {
        if (forceUpdate) {
            // TODO 更新progress Dialog的下载进度
//            KLog.e("total=" + total + ",current=" + current + ",percent = %" + (current / total));
            update_progress.setMax(total);
            update_progress.setProgress(current);
            float progress = current * 100.0f / total;
            update_text.setText(((int) progress) + "%");
            if (current >= total) {
                dismissDialog(progressDlg);
                return;
            }

            return;
        }
//        notificationUtils.upDateProgress(current / 1024,
//                total / 1024);

    }

    private void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private AlertDialog createUpdateDialog() {
//        ToastUtil.show(mContext, "创建升级Dialog");
        if (updateDialog != null)
            return updateDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        updateDialog = builder.create();
        ResourcesUnusualUtil.register(mContext);
        View view = View.inflate(mContext,
                ResourcesUnusualUtil.getLayoutId("dlg_update_apk"),
                null);
        TextView tvContent = (TextView) view.findViewById(ResourcesUnusualUtil.getId("txt_update_content"));
        String content = checkBean.getContent();
        if (content != null) {
            tvContent.setText(content);
        }
        Button btn_input_pwd_cancel = (Button) view.findViewById(ResourcesUnusualUtil.getId("btn_input_pwd_cancel"));
        if (forceUpdate) {
            btn_input_pwd_cancel.setVisibility(View.GONE);
            btn_input_pwd_cancel.setEnabled(false);
        }
        btn_input_pwd_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateConsts.ACTION_DOWNLOAD);
                intent.putExtra(UpdateConsts.ACTION_CHECKED_KEY, false);
                mContext.sendBroadcast(intent);
                updateDialog.dismiss();
            }
        });
        Button btn_input_pwd_ok = (Button) view.findViewById(ResourcesUnusualUtil.getId("btn_input_pwd_ok"));
        btn_input_pwd_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
                //如果已经下载到升级包
                if (checkBean.getN_v().equals(UpdateConsts.downloadCode)) {
                    apkFile = new File(saveUrlString);
                    noticeInstall();
                    return;
                }
                NetWorkUtil.init(mContext);
                String netType = NetWorkUtil.getCurrentNetworkType();
                if (netType.equals("2G") || netType.equals("3G") || netType.equals("4G"))
                    showNetWarnDialog();
                else {
                    downloadApk();
                }
            }
        });
        updateDialog.setCancelable(true);
        updateDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                updateDialog.dismiss();
                if (forceUpdate) {
                    mContext.finish();
                }
            }
        });
//        updateDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        updateDialog.setView(view);
        return updateDialog;
    }

    private AlertDialog createProgressDialog() {
        if (progressDlg != null)
            return progressDlg;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        progressDlg = builder.create();
        progressDlg.setCancelable(true);
        progressDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                progressDlg.dismiss();
                mContext.finish();
            }
        });
        progressDlg.setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(mContext).inflate(ResourcesUnusualUtil.getLayoutId("create_progress_dialog"), null);
        update_progress = (ProgressBar) view.findViewById(ResourcesUnusualUtil.getId("update_progress"));
        update_text = (TextView) view.findViewById(ResourcesUnusualUtil.getId("update_text"));
        progressDlg.setView(view);
        return progressDlg;
    }

}

