
import 'phone_replay_flutter_lib_platform_interface.dart';

class PhoneReplayFlutterLib {
  Future<String?> getPlatformVersion() {
    return PhoneReplayFlutterLibPlatform.instance.getPlatformVersion();
  }
}
