package com.example.my_iot_device_data_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button spinBTN;
    TextView viewAllTV, countDayTV, dateTV, countDownTV;

    private CountDownTimer countDownTimer;
    private long timeLeftInInMilliSeconds = 300000; // 5 minutes


    // creating a variable for our array list, adapter class,
    // recycler view, progressbar, nested scroll view
    private ArrayList<DataModel> dataModelArrayList;
    private MyAdapter myAdapter;
    private RecyclerView dataRecyclerRV;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        countDownTV = findViewById(R.id.TVCountDown);

        spinBTN = findViewById(R.id.BTNspin);
        viewAllTV = findViewById(R.id.TVviewAll);
        countDayTV = findViewById(R.id.TVcountDay);
        dateTV = findViewById(R.id.TVdateAtTop);

        // creating a new array list.
        dataModelArrayList = new ArrayList<>();

        // initializing our views.
        dataRecyclerRV = findViewById(R.id.RVdataRecycler);
        loadingPB = findViewById(R.id.idPBLoading);

        // calling a method to load our API.
        getDataFromAPI();


        viewAllTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent h = new Intent(com.example.my_iot_device_data_2.MainActivity.this, SecondPage.class);
                startActivity(h);
            }
        });

        startTimer();
    }

    private void getDataFromAPI() {

        // creating a string variable for URL.
//      url of iot data sheet
                String url = "https://script.google.com/macros/s/AKfycbyhDIz41OyFuaWwPpzWSCvelgjUV24VAGGdf4KhS9kSjLFq-A_uE_fy30RTMGMxsdFx/exec?action=get";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // creating a variable for our JSON object request and passing our URL to it.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONObject feedObj = response.getJSONObject("name");
                    JSONArray entryArray = feedObj.getJSONArray("items");
                    for (int i = 0; i < entryArray.length(); i++) {
                        JSONObject entryObj = entryArray.getJSONObject(i);
                        String column1 = entryObj.getJSONObject("gsx$name").getString("$t");
                        String column2 = entryObj.getJSONObject("gsx$roll").getString("$t");
                        dataModelArrayList.add(new DataModel(column1, column2));

                        // passing array list to our adapter class.
                        myAdapter = new MyAdapter(dataModelArrayList, MainActivity.this);

                        // setting layout manager to our recycler view.
                        dataRecyclerRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                        // setting adapter to our recycler view.
                        dataRecyclerRV.setAdapter(myAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handling on error listener method.
                Toast.makeText(MainActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        // calling a request queue method
        // and passing our json object
        queue.add(jsonObjectRequest);
    }

    //    Count Down Timer Code
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInInMilliSeconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInInMilliSeconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();
//        timerRunning = true;
    }

    private void updateTimer() {
        int minutes = (int) timeLeftInInMilliSeconds / 60000;
        int seconds = (int) timeLeftInInMilliSeconds % 60000 / 1000;

        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        countDownTV.setText(timeLeftText);
    }

    private void stopTimer() {
        countDownTimer.cancel();
//        timerRunning = false;
    }
}