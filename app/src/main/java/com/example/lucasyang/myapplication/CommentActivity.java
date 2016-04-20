package com.example.lucasyang.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    // JSON Node names
    private static final String TAG_USER = "user";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_ID = "id";
    private static final String TAG_BODY = "body";

    private TextView outtext3;
    private JSONArray comments = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comments);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */

        Intent intent = getIntent();
        String message = intent.getStringExtra(IssueActivity.EXTRA_MESSAGE);

        try {
            GetComments gc = new GetComments();
            gc.execute(message);
        } catch (Exception e) {
            Log.e("CommentActivity", "Exception", e);
            e.printStackTrace();
        }
    }

    class GetComments extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String a = params[0];

            ArrayList<Comment> commentList = new ArrayList<Comment>();

            HttpURLConnection urlConnection = null;
            String resultString = "";

            try {
                URL url = new URL(a);
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) { // that is 200

                    InputStream ins = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null)
                        resultString += line + "\n";

                    bufferedReader.close();

                    ins.close();
                }

                // step 2: JSON parse the string
                if (resultString != null && !resultString.equals("")) {
                    try {
                        comments = new JSONArray(resultString);

                        // a loop that reads json object, creates Comment objects, and saves instances into a list
                        for (int j = 0; j < comments.length(); j++) {

                            JSONObject i = comments.getJSONObject(j);

                            JSONObject user = i.getJSONObject(TAG_USER);
                            String login = user.getString(TAG_LOGIN);
                            String id = user.getString(TAG_ID);

                            String body = i.getString(TAG_BODY);
                            if (body.length() > 140)  // if the length of body is > 140
                                body = body.substring(0, 137) + "...";

                            Comment thisComment = new Comment(login, id, body);
                            commentList.add(thisComment);
                        } // end of for loop
                    } catch (JSONException e) {
                        Log.e("CommentActivity", "Exception", e);
                        e.printStackTrace();
                    } // end of JSON try-catch
                }
            } catch (MalformedURLException e) {
                Log.e("CommentActivity", "MalformedURLException", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("CommentActivity", "IOException", e);
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            } // end of HTTP try-catch

            String listToString = "\n\n\n\n";
            int k = 0;
            for (Comment com : commentList) {
                listToString += com.toString();
            }
            listToString += "\n\n\n\n";
            return listToString;
        }

        @Override
        protected void onPostExecute(String result) {
            outtext3 = (TextView) findViewById(R.id.textview3);
            outtext3.setText(result);
            super.onPostExecute(result);
        }
    }

}
