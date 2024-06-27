package com.phonereplay.phone_replay_flutter_lib;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.phonereplay.phone_replay_flutter_lib.tasklogger.PhoneReplay;
import com.phonereplay.phone_replay_flutter_lib.tasklogger.PhoneReplayApi;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class PhoneReplayFlutterLibPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private Activity currentActivity;
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.example.phone_replay_flutter/biblioteca");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "setProjectKey":
                String key = call.argument("key");
                if (currentActivity != null) {
                    PhoneReplay.init(currentActivity, key);
                    result.success(null);
                } else {
                    result.error("ACTIVITY_NOT_AVAILABLE", "Activity is not available", null);
                }
                break;
            case "startRecording":
                PhoneReplayApi.startRecording();
                result.success(null);
                break;
            case "stopRecording":
                PhoneReplayApi.stopRecording();
                result.success(null);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        currentActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivity() {
    }
}
