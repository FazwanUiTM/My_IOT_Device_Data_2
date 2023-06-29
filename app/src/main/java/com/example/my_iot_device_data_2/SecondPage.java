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

import java.util.ArrayList;

public class SecondPage extends AppCompatActivity {
    ImageView backIV;

    // creating a variable for our array list, adapter class,
    // recycler view, progressbar, nested scroll view
    private ArrayList<DataModel> dataModelArrayList;
    private MyAdapter myAdapter;
    private RecyclerView dataRecyclerRV;
    private ProgressBar loadingPB;


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

        // calling a method to load our API.
        getDataFromAPI();

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent h = new Intent(SecondPage.this, MainActivity.class);
                startActivity(h);
            }
        });
    }


    private void getDataFromAPI() {

            // creating a string variable for URL.
//      url of iot data sheet
            String url = "https://script.google.com/macros/s/AKfycbyhDIz41OyFuaWwPpzWSCvelgjUV24VAGGdf4KhS9kSjLFq-A_uE_fy30RTMGMxsdFx/exec?action=get";

            // creating a new variable for our request queue
            RequestQueue queue = Volley.newRequestQueue(SecondPage.this);

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
                            myAdapter = new MyAdapter(dataModelArrayList, SecondPage.this);

                            // setting layout manager to our recycler view.
                            dataRecyclerRV.setLayoutManager(new LinearLayoutManager(SecondPage.this));

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
                    Toast.makeText(SecondPage.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
                }
            });
            // calling a request queue method
            // and passing our json object
            queue.add(jsonObjectRequest);
        }

}