/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.authenticate;

import androidx.appcompat.app.AppCompatActivity;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.main.HomeActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This activity is the entry point into our app.
 * This activity is responsible for listening to and launching the LogInFragment, and RegisterFragment.
 * We use firebase for authentication, as well as heroku.
 * @author Kevin Bui
 * @author Codie Bryan
 */
public class SignInActivity extends AppCompatActivity  implements LogInFragment.LoginFragmentListenter, RegisterFragment.RegisterFragmentListener {

    /**We will use shared preferences in order to remember users and their emails while on the app*/
    private SharedPreferences mSharedPreferences;
    /**This variable stores JSON information from GET/POST requests*/
    private JSONObject mUserJSON;


    private FirebaseAuth mAuth;

    /**
     * If the user is already logged in, then there will be no need to login again.
     * Otherwise, the LogInFragment is launched and the user will have to login.
     * @param savedInstanceState null
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);

        if (mAuth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            mSharedPreferences.edit()
                    .putString(getString(R.string.USER_EMAIL),
                            mAuth.getCurrentUser().getEmail()).apply();
            mSharedPreferences = getSharedPreferences(getString(R.string.FIREBASE_UID), Context.MODE_PRIVATE);
            mSharedPreferences.edit()
                    .putString(getString(R.string.FIREBASE_UID),
                            mAuth.getCurrentUser().getUid()).apply();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                .apply();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.sign_in_fragment_id, new LogInFragment())
                .commit();
    }




    /**
     * A POST request will be sent to https://dawggit.herokuapp.com/login
     * to check if the user account exists.
     * @param email user email
     * @param pwd user password
     */
    @Override
    public void login(String email, String pwd) {


        StringBuilder url = new StringBuilder(getString(R.string.post_login));
        mUserJSON = new JSONObject();
        try {
            mUserJSON.put("email", email);
            mUserJSON.put("password", pwd);
            mSharedPreferences
                    .edit()
                    .putString(getString(R.string.USER_EMAIL), email)
                    .apply();
            mSharedPreferences = getSharedPreferences(getString(R.string.FIREBASE_UID), MODE_PRIVATE);
            if(mAuth.getCurrentUser() != null) {
                mSharedPreferences
                        .edit()
                        .putString(getString(R.string.FIREBASE_UID), mAuth.getCurrentUser().getUid())
                        .apply();
            }
            new LoginAsyncTask().execute(url.toString());
        } catch(JSONException e) {
            Toast.makeText(this, "Error with JSON creation on logging in"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A POST request will be sent to https://dawggit.herokuapp.com/register
     * to add the user account into the database.
     * @param username user username
     * @param email user email
     * @param password user password
     */
    @Override
    public void registerNewAccount(String username, String email, String password) {

        StringBuilder url = new StringBuilder(getString(R.string.post_register));
        mUserJSON = new JSONObject();
        try {
            mUserJSON.put("username", username);
            mUserJSON.put("email", email);
            mUserJSON.put("password", password);
            new RegisterAccountAsyncTask().execute(url.toString());
        } catch (JSONException e) {
            Toast.makeText(this, "Error with JSOM creation on registering an account: " +
                            e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This class allows asynchronous user registration.
     */
    private class RegisterAccountAsyncTask extends AsyncTask<String, Void, String> {

        /**
         * Background Task
         * @param urls urls
         * @return JSON string
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(urlConnection.getOutputStream());

                    // For Debugging
                    Log.i("REGISTER USER", mUserJSON.toString());
                    wr.write(mUserJSON.toString());
                    wr.flush();
                    wr.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to register a new account: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Once the task is complete we check if registering was a success or failure.
         * If it was a success then the user account will be in the database.
         * If it was a failure then the user gets an error message and has to try again.
         * @param s the string representation of the JSON response.
         */
        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to register")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "Registered successfully"
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Registration failed: "
                                    + jsonObject.getString("error")
                            , Toast.LENGTH_LONG).show();
                    Log.e("REGISTER USER", jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Registering User"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e("REGISTER USER", e.getMessage());
            }
        }
    }

    /**
     * This class is responsible is responsible for async login.
     */
    private class LoginAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(urlConnection.getOutputStream());

                    // For Debugging
                    Log.i("LOG_IN_USER", mUserJSON.toString());
                    wr.write(mUserJSON.toString());
                    wr.flush();
                    wr.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to login: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Once the task is complete, we check if logging in was successful.
         * If the credentials matched then they are logged in, and the user's email is remembered.
         * The user then gets taken to the home activity.
         * Else, the user is prompted of an error or invalid credentials.
         * @param s The string form of the JSON response.
         */
        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to login")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message")
                            , Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);

                    startActivity(i);
                    finish();

                }
                else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    Log.d("LOGIN", jsonObject.getString("message"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Logging In"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e("LOGIN", e.getMessage());
            }
        }
    }
}
