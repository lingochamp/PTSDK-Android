package com.liulishuo.ptdemo;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.liulishuo.pt.PTManager;
import com.liulishuo.pt.TokenProvider;
import com.liulishuo.pt.internal.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wcw on 3/1/18.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PTManager.init(new TokenProvider() {

            @Override
            public void fetchToken(Context context, FetchTokenCallback callback, boolean noCache) {
                if (!noCache && isCacheValid(context)) {
                    callback.onSuccess(getCacheToken(context));
                } else {
                    if (noCache) {
                        sp(context).edit().clear().apply();
                    }
                    fetch(context, callback);
                }
            }

            private void fetch(final Context context, final FetchTokenCallback callback) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("正在获取 token 中");
                progressDialog.show();

                String token = null;
                long expiredAt = 0L;

                // todo 需要调用 api 获取 token
                // token = fetch api from network
                if (token == null) {
                    callback.onError(new RuntimeException("token is invalid"));
                } else {
                    saveCacheToken(context, token, expiredAt);
                    callback.onSuccess(token);
                }
                progressDialog.dismiss();
            }

            private SharedPreferences sp(Context context) {
                return context.getSharedPreferences("pt", MODE_PRIVATE);
            }

            private boolean isCacheValid(Context context) {
                SharedPreferences sp = sp(context);
                String token = sp.getString("token", null);
                long expiredAt = sp.getLong("expiredAt", 0);
                return !TextUtils.isEmpty(token) && System.currentTimeMillis() / 1000 < expiredAt;
            }

            private String getCacheToken(Context context) {
                SharedPreferences sp = sp(context);
                return sp.getString("token", null);
            }

            private void saveCacheToken(Context context, String token, long expiredAt) {
                sp(context).edit()
                        .putString("token", token)
                        .putLong("expiredAt", expiredAt)
                        .apply();
            }
        });
    }
}
