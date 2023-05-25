package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {


    ListView lv_stats;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        ArrayList<String> al_stats = getStatPreferences();


        lv_stats = findViewById(R.id.lv_stats);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(StatsActivity.this,android.R.layout.simple_list_item_1,al_stats);
        lv_stats.setAdapter(arrayAdapter);
    }

    private ArrayList<String> getStatPreferences() {
        ArrayList<String> result = new ArrayList<String>();
        sPref = getSharedPreferences("Stats",0);
        for (int i = 2; i <= 5; i++) { //TODO HARDCODED
            int fieldsize = sPref.getInt("fieldsize"+ i,i);
            int turns = sPref.getInt("turns"+ i,0);
            int seconds = sPref.getInt("time"+ i,0);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            result.add(String.format("Ширина: %d, Количество ходов: %d, Время: %d:%02d",fieldsize,turns,minutes,seconds));
        }
        return result;

    }
}