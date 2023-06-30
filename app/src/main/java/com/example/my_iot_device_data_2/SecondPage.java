package com.example.my_iot_device_data_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SecondPage extends AppCompatActivity {
    ImageView backIV;

    // creating a variable for our array list, adapter class,
    // recycler view, progressbar, nested scroll view
    private ArrayList<DataModel> dataModelArrayList;
    private MyAdapter myAdapter;
    private RecyclerView dataRecyclerRV;
    private ProgressBar loadingPB;
    private DecimalFormat Df;

    //private String[] DateArray = {"31-05-23", "01-06-23", "02-06-23", "03-06-23", "04-06-23", "05-06-23", "06-06-23", "07-06-23", "08-06-23", "11-06-23", "13-06-23", "14-06-23", "22-0-23"};
    private String[] DateArray = {"07-06-23", "08-06-23", "11-06-23", "13-06-23", "14-06-23", "22-06-23"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        backIV = findViewById(R.id.IVback);
        loadingPB = findViewById(R.id.idPBLoading);

        // creating a new array list.
        dataModelArrayList = new ArrayList<>();

        // initializing our views.
        dataRecyclerRV = findViewById(R.id.RVdataRecycler);
        loadingPB = findViewById(R.id.idPBLoading);

        Df = new DecimalFormat("0.00");

        // calling a method to load our API.

        myAdapter = new MyAdapter(dataModelArrayList, SecondPage.this);

        dataRecyclerRV.setLayoutManager(new LinearLayoutManager(SecondPage.this));
        dataRecyclerRV.setAdapter(myAdapter);


        for (int i =0; i<DateArray.length; i++){
            getDataFromAPI(DateArray[i]);
        }


        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent h = new Intent(SecondPage.this, MainActivity.class);
                startActivity(h);
            }
        });
    }


    private void getDataFromAPI(String dateAPI) {

        String url = "https://sheets.googleapis.com/v4/spreadsheets/1hW7CGrAUklLdn6-XYxGfx269Q_cyQm24vb5sYRRNZmg/values/"+dateAPI+"?alt=json&key=AIzaSyCRFdWkYbAlHTiljAzzSJ9toRvzkLUJSFY";
        RequestQueue queue = Volley.newRequestQueue(SecondPage.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONArray feedObj = response.getJSONArray("values");
                    JSONArray row = feedObj.getJSONArray(feedObj.length() - 1);

                    Double avgHumid = 0.0;
                    Double avgTemp = 0.0;

                    for (int j = 1; j < feedObj.length(); j++) {
                        JSONArray Values = feedObj.getJSONArray(j);
                        String temp1 = Values.getString(0);
                        String temp2 = Values.getString(1);
                        String temp3 = "";

                        if (Values.length() > 2) {
                            temp3 = Values.getString(2);
                        }

                        Log.d("TEST", temp2);

                        StringTokenizer datas = new StringTokenizer(temp2, ",");
                        String xxx = (datas.nextToken());
                        Double humiditys = Double.parseDouble(datas.nextToken());
                        Double temperatures = Double.parseDouble(datas.nextToken());

                        avgHumid += humiditys;
                        avgTemp += temperatures;
                    }

                    avgHumid /= feedObj.length();
                    avgTemp /= feedObj.length();

                    dataModelArrayList.add(new DataModel(dateAPI, Df.format(avgTemp).toString(),  Df.format(avgHumid).toString()));

                    myAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handling on error listener method.
                Toast.makeText(SecondPage.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }
}