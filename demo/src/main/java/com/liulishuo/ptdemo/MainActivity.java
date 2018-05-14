package com.liulishuo.ptdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.liulishuo.pt.PTManager;
import com.liulishuo.pt.PTResult;
import com.liulishuo.pt.PTResultCallback;
import com.liulishuo.pt.PTStartCallback;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements PTResultCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 开启 webview debug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public void onSuccess(PTResult result) {
        String report = String.format(
                        "score = %d\n"
                        + "fluency = %s\n"
                        + "level = %d\n"
                        + "levelDescription = %s\n"
                        + "pronunciation = %s\n"
                        + "rawReport = %s",
                result.getScore(), result.getFluency(), result.getLevel(),
                result.getLevelDescription(), result.getPronunciation(),
                result.getRawReport());
        new AlertDialog.Builder(this).setMessage(report).create().show();
    }

    @Override
    public void onCancel() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PTManager.handleStartPtOnActivityResult(this, requestCode, resultCode, data);
    }

    public void startPT(View view) {
        String id = getIdFromCache();
        if (id == null) {
            id = UUID.randomUUID().toString();
            putId(id);
        }
        PTManager.startPT(this, id, new PTStartCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    public void restartPT(View view) {
        clear();
        startPT(view);
    }

    private String getIdFromCache() {
        return getSp().getString("id", null);
    }

    private void putId(String id) {
        getSp().edit().putString("id", id).apply();
    }

    private void clear() {
        getSp().edit().clear().apply();
    }

    private SharedPreferences getSp() {
        return getSharedPreferences("demo", 0);
    }
}
