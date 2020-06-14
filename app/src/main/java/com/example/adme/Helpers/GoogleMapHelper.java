package com.example.adme.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.adme.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.gson.JsonObject;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class GoogleMapHelper {

    private static final String TAG = "GoogleMapHelper";

    public static final String API_KEY = "AIzaSyBX7S1-Ra-VAaas4ZdFUSWhPGLvFdxDMU0";

    private Context context;

    GoogleMap mMap;
    LatLng origin,dest;
    private static final float DEFAULT_ZOOM = 15;

    private GoogleMapHelperCommunicator communicator;

    public GoogleMapHelper(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public GoogleMapHelper(Context context,GoogleMapHelperCommunicator communicator) {
        this.context = context;
        this.communicator = communicator;
    }

    public String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&key="+API_KEY;

        this.origin  = origin;
        this.dest = dest;

        return url;
    }

    public void downloadJson(String Url){
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(Url);
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);
            Log.i(TAG, "onPostExecute: " + result);

        }
    }

    public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String,String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String,String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);


                routes = parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String,String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.add(origin);
                lineOptions.addAll(points);
                lineOptions.add(dest);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#3f5aa6"));
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List list = PolyUtil.decode(polyline);

                        /** Traversing all points */
                        for(int l=0;l <list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }

    public static void markCurrentLocation(Context context, GoogleMap mMap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
                locationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, location -> {
                    if(location != null){
                        LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true).title(context.getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,DEFAULT_ZOOM));
                        Log.i(TAG, "run: "+location.getLongitude() + " " + location.getLatitude());
                    }

                }).addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).start();
    }


    public static void getCurrentLocationAddress(Context context,OnLocationAddressCallback callback){
        OnLocationAddressCallback addressCallback =  callback;
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, location -> {
            if(location != null){
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL'
                    Log.i(TAG, "getCurrentLocationAddress: "+ address);
                    addressCallback.locationAddressFetched(addresses.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        });


    }



    public interface OnLocationAddressCallback{
        void locationAddressFetched(Address address);
    }



    public void searchPlaces(String text_search,String current_location_lat,String current_location_lang){
        String[] tokens = text_search.split("\\s+");
        String query = "";
        for (int i = 0; i < tokens.length; i++) {
            if(i == (tokens.length-1)){
                query+=tokens[i];
            }else{
                query+=tokens[i]+"+";
            }
        }
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+query+"&location="+current_location_lat+","+current_location_lang+"&radius=10000&key="+API_KEY;
        Log.d(TAG, "searchPlaces: "+url);
        new DownloadTaskForPlaceSearch().execute(url);

    }

    private class DownloadTaskForPlaceSearch extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, "onPostExecute: " + result);
            new ParsePlacesTask().execute(result);
        }
    }


    private class ParsePlacesTask extends AsyncTask<String,Void,List<MyPlaces>>{

        @Override
        protected List<MyPlaces> doInBackground(String... jsonData) {
            List<MyPlaces> places = parsePlaces(jsonData);
            return places;
        }

        @Override
        protected void onPostExecute(List<MyPlaces> places) {
            communicator.onPlacesSearchComplete(places);
        }
    }

    private List<MyPlaces> parsePlaces(String[] jsonData) {
        List<MyPlaces> places = new ArrayList<>();
        try {

            JSONObject mainObj = new JSONObject(jsonData[0]);
            JSONArray resultArray = mainObj.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject placeObj = resultArray.getJSONObject(i);
                String formatted_address = placeObj.getString("formatted_address");
                String name = placeObj.getString("name");
                JSONObject geometryObj = placeObj.getJSONObject("geometry");
                JSONObject locationObj = geometryObj.getJSONObject("location");
                String latitude = locationObj.getString("lat");
                String longitude = locationObj.getString("lng");
                MyPlaces place = new MyPlaces(formatted_address,name,latitude,longitude);
                places.add(place);
                Log.d(TAG, "parsePlaces: "+formatted_address+" "+name+" "+latitude+" "+longitude);
            }

        } catch (JSONException e) {
            Log.e(TAG, "parsePlaces: ",e );
        }
        return places;
    }

    public void getCurrentLocationAddress(String latitude,String longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            double lat = Double.parseDouble(latitude);
            double lng = Double.parseDouble(longitude);
            addresses = geocoder.getFromLocation(lat,lng,1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL'
            Log.i(TAG, "getCurrentLocationAddress: "+ address);
            MyPlaces place = new MyPlaces(address,knownName,latitude,longitude);
            communicator.onCurrentLocationAddressFetched(place);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public interface GoogleMapHelperCommunicator{
        void onPlacesSearchComplete(List<MyPlaces> placesList);
        void onCurrentLocationAddressFetched(MyPlaces place);
    }


    public static void markLocation(Context context, GoogleMap mMap, LatLng location){
        mMap.addMarker(new MarkerOptions().position(location).draggable(true).title(context.getString(R.string.current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,DEFAULT_ZOOM));
    }

    public static double getDistanceInMiles(double lat1, double lng1, double lat2, double lng2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        float distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters/1609.34;
    }

    public static float getCurrentLocationDistance(Context context, GoogleMap mMap, LatLng otherlocation){
        float[] results = new float[1];
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
//                locationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, location -> {
//                    if(location != null){
//                        LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
//                        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, otherlocation.latitude, otherlocation.longitude, results);
//                        float distance = results[0];
//                        Log.i(TAG, "distance: "+distance);
//                    }
//
//                }).addOnFailureListener((Activity) context, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//            }
//        }).start();
        return results[0];
    }

}
