package com.example.marti.hodoor.Activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marti.hodoor.NukeSSLCerts;
import com.example.marti.hodoor.R;
import com.example.marti.hodoor.Services.Swipes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class activity_user_info extends AppCompatActivity {

    private HashMap<String, String> getSwipes() {
        HashMap<String, String> swipes = new HashMap<>();
        swipes.put("IN", "v praci");
        swipes.put("OUT", "doma");
        swipes.put("OBR", "pauza");
        swipes.put("FBR", "v praci");
        swipes.put("OTR", "na vylete");
        swipes.put("FTR", "v praci");
        return swipes;
    }

    HashMap<String, String> swipes = getSwipes();
    String name, id, hours, swipeId, swipeType, swipeDate;
    Button inBtn, breakBtn, tripBtn, outBtn, fromBreakButton, fromTripButton;
    TextView userName, userHours, userStatus;
    ProgressBar spinner;
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel mChannel;
    ArrayList<Button> allButtons = new ArrayList<>();

    public void onResume() {
        super.onResume();
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        btnSetAllDisabled();

        final String yourToken = "4d36d0a6e802effdfd8254e1cea24ba9f16b61eb";
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://hodoor.eledus.cz/api/keys/";

        NukeSSLCerts.nuke();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();

                            int position = pref.getInt("position", 0);

                            JSONArray allUsers = new JSONArray(response);
                            JSONObject user = allUsers.getJSONObject(position);
                            JSONObject userInfo = user.getJSONObject("user");
                            JSONObject lastSwipe = userInfo.getJSONObject("last_swipe");

                            spinner.setVisibility(View.GONE);

                            editor.putString("hours", userInfo.getString("hours_this_month"));
                            editor.putString("swipeId", userInfo.getString("id"));
                            editor.putString("swipeType", lastSwipe.getString("swipe_type"));
                            editor.putString("swipeDate", lastSwipe.getString("datetime"));
                            editor.apply();

                            refreshUserParm();

                        } catch (JSONException ignored) {
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), error.toString(), Toast.LENGTH_SHORT).show();
            }

        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + yourToken);
                return headers;
            }
        };
        int socketTimeout = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);


    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        activity_user_info.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        inBtn = (Button) findViewById(R.id.inBtn);
        breakBtn = (Button) findViewById(R.id.breakBtn);
        tripBtn = (Button) findViewById(R.id.tripBtn);
        outBtn = (Button) findViewById(R.id.outgoBtn);
        fromBreakButton = (Button) findViewById(R.id.ftbBtn);
        fromTripButton = (Button) findViewById(R.id.ftrBtn);

        allButtons.add(inBtn);
        allButtons.add(breakBtn);
        allButtons.add(tripBtn);
        allButtons.add(outBtn);
        allButtons.add(fromBreakButton);
        allButtons.add(fromTripButton);

        userName = (TextView) findViewById(R.id.userNameInfo);
        userHours = (TextView) findViewById(R.id.hours);
        userStatus = (TextView) findViewById(R.id.status);
        refreshUserParm();



    }

    private void btnSetEnable() {
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        swipeType = pref.getString("swipeType", "");
        switch (swipeType) {
            case "IN":
                inBtn.setEnabled(false);
                fromTripButton.setEnabled(false);
                fromBreakButton.setEnabled(false);
                breakBtn.setEnabled(true);
                outBtn.setEnabled(true);
                tripBtn.setEnabled(true);
                break;

            case "OUT":
                inBtn.setEnabled(true);
                fromTripButton.setEnabled(false);
                fromBreakButton.setEnabled(false);
                breakBtn.setEnabled(false);
                outBtn.setEnabled(false);
                tripBtn.setEnabled(false);
                break;

            case "OBR":
                inBtn.setEnabled(false);
                fromTripButton.setEnabled(false);
                fromBreakButton.setEnabled(true);
                breakBtn.setEnabled(false);
                outBtn.setEnabled(false);
                tripBtn.setEnabled(false);
                break;

            case "FBR":
                inBtn.setEnabled(false);
                fromTripButton.setEnabled(false);
                fromBreakButton.setEnabled(false);
                breakBtn.setEnabled(true);
                outBtn.setEnabled(true);
                tripBtn.setEnabled(true);
                break;

            case "OTR":
                inBtn.setEnabled(false);
                fromTripButton.setEnabled(true);
                fromBreakButton.setEnabled(false);
                breakBtn.setEnabled(false);
                outBtn.setEnabled(false);
                tripBtn.setEnabled(false);
                break;

            case "FTR":
                inBtn.setEnabled(false);
                fromTripButton.setEnabled(false);
                fromBreakButton.setEnabled(false);
                breakBtn.setEnabled(true);
                outBtn.setEnabled(true);
                tripBtn.setEnabled(true);
                break;

        }
        refresh_colors();
    }

    private void btnSetAllDisabled() {
        inBtn.setEnabled(false);
        fromTripButton.setEnabled(false);
        fromBreakButton.setEnabled(false);
        breakBtn.setEnabled(false);
        outBtn.setEnabled(false);
        tripBtn.setEnabled(false);
        refresh_colors();
    }

    private void refresh_colors() {
        for (Button button : allButtons) {
            if (button.isEnabled()) {
                button.getBackground().setColorFilter(Color.parseColor("#00AEEF"), PorterDuff.Mode.DARKEN);
            } else {
                button.getBackground().setColorFilter(Color.parseColor("#bfe5f2"), PorterDuff.Mode.DARKEN);
            }
        }
    }

    private void refreshUserParm() {
        btnSetAllDisabled();
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        name = pref.getString("name", "");
        id = pref.getString("id", "");
        hours = pref.getString("hours", "");
        swipeId = pref.getString("swipeId", "");
        swipeType = pref.getString("swipeType", "");
        swipeDate = pref.getString("swipeDate", "");
        userHours.setText(hours);
        userName.setText(name);
        userStatus.setText(swipes.get(swipeType));
        btnSetEnable();
    }

    private void setSwipeType(String type) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("swipeType", type);
        editor.apply();
    }

    public void btnSwipe(View v) {
        switch (v.getId()) {
            case R.id.inBtn:
                setSwipeType("IN");
                break;
            case R.id.breakBtn:
                setSwipeType("OBR");
                break;
            case R.id.tripBtn:
                setSwipeType("OTR");
                break;
            case R.id.outgoBtn:
                setSwipeType("OUT");
                break;
            case R.id.ftbBtn:
                setSwipeType("FBR");
                break;
            case R.id.ftrBtn:
                setSwipeType("FTR");
                break;
        }
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        Intent service = new Intent(activity_user_info.this, Swipes.class);
        startService(service);
        stopService(service);
        btnSetAllDisabled();
        CountDownTimer timer2 = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                spinner.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                spinner.setVisibility(View.GONE);
                btnSetEnable();
                refreshUserParm();

            }
        };
        timer2.start();
    }

    public void onBackPressed() {
        this.finishAffinity();
    }
}
