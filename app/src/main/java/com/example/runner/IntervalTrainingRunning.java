package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class IntervalTrainingRunning extends AppCompatActivity {

    private ImageButton buttonResume;
    private ImageButton buttonStop;
    private ArrayList<Long> timers_values;
    private ArrayList<String> timers_names;

    private DBTimers dbTimers;
    private SQLiteDatabase database;


    private CountDownTimer countDownTimer;
    private CountDownTimer countDownTimer2;
    private TextView tvCountDown;
    private TextView tvTotalTime;
    private TextView tvTimerName;
    private TextView tvNumberOfCycles;

    private int numberOfCycles;
    private boolean timer_running;
    private long timeLeftInMillis;
    private long timeLeftInMillis2;
    private long total_time;
    private int position = 0;
    private int counter = 0;
    private int cycles_counter = 1;

    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_training_running);
        init();
    }

    private void init() {
        buttonResume = findViewById(R.id.Resume);
        buttonStop = findViewById(R.id.StopTraining);
        tvCountDown = findViewById(R.id.tvCurrentTimer);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvTimerName = findViewById(R.id.tvTimerName);
        tvNumberOfCycles = findViewById(R.id.tvNumberOfCycles);

        buttonStop.setVisibility(View.GONE);
        buttonResume.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));

        // получаем значения времени и название каждого таймера и записываем в один массив
        timers_values = new ArrayList<Long>();
        timers_names = new ArrayList<String>();
        dbTimers = new DBTimers(this);
        database = dbTimers.getReadableDatabase();
        Cursor cursor = database.query("timers", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int secondsColIndex = cursor.getColumnIndex("seconds");
            int nameColIndex = cursor.getColumnIndex("name");
            do {
                long seconds = cursor.getInt(secondsColIndex);
                String timer_name = cursor.getString(nameColIndex);
                timers_values.add(seconds * 1000);
                timers_names.add(timer_name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        // получаем количество циклов
        SharedPreferences cycles = getSharedPreferences("Cycles", MODE_PRIVATE);
        numberOfCycles = cycles.getInt("CyclesNumber", 0);

        for (int i = 0; i < timers_values.size(); ++i) {
            total_time += timers_values.get(i);
        }
        total_time *= numberOfCycles;
        timeLeftInMillis = total_time + 1000;
        timeLeftInMillis2 = timers_values.get(position);
        startTimer();
        startTimer2();
        tvNumberOfCycles.setText(String.format(Locale.getDefault(), "%d/%d", cycles_counter, numberOfCycles));
        mediaPlayer1 = MediaPlayer.create(this, R.raw.sound321);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.done);
    }

    public void onClickResumeTraining(View view) {
        if (timer_running) {
            pauseTimer();
        } else {
            startTimer();
            startTimer2();
        }
    }

    public void onClickStopTraining(View view) {
        finish();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timer_running = false;
                mediaPlayer2.start();
                finish();
            }
        }.start();

        timer_running = true;
        buttonStop.setVisibility(View.GONE);
        buttonResume.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
    }

    private void startTimer2() {
        tvTimerName.setText(timers_names.get(position));
        countDownTimer2 = new CountDownTimer(timeLeftInMillis2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis2 = millisUntilFinished;
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void updateCountDownText() {
        long all_seconds1 = timeLeftInMillis / 1000;
        long all_seconds2 = timeLeftInMillis2 / 1000;

        if (all_seconds2 == 1 || all_seconds2 == 2 || all_seconds2 == 3) {
            mediaPlayer1.start();
        }

        if (all_seconds2 == 0 && ++counter < numberOfCycles * timers_values.size()) {
            countDownTimer2.onFinish();
            ++position;
            mediaPlayer2.start();
            if (position == timers_values.size()) {
                position = 0;
                ++cycles_counter;
                tvNumberOfCycles.setText(String.format(Locale.getDefault(), "%d/%d", cycles_counter, numberOfCycles));
            }
            timeLeftInMillis2 = timers_values.get(position);
            startTimer2();
            all_seconds2 = timeLeftInMillis2 / 1000;
            long minutes2 = all_seconds2 / 60;
            long seconds2 = all_seconds2 % 60;
            String timeFormatted2 = String.format(Locale.getDefault(), "%02d:%02d", minutes2, seconds2);
            tvCountDown.setText(timeFormatted2);
        } else {
            long minutes2 = all_seconds2 / 60;
            long seconds2 = all_seconds2 % 60;
            String timeFormatted2 = String.format(Locale.getDefault(), "%02d:%02d", minutes2, seconds2);
            tvCountDown.setText(timeFormatted2);
        }

        long minutes1 = all_seconds1 / 60;
        long seconds1 = all_seconds1 % 60;
        String timeFormatted1 = String.format(Locale.getDefault(), "%02d:%02d", minutes1, seconds1);

        tvTotalTime.setText(timeFormatted1);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        countDownTimer2.cancel();
        timer_running = false;
        buttonStop.setVisibility(View.VISIBLE);
        buttonResume.setImageDrawable(getResources().getDrawable(R.drawable.ic_resume));
    }
}
