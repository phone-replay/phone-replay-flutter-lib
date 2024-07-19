package com.phonereplay.phone_replay_flutter_lib.tasklogger.service;

import android.graphics.Bitmap;

import com.phonereplay.phone_replay_flutter_lib.tasklogger.ActivityGesture;
import com.phonereplay.phone_replay_flutter_lib.tasklogger.DeviceModel;
import com.phonereplay.phone_replay_flutter_lib.tasklogger.network.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

public class PhoneReplayService {
    private static final int COMPRESSION_QUALITY = 10;
    private final Client client;
    private byte[] fullBytesVideo;
    private byte[] previousImageCompressed;

    public PhoneReplayService() {
        this.client = new Client();
        new Thread(client::callEndpoint).start();
    }

    private static byte[] joinByteArrays(byte[] array1, byte[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        byte[] result = new byte[length1 + length2];
        System.arraycopy(array1, 0, result, 0, length1);
        System.arraycopy(array2, 0, result, length1, length2);
        return result;
    }

    public static byte[] compress(byte[] data) throws IOException {
        if (data == null) {
            return null;
        }
        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
        try {
            deflater.setInput(data);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            deflater.finish();
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            deflater.end();
        }
    }

    public static byte[] compressGzip(byte[] data) throws IOException {
        if (data == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos);
        gzipOutputStream.write(data);
        gzipOutputStream.close();
        return baos.toByteArray();
    }

    private static byte[] writeImageCompressedFromBitmap(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, byteArrayOutputStream);
        return compressGzip(byteArrayOutputStream.toByteArray());
    }

    private static byte[] writeImageFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static boolean compareByteArrays(byte[] array1, byte[] array2) {
        return Arrays.equals(array1, array2);
    }

    private static byte[] combineIdentifierAndData(byte[] data) {
        byte[] identifierBytes = "------".getBytes();
        byte[] combinedData = new byte[identifierBytes.length + data.length];

        System.arraycopy(identifierBytes, 0, combinedData, 0, identifierBytes.length);
        System.arraycopy(data, 0, combinedData, identifierBytes.length, data.length);

        return combinedData;
    }

    public void queueBytesBitmap(Bitmap bitmap, boolean compress) throws IOException {
        byte[] imageData;
        if (compress) {
            imageData = writeImageCompressedFromBitmap(bitmap);
        } else {
            imageData = writeImageFromBitmap(bitmap);
        }

        byte[] combineIdentifierAndData;
        if (fullBytesVideo != null) {
            if (compareByteArrays(previousImageCompressed, imageData)) {
                combineIdentifierAndData = combineIdentifierAndData("D".getBytes());
            } else {
                combineIdentifierAndData = combineIdentifierAndData(imageData);
                previousImageCompressed = imageData;
            }
            fullBytesVideo = joinByteArrays(fullBytesVideo, combineIdentifierAndData);
        } else {
            previousImageCompressed = imageData;
            fullBytesVideo = imageData;
        }
        bitmap.recycle();
    }

    public void createVideo(Map<String, ActivityGesture> activityGesture, DeviceModel deviceModel, String projectKey, long duration) throws IOException {
        client.sendBinaryData(compress(fullBytesVideo), activityGesture, deviceModel, projectKey, duration);
        this.fullBytesVideo = null;
    }

    public boolean validateAccessKey(String projectKey) {
        return client.validateAccessKey(projectKey);
    }
}
