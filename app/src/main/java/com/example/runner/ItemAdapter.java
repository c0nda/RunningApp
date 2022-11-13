package com.example.runner;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<ItemTimer> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<ItemTimer> timersList;

    ItemAdapter(Context context, int resource, ArrayList<ItemTimer> timers) {
        super(context, resource, timers);
        this.timersList = timers;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ItemTimer timer = timersList.get(position);

        viewHolder.tvTimerName.setText(timer.getTimer_name());
        viewHolder.editText.setText(String.valueOf(timer.getSeconds()));
        viewHolder.image.setImageResource(R.drawable.ic_timer_holo_blue_dark);

        viewHolder.buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_seconds = viewHolder.editText.getText().toString();
                int seconds = Integer.parseInt(str_seconds);
                if (seconds > 0) {
                    --seconds;
                }
                timer.setSeconds(seconds);
                viewHolder.editText.setText(String.valueOf(seconds));
            }
        });

        viewHolder.buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_seconds = viewHolder.editText.getText().toString();
                int seconds = 0;
                if (!str_seconds.equals("")) {
                    seconds = Integer.parseInt(str_seconds);
                    if (seconds < 1000) {
                        ++seconds;
                    }
                }
                timer.setSeconds(seconds);
                viewHolder.editText.setText(String.valueOf(seconds));
            }
        });

        viewHolder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches("^00")) {
                    viewHolder.editText.setText("0");
                }
                DBTimers dbTimers = new DBTimers(getContext());
                SQLiteDatabase database = dbTimers.getWritableDatabase();
                ContentValues cv = new ContentValues();
                String timer_name = viewHolder.tvTimerName.getText().toString();
                cv.put("name", timer_name);
                if (viewHolder.editText.getText().toString().equals("")) {
                    cv.put("seconds", 0);
                } else {
                    cv.put("seconds", Integer.parseInt(viewHolder.editText.getText().toString()));
                }
                database.update("timers", cv, "name = ?", new String[]{timer_name});
                dbTimers.close();
                database.close();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        final ImageButton buttonPlus, buttonMinus;
        final TextView tvTimerName;
        final EditText editText;
        final ImageView image;

        ViewHolder(View view) {
            buttonPlus = view.findViewById(R.id.buttonPlusTime);
            buttonMinus = view.findViewById(R.id.buttonMinusTime);
            tvTimerName = view.findViewById(R.id.tvText);
            editText = view.findViewById(R.id.editTextTime);
            image = view.findViewById(R.id.ivImage);
        }
    }
}
