package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

public class ItemTimer extends AppCompatActivity {
    private String timer_name;
    private int seconds;

    ItemTimer(String timer_name) {
        this.timer_name = timer_name;
        this.seconds = 0;
    }

    public String getTimer_name() {
        return this.timer_name;
    }

    public int getSeconds() {
        return this.seconds;
    }

    public void setTimer_name(String name) {
        this.timer_name = name;
    }

    public void setSeconds(int sec) {
        this.seconds = sec;
    }

}
