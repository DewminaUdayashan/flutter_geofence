import 'dart:ui';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_geofence/callback_dispatcher.dart';
import 'package:flutter_geofence/flutter_geofence.dart';


void callbackDispatcher() {
  print("Dispatcherr");
  const MethodChannel _backgroundChannel =
  MethodChannel('plugins.flutter.io/geofencing_plugin_background');


  WidgetsFlutterBinding.ensureInitialized();

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    final List<dynamic> args = call.arguments;
    final Function? callback = PluginUtilities.getCallbackFromHandle(
        CallbackHandle.fromRawHandle(args[0]));
    assert(callback != null);
    final List<String> triggeringGeofences = args[1].cast<String>();
    final List<double> locationList = <double>[];
    // 0.0 becomes 0 somewhere during the method call, resulting in wrong
    // runtime type (int instead of double). This is a simple way to get
    // around casting in another complicated manner.
    args[2]
        .forEach((dynamic e) => locationList.add(double.parse(e.toString())));
  });
  _backgroundChannel.invokeMethod('GeofencingService.initialized');
}

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  FlutterGeofence geofence = new FlutterGeofence();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        floatingActionButton: Row(
          children: [
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: FloatingActionButton(onPressed: () {
                FlutterGeofence.askPermissions();
              }),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: FloatingActionButton(onPressed: () async {
                 FlutterGeofence.startGeofence(
                  lat: 6,
                  lng: 8,
                  radius: 100,
                  function:callbackDispatcher
                );
              }),
            )
          ],
        ),
      ),
    );
  }
}
