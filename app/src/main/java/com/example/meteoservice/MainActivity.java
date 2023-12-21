package com.example.meteoservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    static String city = "";
    static List<String> cityNames = new ArrayList<>();
    static ArrayAdapter<String> adapter;
    static final String API_REQUEST = "https://api.weatherapi.com/v1/current.json?key=7c9a701ad40141179f1174357232112&q=Chelyabinsk&aqi=no";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityNames);
        cityNames.add("Ryazan");
        cityNames.add("Chelyabinsk");
        cityNames.add("Moscow");
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        // Установим адаптер для AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter);

        // Найдем кнопку поиска в макете
        Button buttonSearch = findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(view -> {
            city = autoCompleteTextView.getText().toString();
            if (!cityNames.contains(city)) {
                cityNames.add(city);
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityNames);
                autoCompleteTextView.setAdapter(adapter);
            }
            Toast.makeText(MainActivity.this, "Выбран город: " + city, Toast.LENGTH_SHORT).show();
            textView = findViewById(R.id.TV);

            registerReceiver(receiver, new IntentFilter("MeteoService"), RECEIVER_EXPORTED);

            Intent intent = new Intent(this, MeteoService.class);
            startService(intent);
        });
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RESULT", intent.getStringExtra("INFO"));
            String str = intent.getStringExtra("INFO");
            try {
                JSONObject start = new JSONObject(str);
                JSONObject current = start.getJSONObject("current");
                double temp = current.getDouble("temp_c");

                textView.setText("" + temp);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MeteoService.class);
        stopService(intent);
        unregisterReceiver(receiver);
    }
}