package com.phonereplay.phone_replay_flutter_lib.tasklogger;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import io.flutter.embedding.android.FlutterActivity;

public class PhoneReplay extends Activity {

    private static PhoneReplayApi phoneReplayApi;
    private static PhoneReplay sInstance;
    private static Context context = null;
    private static String accessKey = null;
    private static Activity activity = null;

    private PhoneReplay(Activity activity, String accessKey) {
        context = AppContext.getContext();
        PhoneReplay.accessKey = accessKey;
        PhoneReplay.activity = activity;
    }

    public static void init(Activity activity, String accessKey) {
        PhoneReplay.getInstance(activity, accessKey).attachBaseContext();
    }

    public synchronized static PhoneReplay getInstance(Activity activity, String accessKey) {
        if (sInstance == null) {
            sInstance = new PhoneReplay(activity, accessKey);
        }
        return sInstance;
    }

    private void attachBaseContext() {
        if (activity instanceof FlutterActivity) {
            phoneReplayApi = new PhoneReplayApi(activity, context, accessKey, "FLUTTER");
            phoneReplayApi.initThread();
            phoneReplayApi.initHandler();
            Log.d("ActivityInstance", "activity is flutter instance native instance");
        }
    }
}
