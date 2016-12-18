package com.itheima.mobilesafe.utils;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Yi-Jing on 2016/9/16.
 */

public class NetAsyncTask extends AsyncTask<Integer, Void, String> {
    private String url;
    private String[] name;
    private String[] data;
    private NetUtils.Callback callback;

    public NetAsyncTask(String url, String[] name, String[] data, NetUtils.Callback callback) {
        this.callback = callback;
        this.data = data;
        this.name = name;
        this.url = url;
    }

    @Override
    protected String doInBackground(Integer... type) {
        String response = "";
        Log.d("doInBackground", type[0] + "");
        if ((type[0] & NetUtils.POST) == NetUtils.POST){
            if ((type[0] & NetUtils.JSON) == NetUtils.JSON)
                response = NetUtils.sendJSONByPost(url, name, data);
            else
                response = NetUtils.sendDataByPost(url, name, data);
        }
        else {
            response = NetUtils.sendDataByGet(url, name, data);
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        callback.onResponse(result);
    }

}
