package com.example.marti.hodoor.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marti.hodoor.NukeSSLCerts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetAllUsersInfo extends Service {
    private static HashMap<String, ArrayList<String>> usersInfo = new HashMap<>();

    public GetAllUsersInfo() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String yourToken = "4d36d0a6e802effdfd8254e1cea24ba9f16b61eb";
        //	e3c4b0dcb6e9236e4875fb021a18c23e4cc18f06
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://hodoor.eledus.cz/api/keys/";
        //  http://127.0.0.1:8000/api/keys/

        NukeSSLCerts.nuke();
        //set all cetification true

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        ArrayList<String> AllUsersName = new ArrayList<String>();
                        ArrayList<String> AllHours_this_month = new ArrayList<String>();
                        ArrayList<String> AllUserId = new ArrayList<String>();
                        ArrayList<String> AllSwipeDatetime = new ArrayList<String>();
                        ArrayList<String> AllSwipeType = new ArrayList<String>();
                        ArrayList<String> AllSwipeId = new ArrayList<String>();
                        ArrayList<String> AllIds = new ArrayList<String>();

                        //create lists of data
                        try {
                            JSONArray allUsers = new JSONArray(response);
                            for (int i = 0; i < allUsers.length(); i++) {
                                JSONObject user = allUsers.getJSONObject(i);
                                JSONObject userInfo = user.getJSONObject("user");

                                AllIds.add(user.getString("id"));
                                AllUsersName.add(userInfo.getString("username"));
                                AllHours_this_month.add(userInfo.getString("hours_this_month"));
                                AllUserId.add(userInfo.getString("id"));

                                JSONObject lastSwipe = userInfo.getJSONObject("last_swipe");
                                AllSwipeDatetime.add(lastSwipe.getString("datetime"));
                                AllSwipeType.add(lastSwipe.getString("swipe_type"));
                                AllSwipeId.add(Integer.toString(lastSwipe.getInt("id")));

                                //procesing of json from web
                            }
                            usersInfo.put("name", AllUsersName);
                            usersInfo.put("hours", AllHours_this_month);
                            usersInfo.put("id", AllUserId);
                            usersInfo.put("swipeDate", AllSwipeDatetime);
                            usersInfo.put("swipeType", AllSwipeType);
                            usersInfo.put("swipeId", AllSwipeId);
                            usersInfo.put("RFids",AllIds);
                              } catch (JSONException ignored) {
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + yourToken);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        this.stopSelf();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static HashMap getUserInformation() {
        return usersInfo;
    }
}
