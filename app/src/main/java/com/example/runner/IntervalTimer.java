package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;

public class IntervalTimer extends AppCompatActivity {
    private static final int CM_DELETE_ID = 1;

    private ListView listView;
    private ItemAdapter timerAdapter;
    ArrayList<ItemTimer> data;
    private ImageButton buttonPlusTimer;

    private ImageButton buttonTimer;
    private ImageButton buttonTraining;
    private ImageButton buttonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_timer);
        init();
    }

    private void init() {
        buttonTimer = findViewById(R.id.btnTimer);
        buttonTraining = findViewById(R.id.buttonTraining);
        buttonPlusTimer = findViewById(R.id.btnPlusTimer);

        data = new ArrayList<ItemTimer>();
        data.add(new ItemTimer("Таймер 1"));
        data.add(new ItemTimer("Таймер 2"));
        listView = (ListView) findViewById(R.id.listView);
        timerAdapter = new ItemAdapter(this, R.layout.item, data);
        listView.setAdapter(timerAdapter);
        registerForContextMenu(listView);
    }

    public void onClickButtonPlusTimer(View view) {
        data.add(new ItemTimer("Таймер " + String.valueOf(data.size() + 1)));
        timerAdapter.notifyDataSetChanged();
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
            data.remove(adapterContextMenuInfo.position);
            timerAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void onClickButtonTraining(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}