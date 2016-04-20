package com.example.lucasyang.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import java.util.Collections;

public class IssueActivity extends AppCompatActivity {

    private TextView outtext;
    private JSONArray issues = null;

    public static final String EXTRA_MESSAGE = "com.example.lucasyang.myapplication.IssueActivity.MESSAGE";

    // JSON Node names
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_BODY = "body";
    private static final String TAG_UPDATED_AT = "updated_at";
    private static final String TAG_COMMENTS_URL = "comments_url";
    private static final String TAG_COMMENTS = "comments";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayout1);

        try {
            GetIssues gi = new GetIssues(layout);
            // this calls 4 steps in AsyncTask: onPreExecute, doInBackground, onProgressUpdate and onPostExecute.
            gi.execute();
        } catch (Exception e) {
            Log.e("IssueActivity", "Exception", e);
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    //       android.os.AsyncTask<Params, Progress, Result>
    class GetIssues extends AsyncTask<String, Void, ArrayList<Issue>> {

        private LinearLayout layout;

        private GetIssues(LinearLayout cl) {
            layout = cl;
        }

        @Override
        protected ArrayList<Issue> doInBackground(String... params) {
            ArrayList<Issue> issueList;
            issueList = new ArrayList<Issue>();

            // step 1: get data from remote, save it as a string
            HttpURLConnection urlConnection = null;
            String resultString = "";

            try {
                URL url = new URL("https://api.github.com/repos/rails/rails/issues");
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
                        issues = new JSONArray(resultString);

                        // a loop that reads json object, creates Issue objects, and saves instances into a list
                        for (int j = 0; j < issues.length(); j++) {

                            JSONObject i = issues.getJSONObject(j);

                            String id = i.getString(TAG_ID);
                            String title = i.getString(TAG_TITLE);

                            String body = i.getString(TAG_BODY);
                            if(body.length() > 140)  // if the length of body is > 140
                                body = body.substring(0, 137) + "...";

                            String updated_at = i.getString(TAG_UPDATED_AT);
                            String comments = i.getString(TAG_COMMENTS);
                            String comments_url = i.getString(TAG_COMMENTS_URL);

                            Issue thisIssue = new Issue(id, title, updated_at, body, comments, comments_url);
                            issueList.add(thisIssue);
                            Collections.sort(issueList);
                            Collections.reverse(issueList); // if un-comment, display most recent first

                        } // end of for loop
                    } catch (JSONException e) {
                        Log.e("IssueActivity", "JSONException", e);
                        e.printStackTrace();
                    } // end of JSON try-catch
                }
            } catch (MalformedURLException e) {
                Log.e("IssueActivity", "MalformedURLException", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("IssueActivity", "IOException", e);
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            } // end of HTTP try-catch

            // step 3: return the list, in format
            return issueList;
        } // end of doInBackground method


        @Override
        //protected void onPostExecute(String result) {
        protected void onPostExecute(ArrayList<Issue> al) {
            for(Issue is : al) {
                // step 4: display each issue in one TextView

                TextView tv = new TextView(layout.getContext());
                tv.setText(is.toString());
                layout.addView(tv);
//                tv.callOnClick();
                tv.setClickable(true);
                final String passedURL = is.comments_url;

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),CommentActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, passedURL);
                        // Add code here if transferring more data (say, Issue ID) is desired
                        startActivity(intent);
                    }
                });
            }

            super.onPostExecute(al);
        } // end of onPostExecute method
    } // end of GetIssues class definition


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
