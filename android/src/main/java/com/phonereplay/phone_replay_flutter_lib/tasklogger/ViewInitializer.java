package com.phonereplay.phone_replay_flutter_lib.tasklogger;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterView;

public class ViewInitializer {

    private static View currentView;
    private static UserInteractionAwareCallback userInteractionAwareCallback;

    public static void initView(Activity activity) {
        if (activity instanceof FlutterActivity) {
            FlutterActivity flutterActivity = (FlutterActivity) activity;
            ViewGroup rootView = flutterActivity.findViewById(android.R.id.content);
            if (rootView != null) {
                for (int i = 0; i < rootView.getChildCount(); i++) {
                    View child = rootView.getChildAt(i);
                    if (child instanceof FlutterView) {
                        currentView = child;
                        break;
                    }
                }
            }
        } else {
            currentView = activity.getWindow().getDecorView();
        }
    }

    public static void setupUserInteractionCallback(Activity activity) {
        Window window = activity.getWindow();
        if (window != null && !(window.getCallback() instanceof UserInteractionAwareCallback)) {
            userInteractionAwareCallback = new UserInteractionAwareCallback(window.getCallback(), activity);
            window.setCallback(userInteractionAwareCallback);
        }
        initView(activity);
    }

    public static UserInteractionAwareCallback getUserInteractionAwareCallback() {
        return userInteractionAwareCallback;
    }

    public static View getCurrentView() {
        return currentView;
    }
}
