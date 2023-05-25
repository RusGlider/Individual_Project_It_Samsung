package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//TODO
public class MenuActivity extends AppCompatActivity {

    TextView tv_info_time;
    TextView tv_info_fieldsize;
    TextView tv_info_turns;
    Button b_start;
    Button b_stats;
    RadioGroup rg_fieldsize;

    RadioButton[] rb_fieldsizes = new RadioButton[4];

    int lasttime = 0;
    int fieldsize = 0;
    int turns = 0;
    int checked_fieldsize = 0;

    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tv_info_time = findViewById(R.id.tv_info_time);
        tv_info_fieldsize = findViewById(R.id.tv_info_fieldsize);
        tv_info_turns = findViewById(R.id.tv_info_turns);
        rg_fieldsize = findViewById(R.id.rg_fieldsize);

        rg_fieldsize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case -1: checked_fieldsize = 4; break;
                    case R.id.rb_size2: checked_fieldsize = 2; break;
                    case R.id.rb_size3: checked_fieldsize = 3; break;
                    case R.id.rb_size4: checked_fieldsize = 4; break;
                    case R.id.rb_size5: checked_fieldsize = 5; break;
                    default: checked_fieldsize = 4;
                }
            }
        });

        View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton rb = (RadioButton)view;
                switch(rb.getId()) {
                    case R.id.rb_size2: fieldsize = 2; break;
                    case R.id.rb_size3: fieldsize = 3; break;
                    case R.id.rb_size4: fieldsize = 4; break;
                    case R.id.rb_size5: fieldsize = 5; break;
                    default: fieldsize = 4;
                }
            }
        };

        rb_fieldsizes[0] = findViewById(R.id.rb_size2);
        rb_fieldsizes[1] = findViewById(R.id.rb_size3);
        rb_fieldsizes[2] = findViewById(R.id.rb_size4);
        rb_fieldsizes[3] = findViewById(R.id.rb_size5);
        for (int i = 0; i < 4; i++) {
            //rb_fieldsizes[i].setOnClickListener(radioButtonClickListener);
        }


        loadPreferences();


        try {
            Bundle arguments = getIntent().getExtras();
            if (arguments != null) {
                lasttime = (int) arguments.get("time");
                fieldsize = (int) arguments.get("fieldsize");
                turns = (int) arguments.get("turns");
                Log.w("bundle","loaded bundle: seconds:"+lasttime+", field size:"+fieldsize+", turns:"+turns);
                setStatPreferences(fieldsize,turns,lasttime);
                if (checkMaxStat(fieldsize,turns,lasttime)) {
                    setPreferences();
                }
            }
        }catch (Exception e) {
            loadPreferences();
            //lasttime = 0;
            //fieldsize = 0;
            //turns = 0;
            //Toast.makeText(MenuActivity.this, "Не получилось получить Bundle игры", Toast.LENGTH_SHORT).show();
            Log.w("bundle","Unable to load bundle");
        }
        //
        setInfoText();

        b_start = findViewById(R.id.b_start);

        b_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
                if (rg_fieldsize.getCheckedRadioButtonId() == -1) checked_fieldsize = 4;
                myIntent.putExtra("fieldsize",checked_fieldsize);
                startActivity(myIntent);
            }
        });

        b_stats = findViewById(R.id.b_stats);
        b_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MenuActivity.this,StatsActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private void setInfoText() {
        int seconds = lasttime;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        Log.w("settext","time:"+ seconds+", lasttime:"+lasttime+", fieldsize:"+fieldsize);
        //tv_info_time.setText("Время:"+minutes+":"+seconds);
        tv_info_time.setText(String.format("Время:%d:%02d", minutes, seconds));

        tv_info_turns.setText(String.format("Количество ходов:%d",turns));
        tv_info_fieldsize.setText(String.format("Ширина поля:%d",fieldsize)); //fieldsize % 2 == 0 ? fieldsize : fieldsize-1

    }

    private void loadPreferences() {
        sPref = getPreferences(MODE_PRIVATE);
        turns = sPref.getInt("turns",0);
        fieldsize = sPref.getInt("fieldsize",4);
        lasttime = sPref.getInt("time",0);
        Log.w("loading preferences","time:"+ lasttime+", lasttime:"+lasttime+", fieldsize:"+fieldsize);
    }

    private void setPreferences() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("turns",turns);
        ed.putInt("fieldsize",fieldsize);
        ed.putInt("time",lasttime);
        ed.apply();
        Log.w("saving preferences","time:"+ lasttime+", lasttime:"+lasttime+", fieldsize:"+fieldsize);

    }

    private boolean checkMaxStat(int fieldsize, int newTurns, int newTime) {
        sPref = getSharedPreferences("Stats",0);
        int oldTurns = sPref.getInt("turns"+ fieldsize,999999);
        int oldTime = sPref.getInt("time"+ fieldsize,999999);
        //setStatPreferences(fieldsize, newTurns,newTime);
        return oldTime > newTime || oldTurns > newTurns;
    }

    private void setStatPreferences(int fieldsize, int turns, int time) {
        sPref = getSharedPreferences("Stats",0);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("fieldsize"+ fieldsize,fieldsize);
        ed.putInt("turns"+ fieldsize,turns);
        ed.putInt("time"+ fieldsize,time);
        ed.apply();
    }

    private ArrayList<String> getStatPreferences() {
        ArrayList<String> result = new ArrayList<String>();
        sPref = getPreferences(MODE_PRIVATE);
        for (int i = 2; i <= 5; i++) {
            result.add(sPref.getString(Integer.toString(i),""));
        }
        return result;

    }


}