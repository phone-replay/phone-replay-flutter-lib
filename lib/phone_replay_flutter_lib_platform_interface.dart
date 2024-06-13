import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'phone_replay_flutter_lib_method_channel.dart';

abstract class PhoneReplayFlutterLibPlatform extends PlatformInterface {
  /// Constructs a PhoneReplayFlutterLibPlatform.
  PhoneReplayFlutterLibPlatform() : super(token: _token);

  static final Object _token = Object();

  static PhoneReplayFlutterLibPlatform _instance = MethodChannelPhoneReplayFlutterLib();

  /// The default instance of [PhoneReplayFlutterLibPlatform] to use.
  ///
  /// Defaults to [MethodChannelPhoneReplayFlutterLib].
  static PhoneReplayFlutterLibPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PhoneReplayFlutterLibPlatform] when
  /// they register themselves.
  static set instance(PhoneReplayFlutterLibPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
