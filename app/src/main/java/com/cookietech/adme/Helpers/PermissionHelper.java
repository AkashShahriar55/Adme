package com.cookietech.adme.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class PermissionHelper {
    private static final String TAG = "PermissionHelper";

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 2;

    public static boolean requestLocationPermission(Context context){
        return checkPermission(context,Manifest.permission.ACCESS_FINE_LOCATION,PermissionHelper.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,"Location Permission","Location permission is must for the map features");
    }

     private static boolean checkPermission(Context context,final String permission_type,final int permission_id,final String permission_rational_title,final String permission_rational_message) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                permission_type)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    permission_type)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle(permission_rational_title).setMessage(permission_rational_message)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions((Activity)context,
                                        new String[]{permission_type},
                                        permission_id);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            } else {
                Log.i(TAG, "checkPermission: permission not granted");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{permission_type},
                        permission_id);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            Log.i(TAG, "checkPermission: permission granted");
            return true;

        }

        return false;
    }

    public static Task<LocationSettingsResponse> checkSettingsForLocation(Context context) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        return client.checkLocationSettings(builder.build());
    }
}
