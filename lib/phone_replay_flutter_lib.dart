import 'package:flutter/services.dart';

class PhoneReplayFlutterLib {
  static const _channel = MethodChannel("com.example.phone_replay_flutter/biblioteca");

  static Future<void> setProjectKey(String key) async {
    try {
      await _channel.invokeMethod('setProjectKey', {'key': key});
    } on PlatformException catch (e) {
      print("Failed to set project key: '${e.message}'.");
    }
  }

  static Future<void> startRecording() async {
    try {
      await _channel.invokeMethod('startRecording');
    } on PlatformException catch (e) {
      print("Failed to start recording: '${e.message}'.");
    }
  }

  static Future<void> stopRecording() async {
    try {
      await _channel.invokeMethod('stopRecording');
    } on PlatformException catch (e) {
      print("Failed to stop recording: '${e.message}'.");
    }
  }
}
