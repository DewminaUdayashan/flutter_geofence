
import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class FlutterGeofence {
  static const MethodChannel _channel =
      const MethodChannel('flutter_geofence');

  static Future<String?>  startGeofence(
      {@required double? lat,@required double? lng, double radius = 150.0}) async {
    var sendMap = <String, dynamic>{
      'lat':lat,
      'lng':lng,
      'radius':radius,
    };
    final String? res = await _channel.invokeMethod('start_geofence',sendMap);
    return res;
  }

  static void askPermissions(){
     _channel.invokeMethod('permission');
  }

}
