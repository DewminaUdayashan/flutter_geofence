import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_geofence/flutter_geofence.dart';

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
                final res = await FlutterGeofence.startGeofence(
                  lat: 6,
                  lng: 8,
                  radius: 10,
                );
                print(res);
              }),
            )
          ],
        ),
      ),
    );
  }
}
