package com.example.android.fact_check;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class parameters extends AppCompatActivity {
    public String language, result_size;
    private ArrayList<String> languages = new ArrayList<String>();
    private ArrayList<String> languageCode = new ArrayList<String>();
    private EditText et;
    private Button saveButton;

    public String getResult_size() {
        et = findViewById(R.id.result_size);
        et.setText(result_size + "");
        return result_size;
    }

    public void setResult_size(String result_size) {
        et = findViewById(R.id.result_size);
        result_size = et.getText().toString();
        this.result_size = result_size;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        languages.add("English");
        languages.add("Hindi");
        languages.add("All languages");
        languageCode.add("En");
        languageCode.add("Hi");
        languageCode.add("");
        final Intent intent = getIntent();
        language = intent.getStringExtra("language");
        result_size = intent.getStringExtra("resultSize");


        //spinner
        Spinner spinner = (Spinner) findViewById(R.id.language_spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Log.i("response", "I am above set selection and lang is" + language + "and languages.indexOf(language) is " + languages.indexOf(language));
        spinner.setSelection(languageCode.indexOf(language));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("response", adapterView.getItemAtPosition(i) + "");
                if (adapterView.getItemAtPosition(i) == "English") {
                    language = "En";
                } else if (adapterView.getItemAtPosition(i) == "Hindi") {
                    language = "Hi";
                } else {
                    language = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getLanguage();
        getResult_size();
        //save button
        saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguage(language);
                setResult_size(result_size);
                Log.i("response", language + "::::" + result_size);
                intent.putExtra("language", language);
                intent.putExtra("resultSize", result_size);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Log.i("response", language + "::::" + result_size);
    }
}
