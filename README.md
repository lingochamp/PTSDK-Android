# Android 版  PTSdk 介绍


# sdk 集成

## gradle 添加依赖

```
implementation 'com.liulishuo:pt:1.0.0'

```

## api level 支持

min api level 17

## 权限要求

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

默认情况下 ptsdk 会处理 runtime permission


# 基本使用

## 初始化

请先将 PTManager 进行初始化，将 TokenProvider 注入
```
PTManager.init(TokenProvider tokenProvider)

```

其中 TokenProvider 需要由 App 开发者实现，以提供 token 供 pt 学习做验权所用。
```
public interface TokenProvider {

	interface FetchTokenCallback {
		void onSuccess(String token);
		void onError(Throwable throwable);
	}

	void fetchToken(Context context, FetchTokenCallback callback, boolean noCache);
	
}

```
具体 TokenProvider 的实现可参考 demo 的实现与 token 获取的 restful api 

## 开始使用

### 开始 PT

调用 PTManager.startPT 则会开始 PT 流程，其中 id 用以标志一次可恢复的 pt，倘若传入相同的 id，则会继续上一次的进度继续测试。
```
PTManager.startPT(@NonNull final Activity activity, @NonNull final String id, @NonNull final PTStartCallback startCb)

```
sdk 会在 start 这一刻调用 init 时注入的 tokenProvider 进行 token 获取，如果获取成功则会进入学习流程，否则会在 startCallback 报错告知。

### 处理 PT 结果

调用 PTManager.startPT 的 Activity 需要实现 PTResultCallback 以接受 pt 结果

```

public class MainActivity extends AppCompatActivity implements PTResultCallback {
    @Override
    public void onSuccess(PTResult result) {
        // process pt result
        Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onCancel() {
        // 提前退出 pt 则会在这收到回调
    }
}
```

务必需要在 onActivityResult 中转发 PTManager.handleStartPtOnActivityResult(this, requestCode, resultCode, data)
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    PTManager.handleStartPtOnActivityResult(this, requestCode, resultCode, data);
}

```

### 其他

因为 ptsdk 在使用过程中会生成一些 cache 文件，所以如果需要清除可以调用 clearPTCache()
```
PTManager.cleanPtCache(Context contxt)
```



