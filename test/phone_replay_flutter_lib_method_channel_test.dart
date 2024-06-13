import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:phone_replay_flutter_lib/phone_replay_flutter_lib_method_channel.dart';

void main() {
  MethodChannelPhoneReplayFlutterLib platform = MethodChannelPhoneReplayFlutterLib();
  const MethodChannel channel = MethodChannel('phone_replay_flutter_lib');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
