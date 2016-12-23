package com.joey.update;

/**
 * Created by Joey on 2016/12/13.
 */

public interface UpdateCheckListener {
    void update(int status, CheckBean bean);
    void onError(int status,String msg);
}
