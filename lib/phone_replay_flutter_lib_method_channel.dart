import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'phone_replay_flutter_lib_platform_interface.dart';

/// An implementation of [PhoneReplayFlutterLibPlatform] that uses method channels.
class MethodChannelPhoneReplayFlutterLib extends PhoneReplayFlutterLibPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('phone_replay_flutter_lib');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
