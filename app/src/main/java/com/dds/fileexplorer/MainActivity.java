package com.dds.fileexplorer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText edit_query;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_query = (EditText) findViewById(R.id.edit_query);
        text = (TextView) findViewById(R.id.text);

        new LocalFileLoader(this, LocalFileLoader.TYPE_ZIP).load(new LocalFileLoader.LocalFileLoadListener() {
            @Override
            public void loadComplete(ArrayList<String> files) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String str : files) {
                    stringBuilder.append(str).append("\n \n");
                }
                text.setText(stringBuilder.toString());
            }
        });


        edit_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String fuzzy = edit_query.getText().toString().trim();
                new LocalFileLoader(MainActivity.this, LocalFileLoader.TYPE_ZIP).search(new LocalFileLoader.LocalFileLoadListener() {
                    @Override
                    public void loadComplete(ArrayList<String> files) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String str : files) {
                            stringBuilder.append(str).append("\n \n");
                        }
                        text.setText(stringBuilder.toString());
                    }
                }, fuzzy);
            }
        });

    }
}
