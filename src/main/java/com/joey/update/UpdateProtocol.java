package com.joey.update;

import com.joey.net.protocol.NetUtils;

/**
 * Created by Joey on 2016/12/13.
 */

public abstract class UpdateProtocol {
    public abstract void download(String url, UpdateProgressListener listener, String saveUrlString);
    public abstract void checkVersionCode(NetUtils netUtils, final int versionCode, UpdateCheckListener listener);
    public abstract void release();
}
