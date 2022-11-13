package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class IntervalTimer extends AppCompatActivity {
    private static final int CM_DELETE_ID = 1;

    private ListView listView;
    private ItemAdapter timerAdapter;
    private ArrayList<ItemTimer> data;
    private ImageButton buttonPlusTimer;
    private AppCompatButton buttonStartTraining;

    private ImageButton buttonTimer;
    private ImageButton buttonTraining;
    private ImageButton buttonProfile;

    private ImageButton buttonMinusCycles;
    private ImageButton buttonPlusCycles;
    private EditText edit_cycles;
    private TextView tvTotalTime;

    private DBTimers dbTimers;
    private SQLiteDatabase database;
    private SharedPreferences cycles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_timer);
        init();
    }

    private void init() {
        buttonProfile = findViewById(R.id.btnProfile);
        buttonTimer = findViewById(R.id.btnTimer);
        buttonTraining = findViewById(R.id.buttonTraining);
        buttonPlusTimer = findViewById(R.id.btnPlusTimer);
        buttonPlusCycles = findViewById(R.id.buttonPlusCycles);
        buttonMinusCycles = findViewById(R.id.buttonMinusCycles);
        edit_cycles = findViewById(R.id.editTextCycles);
        buttonStartTraining = findViewById(R.id.btnStartTimer);
        tvTotalTime = findViewById(R.id.tvTotalTime);

        loadCycles();

        buttonMinusCycles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_seconds = edit_cycles.getText().toString();
                int seconds = Integer.parseInt(str_seconds);
                if (seconds > 0) {
                    --seconds;
                }
                edit_cycles.setText(String.valueOf(seconds));
            }
        });

        buttonPlusCycles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_seconds = edit_cycles.getText().toString();
                int seconds = 0;
                if (!str_seconds.equals("")) {
                    seconds = Integer.parseInt(str_seconds);
                    if (seconds < 1000) {
                        ++seconds;
                    }
                }
                edit_cycles.setText(String.valueOf(seconds));
            }
        });

        data = new ArrayList<ItemTimer>();
        listView = (ListView) findViewById(R.id.listView);
        timerAdapter = new ItemAdapter(this, R.layout.item, data);
        listView.setAdapter(timerAdapter);
        registerForContextMenu(listView);

        dbTimers = new DBTimers(this);
        database = dbTimers.getWritableDatabase();
        Cursor cursor = database.query("timers", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex("name");
            int secondsColIndex = cursor.getColumnIndex("seconds");
            do {
                String timer_name = cursor.getString(nameColIndex);
                int seconds = cursor.getInt(secondsColIndex);
                ItemTimer new_timer = new ItemTimer(timer_name);
                new_timer.setSeconds(seconds);
                data.add(new_timer);
            } while (cursor.moveToNext());
        } else {
            data.add(new ItemTimer("Таймер " + String.valueOf(data.size() + 1)));
            addTimerToDB();
            data.add(new ItemTimer("Таймер " + String.valueOf(data.size() + 1)));
            addTimerToDB();
        }
        cursor.close();
        database.close();

        edit_cycles.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches("^00")) {
                    edit_cycles.setText("0");
                }
                saveCycles();
            }
        });
    }

    private void addTimerToDB() {
        if (!database.isOpen()) {
            database = dbTimers.getWritableDatabase();
            ContentValues cv = new ContentValues();
            String timer_name = data.get(data.size() - 1).getTimer_name();
            int seconds = data.get(data.size() - 1).getSeconds();
            cv.put("name", timer_name);
            cv.put("seconds", seconds);
            database.insert("timers", null, cv);
            database.close();
        } else {
            ContentValues cv = new ContentValues();
            String timer_name = data.get(data.size() - 1).getTimer_name();
            int seconds = data.get(data.size() - 1).getSeconds();
            cv.put("name", timer_name);
            cv.put("seconds", seconds);
            database.insert("timers", null, cv);
        }
    }

    public void onClickButtonPlusTimer(View view) {
        data.add(new ItemTimer("Таймер " + String.valueOf(data.size() + 1)));
        timerAdapter.notifyDataSetChanged();
        addTimerToDB();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Удалить таймер");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            database = dbTimers.getWritableDatabase();
            int position = adapterContextMenuInfo.position;
            ItemTimer timer = timerAdapter.getItem(position);
            String timer_name = timer.getTimer_name();
            database.delete("timers", "name = ?", new String[]{timer_name});
            database.close();

            data.remove(adapterContextMenuInfo.position);
            timerAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void onClickButtonTraining(View view) {
        Intent intent = new Intent(this, TrainingProcess.class);
        startActivity(intent);
        finish();
    }

    public void onClickButtonStartTraining(View view) {
        Intent intent = new Intent(this, IntervalTrainingRunning.class);
        startActivity(intent);
    }

    public void onClickButtonProfile(View view) {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
        finish();
    }

    void saveCycles() {
        cycles = getSharedPreferences("Cycles", MODE_PRIVATE);
        SharedPreferences.Editor editor = cycles.edit();
        String text = edit_cycles.getText().toString();
        if (text.equals("")) {
            editor.putInt("CyclesNumber", 0);
        } else {
            editor.putInt("CyclesNumber", Integer.parseInt(text));
        }
        editor.commit();
    }

    void loadCycles() {
        cycles = getSharedPreferences("Cycles", MODE_PRIVATE);
        String savedText = String.valueOf(cycles.getInt("CyclesNumber", 0));
        edit_cycles.setText(savedText);
    }
}