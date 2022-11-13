package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Profile extends AppCompatActivity {
    SharedPreferences person;

    ImageButton buttonTimer;
    ImageButton buttonTraining;
    ImageButton buttonProfile;

    TextInputEditText etWeight;
    TextInputEditText etHeight;

    TextInputLayout tilWeight;
    TextInputLayout tilHeight;

    AppCompatButton buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    private void init() {
        buttonTimer = findViewById(R.id.buttonTimer);
        buttonTraining = findViewById(R.id.buttonTraining);
        buttonProfile = findViewById(R.id.btnProfile);

        buttonSave = findViewById(R.id.buttonSave);

        tilWeight = findViewById(R.id.textInputLayoutWeight);
        etWeight = findViewById(R.id.etWeight);

        person = getSharedPreferences("Person", MODE_PRIVATE);
        String savedText = String.valueOf(person.getInt("Weight", 0));
        etWeight.setText(savedText);

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().matches("^0")) {
                    etWeight.setText("");
                }
                if (editable.toString().equals("") || Integer.parseInt(editable.toString()) < 10 || Integer.parseInt(editable.toString()) > 200) {
                    tilWeight.setError("Недопустимое значение");
                } else {
                    tilWeight.setError(null);
                }
            }
        });

        tilHeight = findViewById(R.id.textInputLayoutHeight);
        etHeight = findViewById(R.id.etHeight);

        person = getSharedPreferences("Person", MODE_PRIVATE);
        savedText = String.valueOf(person.getInt("Height", 0));
        etHeight.setText(savedText);

        etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().matches("^0")) {
                    etHeight.setText("");
                }
                if (editable.toString().equals("") || Integer.parseInt(editable.toString()) < 100 || Integer.parseInt(editable.toString()) > 250) {
                    tilHeight.setError("Недопустимое значение");
                } else {
                    tilHeight.setError(null);
                }
            }
        });
    }

    public void onClickButtonSave(View view) {
        if (etHeight.getText().toString().equals("") || Integer.parseInt(etHeight.getText().toString()) < 100 || Integer.parseInt(etHeight.getText().toString()) > 250) {
            tilHeight.setError("Недопустимое значение");
        } else if (etWeight.getText().toString().equals("") || Integer.parseInt(etWeight.getText().toString()) < 10 || Integer.parseInt(etWeight.getText().toString()) > 200) {
            tilWeight.setError("Недопустимое значение");
        } else {
            tilWeight.setError(null);
            tilHeight.setError(null);
            person = getSharedPreferences("Person", MODE_PRIVATE);
            SharedPreferences.Editor w_editor = person.edit();
            String w_text = etWeight.getText().toString();
            if (w_text.equals("")) {
                w_editor.putInt("Weight", 0);
            } else {
                w_editor.putInt("Weight", Integer.parseInt(w_text));
            }
            w_editor.apply();

            person = getSharedPreferences("Person", MODE_PRIVATE);
            SharedPreferences.Editor h_editor = person.edit();
            String h_text = etHeight.getText().toString();
            if (h_text.equals("")) {
                h_editor.putInt("Height", 0);
            } else {
                h_editor.putInt("Height", Integer.parseInt(h_text));
            }
            h_editor.apply();

            Toast toast = Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickButtonTraining(View view) {
        Intent intent = new Intent(this, TrainingProcess.class);
        startActivity(intent);
        finish();
    }

    public void onClickButtonTimer(View view) {
        Intent intent = new Intent(this, IntervalTimer.class);
        startActivity(intent);
        finish();
    }
}