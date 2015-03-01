package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


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
        String username = mPreferences.getString(PreferenceKeys.USERNAME, null);

        if (username != null) { launchApp(); }

        mUsernameField = (EditText) findViewById(R.id.username_field);
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
        editor.putString(PreferenceKeys.USERNAME, username);
        editor.putInt(PreferenceKeys.USER_ID, userId);
        editor.apply();
    }

    public void attemptLogin(View view) {

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String loginResponseString = CloudUtilities.getJSON(
                        getString(R.string.sportstat_url) + "user_id/" +
                                getUserInputUsername() + ".json");

                Log.d(getLocalClassName(), loginResponseString);

                try{
                    JSONObject loginResponse = new JSONObject(loginResponseString);

                    if (loginResponse.has("status")) {
                        makeToast("Login failed");
                        return "failure";
                    }

                    if (loginResponse.has("id")) {
                        makeToast("Login successful!");
                        saveUsernameAndUserId(
                                loginResponse.getString("username"),
                                loginResponse.getInt("id")
                        );
                        launchApp();
                    }

                    return "success";
                } catch(Exception e){
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

    }
}
