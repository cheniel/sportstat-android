package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity {

    private EditText mUsernameField;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = mPreferences.getString(Globals.USERNAME, null);
        mUsernameField = (EditText) findViewById(R.id.username_field);

        if (username != null) {
            launchApp();
        }

    }

    private void launchApp() {
        finish();
        Intent intent = new Intent(".activities.MainActivity");
        startActivity(intent);
    }

    private String getUserInputUsername() {
        return mUsernameField.getText().toString();
    }

    private void saveUsernameAndUserId(String username, int userId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Globals.USERNAME, username);
        editor.putInt(Globals.USER_ID, userId);
        editor.apply();
    }

    public void attemptLogin(View view) {

        if (getUserInputUsername().isEmpty()) {
            Toast.makeText(this, "Please input username.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String loginResponseString = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url) + "user_id/" +
                                getUserInputUsername() + ".json");

                Log.d(getLocalClassName(), loginResponseString);

                try {
                    JSONObject loginResponse = new JSONObject(loginResponseString);

                    if (loginResponse.has("status")) {
                        makeToast("Login failed. User may not exist.");
                        return "failure";
                    }

                    if (loginResponse.has("id")) {
                        makeToast("Login successful!");
                        saveUsernameAndUserId(
                                loginResponse.getString("username"),
                                loginResponse.getInt("id")
                        );
                        launchApp();
                        return "success";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                makeToast("Login failed");
                return "failure";
            }

            private void makeToast(final String toast) {
                handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                        }
                    }
                );
            }
        }.execute();

    }

    public void attemptRegistration(View view) {

        if (getUserInputUsername().isEmpty()) {
            Toast.makeText(this, "Please input username.", Toast.LENGTH_SHORT).show();
            return;
        }

        // create post object
        final JSONObject post = new JSONObject();
        try {
            post.put("username", getUserInputUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String registrationResponseString = CloudUtilities.post(
                        getString(R.string.sportstat_url) + "users", post
                );

                Log.d(getLocalClassName(), registrationResponseString);

                try {
                    JSONObject registrationResponse = new JSONObject(registrationResponseString);

                    if (registrationResponse.has("status")) {
                        makeToast("Registration failed.");
                        return "failure";
                    }

                    if (registrationResponse.has("username")) {
                        String username = registrationResponse.getString("username");
                        if (username.equals(getUserInputUsername())) {
                            saveUsernameAndUserId(
                                    registrationResponse.getString("username"),
                                    registrationResponse.getInt("id")
                            );
                            makeToast("Registration successful. Login now.");
                            return "success";
                        } else {
                            makeToast("Username already taken.");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                makeToast("Registration failed");
                return "failure";
            }

            private void makeToast(final String toast) {
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }.execute();


    }


}
