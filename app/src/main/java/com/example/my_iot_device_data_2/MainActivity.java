package com.example.my_iot_device_data_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button spinBTN;
    TextView viewAllTV, countDayTV, dateTV, countDownTV;
    TextView tempTV, HumidTV;

    private static final long COUNTDOWN_INTERVAL = 1000; // Interval of 1 second
    private static final long MILLIS_IN_DAY = 24 * 60 * 60 * 1000; // Milliseconds in a day

    private CountDownTimer countDownTimer;
    private long countdownDuration;
    private long startTime;
    private long timeRemaining;


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

        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(currentTime);

        countDownTV = findViewById(R.id.TVCountDown);

        //spinBTN = findViewById(R.id.BTNspin);
        viewAllTV = findViewById(R.id.TVviewAll);
        countDayTV = findViewById(R.id.TVcountDay);
        tempTV = findViewById(R.id.column2);
        HumidTV = findViewById(R.id.TVhumidity);

        int currentDay = calculateCurrentDay();
        countDayTV.setText(String.valueOf(currentDay));

        dateTV = findViewById(R.id.TVdateAtTop);
        dateTV.setText(formattedDate);

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

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        countdownDuration = sharedPreferences.getLong("countdownDuration", 0 * MILLIS_IN_DAY);
        startTime = sharedPreferences.getLong("startTime", System.currentTimeMillis());
        timeRemaining = countdownDuration - (System.currentTimeMillis() - startTime);

        startCountDown();
    }

    private void getDataFromAPI() {

        // creating a string variable for URL.
        // URL = ""https://sheets.googleapis.com/v4/spreadsheets/{SpreadSheet_ID}/values/{Nama_Tab}?alt=json&key={key_dari_Google_cloud_Console}";
        String url = "https://sheets.googleapis.com/v4/spreadsheets/1hW7CGrAUklLdn6-XYxGfx269Q_cyQm24vb5sYRRNZmg/values/22-06-23?alt=json&key=AIzaSyCRFdWkYbAlHTiljAzzSJ9toRvzkLUJSFY";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONArray feedObj = response.getJSONArray("values");
                    JSONArray row = feedObj.getJSONArray(feedObj.length()-1);
                    String extra = "";
                    String datetime = row.getString(0);
                    StringTokenizer data = new StringTokenizer(row.getString(1), ",") ;
                    String motion = data.nextToken();
                    String humidity = data.nextToken();
                    String temperature = data.nextToken();

                    tempTV.setText(temperature);
                    HumidTV.setText(humidity);

                    if(row.length() > 2 && row.getString(2) != null)
                    {
                        extra = row.getString(2);
                    }

                    Double avgHumid = 0.0;
                    Double avgTemp = 0.0;

                    for( int j = 1; j<feedObj.length(); j++)
                    {
                        JSONArray Values = feedObj.getJSONArray(j);
                        String temp1 = Values.getString(0);
                        String temp2 = Values.getString(1);
                        String temp3 = "";

                        if (Values.length() > 2) {
                            temp3 = Values.getString(2);
                        }

                        StringTokenizer datas = new StringTokenizer(temp2, ",") ;
                        String xxx = (datas.nextToken());
                        Double humiditys = Double.parseDouble(datas.nextToken());
                        Double temperatures = Double.parseDouble(datas.nextToken());

                        avgHumid+= humiditys;
                        avgTemp+= temperatures;
                    }

                    avgHumid/=feedObj.length();
                    avgTemp/=feedObj.length();

                    dataModelArrayList.add(new DataModel(datetime, avgTemp.toString(), avgHumid.toString()));
                    myAdapter = new MyAdapter(dataModelArrayList, MainActivity.this);
                    dataRecyclerRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    dataRecyclerRV.setAdapter(myAdapter);

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
        queue.add(jsonObjectRequest);
    }

    //    Count Down Timer Code
    private int calculateCurrentDay() {
        LocalDate startDate = LocalDate.of(2023, 5, 28); // Replace with your own start date
        LocalDate currentDate = LocalDate.now();
        int currentDay = (int) ChronoUnit.DAYS.between(startDate, currentDate) + 1; // Add 1 to include the start day

        return currentDay;
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeRemaining, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateCountdownDisplay();
            }

            @Override
            public void onFinish() {
                // Countdown finished, handle the event here
                // For example, show a notification or perform a specific action
            }
        };

        countDownTimer.start();
    }

    private void updateCountdownDisplay() {
        long days = TimeUnit.MILLISECONDS.toDays(timeRemaining);
        long hours = TimeUnit.MILLISECONDS.toHours(timeRemaining) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60;

        // Update your UI to display the remaining days, hours, minutes, and seconds
        countDownTV = findViewById(R.id.TVCountDown);
        countDownTV.setText(days + " d " + hours + " hr " + minutes + " min " + seconds + " sec");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the countdown duration, start time, and remaining time to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("countdownDuration", countdownDuration);
        editor.putLong("startTime", startTime);
        editor.putLong("timeRemaining", timeRemaining);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Calculate the time remaining based on the stored countdown duration and elapsed time
        timeRemaining = countdownDuration - (System.currentTimeMillis() - startTime);
        updateCountdownDisplay();
    }


    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
