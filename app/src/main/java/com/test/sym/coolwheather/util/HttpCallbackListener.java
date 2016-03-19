package com.test.sym.coolwheather.util;

/**
 * Created by liuran on 16/3/16.
 */
public interface HttpCallbackListener {

    void onFinish(String response);
    void onError(Exception e);
}
