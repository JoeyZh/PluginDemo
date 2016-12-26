package com.joey.update;

import android.content.Context;

/**
 * Created by Joey on 2016/12/13.
 */

public abstract class UpdateProtocol {
    public abstract void download(String url, UpdateProgressListener listener, String saveUrlString);
    public abstract void checkVersionCode(Context context,final int versionCode, UpdateCheckListener listener);
    public abstract void release();
}
