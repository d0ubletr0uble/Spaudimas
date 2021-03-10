package com.example.myapplication;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import static com.example.myapplication.MainActivity.FILE;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listView = findViewById(R.id.listview);
        ArrayList<String> history = readHistory();
        String header;
        try {
            header = history.remove(0);
        } catch (Exception e) {
            Toast.makeText(this, "KLAIDA", Toast.LENGTH_LONG).show();
            return;
        }
        Collections.reverse(history);
        history.add(0, header);
        listView.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, history));
    }

    private ArrayList<String> readHistory() {
        ArrayList<String> list = new ArrayList<>();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE);
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                list.add(line.replace(';',' '));
                line = reader.readLine();
            }
        } catch (Exception e) {
            Toast.makeText(this, "KLAIDA", Toast.LENGTH_LONG).show();
        }
        return list;
    }

}
