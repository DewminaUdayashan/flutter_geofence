import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class FlutterGeofence {
  static const MethodChannel _channel =
      const MethodChannel('plugin.dewz.geofence/geofence');
  MethodChannel bgChannel =
      MethodChannel("plugin.dewz.geofence/geofence_background");

  static void startGeofence(
      {@required Function? function,
        @required double? lat,
      @required double? lng,
      double radius = 100.0}) async {
    CallbackHandle? callbackHandle = PluginUtilities.getCallbackHandle(function!);

    var sendMap = <String, dynamic>{
      'lat': lat,
      'lng': lng,
      'radius': radius,
    };
    await _channel.invokeMethod('start_geofence', <dynamic>[callbackHandle!.toRawHandle(),sendMap]);
  }



  static void askPermissions() {
    _channel.invokeMethod('permission');
  }
}
