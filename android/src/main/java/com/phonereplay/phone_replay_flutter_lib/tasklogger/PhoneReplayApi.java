package com.phonereplay.phone_replay_flutter_lib.tasklogger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.phonereplay.phone_replay_flutter_lib.tasklogger.service.PhoneReplayService;

import java.io.IOException;
import java.util.Objects;

public class PhoneReplayApi {

    private static final int RECORDING_INTERVAL = 100;
    public static boolean startRecording = false;
    private static Thread thread;
    private static Handler mHandler;
    @SuppressLint("StaticFieldLeak")
    private static PhoneReplayService apiClientService;
    @SuppressLint("StaticFieldLeak")
    private static GestureRecorder gestureRecorder;
    private static StopwatchUtility stopwatch = new StopwatchUtility();
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static String platform;
    private static String projectKey;
    private static long startTime;
    private static long endTime;
    private static Activity activity;

    public PhoneReplayApi(Activity activity, Context context, String accessKey, String platform) {
        PhoneReplayApi.platform = platform;
        PhoneReplayApi.context = context;
        PhoneReplayApi.activity = activity;
        projectKey = accessKey;
        apiClientService = new PhoneReplayService();
    }


    public static void startRecording() {
        if (Objects.equals(platform, "FLUTTER")) {
            ViewInitializer.setupUserInteractionCallback(activity);
        }
        new Thread(() -> {
            gestureRecorder = new GestureRecorder();
            startRecording = true;
            startTime = System.currentTimeMillis();
            mHandler.postDelayed(thread, RECORDING_INTERVAL);
            startCountUp();
        }).start();
    }

    public static void stopRecording() {
        if (startRecording) {
            startRecording = false;
            endTime = System.currentTimeMillis();
            mHandler.removeCallbacks(thread);
            Log.d("timer", stopwatch.timer);
            stopwatch.stop();

            long duration = endTime - startTime;
            Log.d("RecordingDuration", "Duração da gravação: " + duration + " milissegundos");

            DeviceModel deviceModel = new DeviceModel(context);
            if (gestureRecorder != null) {
                String summaryLog = gestureRecorder.generateSummaryLog();
                Log.d("GestureRecorderSummary", summaryLog);
            }
            new Thread(() -> {
                try {
                    apiClientService.createVideo(gestureRecorder.currentSession, deviceModel, projectKey, duration);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    private static void startCountUp() {
        if (stopwatch == null) {
            stopwatch = new StopwatchUtility();
        }
        stopwatch.start();
    }

    public static void registerTouchAction(String action, float x, float y) {
        if (startRecording && activity != null) {
            String activityName = activity.getClass().getSimpleName();
            String targetTime = stopwatch.timer;
            String gestureType = "(" + x + ", " + y + ")";
            gestureRecorder.registerGesture(activityName, action, targetTime, gestureType);
            Log.d("ActionRegistered", "Gesture: " + gestureType + ", Time: " + targetTime);
        }
    }

    public void initHandler() {
        mHandler = new Handler();
    }


    public void initThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    new Thread() {
                        @Override
                        public void run() {
                            if (startRecording) {
                                try {
                                    View currentView = ViewInitializer.getCurrentView();
                                    if (currentView != null) {
                                        Bitmap bitmap;
                                        bitmap = Bitmap.createBitmap(currentView.getWidth(), currentView.getHeight(), Bitmap.Config.ARGB_8888);
                                        PixelCopy.request(Objects.requireNonNull(getSurfaceView(currentView)), bitmap, copyResult -> {
                                            if (copyResult == PixelCopy.SUCCESS) {
                                                try {
                                                    apiClientService.queueBytesBitmap(bitmap, true);
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        }, new Handler(Looper.getMainLooper()));
                                    } else {
                                        System.err.println("Error: currentView is null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                super.run();
                            }
                        }
                    }.start();
                    if (startRecording) {
                        mHandler.postDelayed(thread, RECORDING_INTERVAL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private SurfaceView getSurfaceView(View view) {
        if (view instanceof SurfaceView) {
            return (SurfaceView) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                SurfaceView surfaceView = getSurfaceView(viewGroup.getChildAt(i));
                if (surfaceView != null) {
                    return surfaceView;
                }
            }
        }
        return null;
    }
}
