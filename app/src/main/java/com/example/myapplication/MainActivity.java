package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String FILE = "Spaudimas.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.history) {
            try {
                startActivity(new Intent(this, HistoryActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "KLAIDA", Toast.LENGTH_LONG).show();
            }
        } else if (item.getItemId() == R.id.clear) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteLog();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ar tikrai norite išvalyti istoriją?").setPositiveButton("Taip", dialogClickListener)
                    .setNegativeButton("Ne", dialogClickListener).show();
        }
        return true;
    }

    private void deleteLog() {
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE).delete();
        Toast.makeText(this, "Išvalyta", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void handleClick(View v) {
        // Get all data needed
        String upper = ((EditText) findViewById(R.id.pressureHigh)).getText().toString();
        String lower = ((EditText) findViewById(R.id.pressureLow)).getText().toString();
        String pulse = ((EditText) findViewById(R.id.heartbeat)).getText().toString();
        String spo2 = ((EditText) findViewById(R.id.spo2)).getText().toString();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        getStoragePermissions();

        writeToFile(String.format("%s;%s;%s;%s;%s;%s%s", date, time, upper, lower, pulse, spo2, System.lineSeparator()));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writeToFile(String message) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE);

        //Check if file has headers
        boolean headerFlag = false;
        if (!file.exists())
            headerFlag = true;

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            if (headerFlag)
                fos.write("sep=;\nData;Laikas;Virsutinis;Apatinis;Pulsas;%SpO2\n".getBytes());

            fos.write(message.getBytes());
            fos.close();
            playSound();

            Toast.makeText(this, "Išsaugota", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "KLAIDA", Toast.LENGTH_LONG).show();
        }
    }


    private void getStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "Prieiga suteikta, paspauskite mygtuką dar kartą", Toast.LENGTH_SHORT).show();
    }

    private void playSound() {
        MediaPlayer player = MediaPlayer.create(this, R.raw.pop);
        player.start();
    }

}