/**********
 * author: Tanvi@
 * description: Parsing the JSON places array to List
 **********/
package com.tp.finalloginreg;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceJSONParser {


    public List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> place = null;

        /** Taking each place, parses and adds to list object */
        for(int i=0; i<placesCount;i++){
            try {
                /** Call getPlace with place JSON object to parse the place */
                place = getPlace((JSONObject)jPlaces.get(i));
                placesList.add(place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    /** Parsing the Place JSON object */
    private HashMap<String, String> getPlace(JSONObject jPlace){

        HashMap<String, String> place = new HashMap<String, String>();

        String latitude="";
        String longitude="";
        String name="";

        try {
            name=jPlace.getString("Name");
            latitude = jPlace.getString("latitude");
            longitude = jPlace.getString("longitude");

            place.put("place_name", name);
           // place.put("vicinity", vicinity);
            place.put("latitude", latitude);
            place.put("longitude", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}