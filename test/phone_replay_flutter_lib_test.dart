import 'package:flutter_test/flutter_test.dart';
import 'package:phone_replay_flutter_lib/phone_replay_flutter_lib.dart';
import 'package:phone_replay_flutter_lib/phone_replay_flutter_lib_platform_interface.dart';
import 'package:phone_replay_flutter_lib/phone_replay_flutter_lib_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPhoneReplayFlutterLibPlatform
    with MockPlatformInterfaceMixin
    implements PhoneReplayFlutterLibPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final PhoneReplayFlutterLibPlatform initialPlatform = PhoneReplayFlutterLibPlatform.instance;

  test('$MethodChannelPhoneReplayFlutterLib is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPhoneReplayFlutterLib>());
  });

  test('getPlatformVersion', () async {
    PhoneReplayFlutterLib phoneReplayFlutterLibPlugin = PhoneReplayFlutterLib();
    MockPhoneReplayFlutterLibPlatform fakePlatform = MockPhoneReplayFlutterLibPlatform();
    PhoneReplayFlutterLibPlatform.instance = fakePlatform;

    expect(await phoneReplayFlutterLibPlugin.getPlatformVersion(), '42');
  });
}
