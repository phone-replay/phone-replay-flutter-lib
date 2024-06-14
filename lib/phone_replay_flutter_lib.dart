
import 'phone_replay_flutter_lib_platform_interface.dart';
import 'package:flutter/services.dart';


class PhoneReplayFlutterLib {
  Future<String?> getPlatformVersion() {
    return PhoneReplayFlutterLibPlatform.instance.getPlatformVersion();
  }

  static const _channel = MethodChannel("com.example.phone_replay_flutter/biblioteca");

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
