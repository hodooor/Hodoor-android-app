package com.example.marti.hodoor.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marti.hodoor.R;
import com.example.marti.hodoor.Services.GetAllUsersInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    Button loginBtn;
    EditText password, userName;
    TextView wrongPass;
    Intent service;
    CheckBox checkBox;
    Boolean badName = true;

    public void onResume() {
        super.onResume();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        boolean checked = pref.getBoolean("checked_in", false);
        if (checked) {
            startActivity(new Intent(Login.this, activity_user_info.class));
            finish();
        }

        loginBtn.setEnabled(false);
        CountDownTimer timer = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                loginBtn.setEnabled(true);
            }
        };
        timer.start();

    }

    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        boolean checked = pref.getBoolean("checked_in", false);
        if (checked) {
            startActivity(new Intent(Login.this, activity_user_info.class));
            finish();
        }
        Login.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        password = (EditText) findViewById(R.id.userPassword);
        userName = (EditText) findViewById(R.id.userName);

        loginBtn.setEnabled(false);

        CountDownTimer timer = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                loginBtn.setEnabled(true);
            }
        };


        timer.start();
        service = new Intent(this, GetAllUsersInfo.class);
        startService(service);
        stopService(service);
        loginBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        stopService(service);
                        String pass = password.getText().toString();
                        String user = userName.getText().toString();
                        HashMap usersInfo = GetAllUsersInfo.getUserInformation();
                        ArrayList allNames = (ArrayList) usersInfo.get("name");
                        ArrayList ids = (ArrayList) usersInfo.get("RFids");
                        ArrayList allIds = (ArrayList) usersInfo.get("id");
                        if (ids != null) {
                            for (int i = 0; i < allNames.size(); i++) {
                                String name = (String) allNames.get(i);
                                String id = (String) allIds.get(i);
                                String RFid = (String) ids.get(i);
                                if (pass.equals(RFid)&&user.equals(name)) {
                                    if (checkBox.isChecked()) {
                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putBoolean("checked_in", true);
                                        editor.apply();
                                    }

                                    badName = false;
                                    saveUser(i, usersInfo, name, id);
                                    Intent userinfo = new Intent(Login.this, activity_user_info.class);
                                    startActivity(userinfo);
                                    break;
                                }
                            }
                            if (badName) {
                                Toast.makeText(getApplication(), "wrong username or password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "something is wrong with server", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );

    }



    private void saveUser(int position, HashMap usersInfo, String name, String id) {
        ArrayList allHours = (ArrayList) usersInfo.get("hours");
        ArrayList allSwipeId = (ArrayList) usersInfo.get("swipeId");
        ArrayList allSwipeType = (ArrayList) usersInfo.get("swipeType");
        ArrayList allSwipeDate = (ArrayList) usersInfo.get("swipeDate");

        String hours = (String) allHours.get(position);
        String swipeId = (String) allSwipeId.get(position);
        String swipeType = (String) allSwipeType.get(position);
        String swipeDate = (String) allSwipeDate.get(position);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", id);
        editor.putString("name", name);
        editor.putString("hours", hours);
        editor.putString("swipeId", swipeId);
        editor.putString("swipeType", swipeType);
        editor.putString("swipeDate", swipeDate);
        editor.putInt("position", position);

        editor.apply();

    }

}
