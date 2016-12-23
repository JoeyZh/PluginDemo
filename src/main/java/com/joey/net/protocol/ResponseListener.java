package com.joey.net.protocol;

import com.android.volley.Response;
import org.json.JSONObject;


/**
 * Created by Administrator on 2016/7/22.
 */
public abstract class ResponseListener<T> implements Response.Listener<String> {


    private ResponseHandler handler;

    public ResponseListener(ResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onResponse(String s) {
        convert(s);
    }

    private void convert(String s) {
//        KLog.a(getClass().getName(), "convert obj = " + s);
        try {
            JSONObject obj = new JSONObject();
            int status = obj.getInt("status");
            String msg = obj.getString("message");
            if (handler != null)
                handler.onSuccess();
//            if (0 == status || 1 == status) {
            try {
                T t = (T) obj.get("result");
//                KLog.a(getClass().getName(), "result = " + t.toString());
                onSuccess(t, status);
            } catch (Exception e) {
                ResponseError error = new ResponseError(status, msg);
                onError(error, status);
            }

            return;
        } catch (Exception e) {
            onError(new ResponseError(ResponseError.ERROR_BY_PARSE, e.getMessage()), -1);
            if (handler != null)
                handler.onError();
//            Log.e(getClass().getName(), "error =" + e.getMessage());
            return;
        }
    }
    public abstract void onSuccess(T t, int status);

    public abstract void onError(ResponseError error, int status);

    protected void onErrorNet() {
        if (handler != null)
            handler.onError();
    }
}
