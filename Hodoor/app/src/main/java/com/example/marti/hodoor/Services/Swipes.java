package com.example.marti.hodoor.Services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marti.hodoor.NukeSSLCerts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Swipes extends Service {
    public Swipes() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        final String yourToken = "4d36d0a6e802effdfd8254e1cea24ba9f16b61eb";
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://hodoor.eledus.cz/api/swipes/";

        NukeSSLCerts.nuke();
        //set all cetification true

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
            }

        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + yourToken);
                return headers;
            }

            protected Map<String, String> getParams() {
                TimeZone tz = TimeZone.getTimeZone("UTC+1");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                df.setTimeZone(tz);
                String nowAsISO = df.format(new Date());

                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);

                params.put("user",pref.getString("id", ""));
                params.put("datetime",nowAsISO);
                params.put("swipe_type",pref.getString("swipeType",""));
                params.put("id",pref.getString("swipeId", ""));
                return params;
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

}
