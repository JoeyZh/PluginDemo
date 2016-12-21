package com.joey.net.protocol;

/**
 * Created by Administrator on 2016/7/22.
 */
public interface ResponseHandler {
    /**
     * 成功
     */
    void onSuccess();

    /**
     * 加载中
     */
    void onLoading();

    /**
     * 出错
     */
    void onError();
}
