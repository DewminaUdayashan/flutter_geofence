package com.flutter.flutter_geofence;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.loader.ApplicationInfoLoader;
import io.flutter.embedding.engine.loader.FlutterApplicationInfo;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.view.FlutterCallbackInformation;

/**
 * FlutterGeofencePlugin
 */
public class FlutterGeofencePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private static final String TAG = "MapsActivity";

    private final int FINE_LOCATION_ACCESS_REDQUEST_CODE = 10001;
    private final int BACKGROUND_LOCATION_ACCESS_REDQUEST_CODE = 20001;
    private final String GEOFENCE_ID = "SOME_GEOFENCE_ID"; //must be unique


    private MethodChannel channel;
    public static Context context;
    public static Activity activity;

    public static Long callback;
 public static BackgroundHandler backgroundHandler = new BackgroundHandler();

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;


    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius, String geofenceID) {
        Geofence geofence = geofenceHelper.getGeofence(geofenceID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeogencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                NotificationHelper notificationHelper = new NotificationHelper(context);
                                notificationHelper.sendHighPriorityNotification("GEOFENCE_STARTED","",FlutterGeofencePlugin.class);
                                Log.d(TAG, "onSuccess: Geofence Added..!");
                            }
                        }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }


    private void permissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
        } else {
            //Ask for Permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission..
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REDQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REDQUEST_CODE);
            }
        }
        if (Build.VERSION.SDK_INT >= 29) {
            //We need BG Permision
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // show a dialog & ask for permission
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REDQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REDQUEST_CODE);
                }
            }
        } else {
        }
    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "plugin.dewz.geofence/geofence");
        channel.setMethodCallHandler(this);
    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        if (call.method.equals("start_geofence")) {
             ArrayList args = (ArrayList) call.arguments;
           callback  = (Long) args.get(0);
            final Map<String, Double> arg = (Map<String, Double>) args.get(1);
            double lat = arg.get("lat");
            double lng = arg.get("lng");
            String radius = String.valueOf(arg.get("radius"));
            String geofenceID = String.valueOf(arg.get("geofenceID"));
            addGeofence(new LatLng(lat, lng), Float.parseFloat(radius), geofenceID); //lat lng // radius -> float
            Log.d(TAG, "onMethodCall: Geofence Started");
            result.success("Geofence Started..");
        } else if (call.method.equals("permission")) {
            permissions();
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        context = binding.getActivity().getApplicationContext();
        geofencingClient = LocationServices.getGeofencingClient(binding.getActivity());
        geofenceHelper = new GeofenceHelper(context); // this ->
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        context = binding.getActivity().getApplicationContext();
        geofencingClient = LocationServices.getGeofencingClient(binding.getActivity());
        geofenceHelper = new GeofenceHelper(context); // this ->
    }

    @Override
    public void onDetachedFromActivity() {

    }


}
