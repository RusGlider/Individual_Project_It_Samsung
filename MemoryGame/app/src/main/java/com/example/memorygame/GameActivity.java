package com.example.memorygame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;



//TODO Запретить горизонтальный экран, добавить кнопку назад, больше картинок и вариативности размера поля
public class GameActivity extends Activity {

    private GridView mGrid;
    private GridAdapter mAdapter;
    TextView tv_time;
    TextView tv_turns;

    long startTime = 0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int seconds = getSecondsElapsed();
            int minutes = seconds / 60;
            seconds = seconds % 60;

            tv_time.setText(String.format("Время:%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    public int fieldSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_game);

        tv_time = findViewById(R.id.tv_game_time);
        tv_turns = findViewById(R.id.tv_game_turns);

        try {
            Bundle arguments = getIntent().getExtras();
            if (arguments != null) {
                fieldSize = (int) arguments.get("fieldsize");
            }
        }catch (Exception e) {
            fieldSize = 4;
            Toast.makeText(GameActivity.this, "Не получилось получить Bundle", Toast.LENGTH_SHORT).show();
        }

        mGrid = findViewById(R.id.field);
        mGrid.setNumColumns(fieldSize);
        mGrid.setEnabled(true);



        mAdapter = new GridAdapter(this,fieldSize,fieldSize % 2 == 0 ? fieldSize : fieldSize-1);
        mGrid.setAdapter(mAdapter);

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAdapter.checkOpenCells();
                mAdapter.openCell(i);
                tv_turns.setText("Ходы:"+mAdapter.turnCount);


                if (mAdapter.checkGameOver()) {
                    gameOver();
                }
            }
        });

        timerHandler.postDelayed(timerRunnable, 0);

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void gameOver() {
        timerHandler.removeCallbacks(timerRunnable);
        Toast toast = Toast.makeText(getApplicationContext(),"Игра закончена", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Intent myIntent = new Intent(GameActivity.this, MenuActivity.class);


        myIntent.putExtra("time",getSecondsElapsed());
        myIntent.putExtra("fieldsize",fieldSize);
        myIntent.putExtra("turns",mAdapter.turnCount+1); //TODO поправить подсчёт ходов

        startActivity(myIntent);
    }

    public int getSecondsElapsed() {
        long millis = System.currentTimeMillis() - startTime;
        return (int) (millis / 1000);
    }



}