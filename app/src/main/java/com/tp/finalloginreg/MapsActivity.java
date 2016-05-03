/**********
 * author: Tanvi@
 * description:  Integration of Google's Map API.
 * Contains Autocomplete search of the places stored in database
 *And marking the nearby places  based on the user's current location within a given radius on the Google Maps.
 * The places are stored in the database with latitude and longitude and mark them on the map using it.
 **********/
package com.tp.finalloginreg;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements LocationListener{
    GoogleMap mGoogleMap;
    double mLatitude = 0;
    double mLongitude = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);




        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {
            // Getting reference to the SupportMapFragment
            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Google Map
            mGoogleMap = fragment.getMap();


            // Enabling MyLocation in Google Map
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);
            Button btnFind;
            // Getting reference to Find Button
            btnFind = (Button) findViewById(R.id.btn_find);


            btnFind.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               final ProgressDialog dialog = ProgressDialog.show(MapsActivity.this, "", "Please wait...", true);
                                               String tag_string_req = "place_mark";
                                               final AutoCompleteTextView place = (AutoCompleteTextView) findViewById(R.id.AC_place);
                                               final String mark_place = place.getText().toString();
                                               String url = "http://dessertry.comlu.com/mark.php";
                                               StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                                                   @Override
                                                   public void onResponse(String response) {
                                                       Log.d("Search", "Search Response: " + response.toString());
                                                       dialog.dismiss();
                                                       try {

                                                           JSONArray jsonarray = new JSONArray(response);
                                                           List<HashMap<String, String>> places = null;
                                                           PlaceJSONParser placeJsonParser = new PlaceJSONParser();

                                                           places = placeJsonParser.getPlaces(jsonarray);
                                                           //Log.d("HEY", "did it" + res);
                                                           //Clears all the existing markers
                                                           mGoogleMap.clear();

                                                           for (int i = 0; i < places.size(); i++) {

                                                               // Creating a marker
                                                               MarkerOptions markerOptions = new MarkerOptions();

                                                               // Getting a place from the places list
                                                               HashMap<String, String> hmPlace = places.get(i);

                                                               // Getting latitude of the place
                                                               double lat = Double.parseDouble(hmPlace.get("latitude"));

                                                               // Getting longitude of the place
                                                               double lng = Double.parseDouble(hmPlace.get("longitude"));

                                                               // Getting name
                                                               String name = hmPlace.get("place_name");

                                                               // Getting vicinity
                                                               // String vicinity = hmPlace.get("vicinity");

                                                               LatLng latLng = new LatLng(lat, lng);

                                                               // Setting the position for the marker
                                                               markerOptions.position(latLng);

                                                               // Setting the title for the marker.
                                                               //This will be displayed on taping the marker
                                                               markerOptions.title(name);

                                                               // Placing a marker on the touched position
                                                               mGoogleMap.addMarker(markerOptions);
                                                           }


                                                       } catch (JSONException e) {
                                                           // JSON error
                                                           e.printStackTrace();
                                                           Log.d("Exception", "Json error: " + e.getMessage());
                                                       }

                                                   }
                                               }, new Response.ErrorListener() {

                                                   @Override
                                                   public void onErrorResponse(VolleyError error) {
                                                       Log.e("ERROR", "Search Error: " + error.getMessage());
                                                       Toast.makeText(getApplicationContext(),
                                                               error.getMessage(), Toast.LENGTH_LONG).show();

                                                   }
                                               }) {

                                                   @Override
                                                   protected Map<String, String> getParams() {
                                                       // Posting params to register url
                                                       Map<String, String> params = new HashMap<String, String>();
                                                       params.put("name", mark_place);
                                                       return params;
                                                   }

                                               };
                                               AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                                           }
                                       });



            Map<String, String> params = null;
            if (location != null) {
                onLocationChanged(location);
                // sending location from android to php server using volley and mapping the nearby location
                String tag_string_loc = "req_find_location";
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                final String lat = String.valueOf(latitude);
                final String Long = String.valueOf(longitude);
                final ProgressDialog dialog = ProgressDialog.show(MapsActivity.this, "", "Please wait...", true);
                String url = "http://dessertry.comlu.com/retrieve.php";
                StringRequest strReqfetch = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    List<Pair<String, String>> params = new ArrayList<>();
                    ArrayList<String> res = new ArrayList<String>();

                    @Override
                    public void onResponse(String response) {

                        Log.d("SUCCESS", "Find Response: " + response.toString());
                        dialog.dismiss();
                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            List<HashMap<String, String>> places = null;
                            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

                            places = placeJsonParser.getPlaces(jsonarray);
                            //Log.d("HEY", "did it" + res);
                            //Clears all the existing markers
                            mGoogleMap.clear();

                            for (int i = 0; i < places.size(); i++) {

                                // Creating a marker
                                MarkerOptions markerOptions = new MarkerOptions();

                                // Getting a place from the places list
                                HashMap<String, String> hmPlace = places.get(i);

                                // Getting latitude of the place
                                double lat = Double.parseDouble(hmPlace.get("latitude"));

                                // Getting longitude of the place
                                double lng = Double.parseDouble(hmPlace.get("longitude"));

                                // Getting name
                                String name = hmPlace.get("place_name");

                                // Getting vicinity
                                // String vicinity = hmPlace.get("vicinity");

                                LatLng latLng = new LatLng(lat, lng);

                                // Setting the position for the marker
                                markerOptions.position(latLng);

                                // Setting the title for the marker.
                                //This will be displayed on taping the marker
                                markerOptions.title(name);

                                // Placing a marker on the touched position
                                mGoogleMap.addMarker(markerOptions);
                            }


                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Log.d("Exception", "Json error: " + e.getMessage());
                        }

                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("FETCH", "Fetch Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to retrieve url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("latitude", lat);
                        params.put("longitude", Long);

                        return params;
                    }

                };
                AppController.getInstance().addToRequestQueue(strReqfetch, tag_string_loc);
                locationManager.requestLocationUpdates(provider, 20000, 0, this);

                // AUTOCOMPLETE SEARCH
                String search_url = "http://dessertry.comlu.com/autocomplete.php";
                String tag_string_req = "req_search";

                StringRequest strReq = new StringRequest(Request.Method.POST, search_url, new Response.Listener<String>() {

                    List<Pair<String, String>> params = new ArrayList<>();
                    ArrayList<String> res = new ArrayList<String>();

                    @Override
                    public void onResponse(String response) {
                        Log.d("SUCCESS", "Login Response: " + response.toString());


                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            int len = jsonarray.length();
                            Log.d("LENGTH", "length is " + len);
                            // Check for error node in json
                            for (int i = 0; i < len; i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                String name = jsonobject.getString("Name");
                                res.add(name);
                                params.add(new Pair<>("name", name));
                                //Log.d("HEY", "did it" + res);


                            }


                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Log.d("Exception", "Json error: " + e.getMessage());
                        }

                        autoComplete(res);
                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();

                    }

                });

                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                //AUTOCOMPLETE SEARCH Done!!
                client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
            }
        }
    }


    public void autoComplete(ArrayList<String> results) {
        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.AC_place);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, results);

        textView.setAdapter(adapter);

    }


    // finding the user's current location
    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
