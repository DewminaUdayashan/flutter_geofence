package com.flutter.flutter_plugin_callbacktst;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterCallbackInformation;
import io.flutter.view.FlutterMain;




public class BackgroundHandler implements MethodChannel.MethodCallHandler {


    private MethodChannel backgtoundMethodChannel;
    FlutterEngine flutterEngine;
    Context context;


    public void start(Context context, Long calback){

        Log.d("TAG", "start: Starting Engine");
        this.context = context;
        flutterEngine = new FlutterEngine(context);
        FlutterCallbackInformation callbackInfo = FlutterCallbackInformation.lookupCallbackInformation(calback);
        DartExecutor.DartCallback args = new DartExecutor.DartCallback(
                context.getAssets(),
                FlutterMain.findAppBundlePath(context),
                callbackInfo
        );
        flutterEngine.getDartExecutor().executeDartCallback(args);
        backgtoundMethodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "plugins.flutter.io/geofencing_plugin_background");
        backgtoundMethodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
       if(call.method.equals("GeofencingService.initialized")){
           backgtoundMethodChannel.invokeMethod("start","hrii");
       }
        result.success(null);
    }
}
