package com.example.teamtwo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://localhost:8080/androidMobileApp/select";  // Adjust the URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonA = findViewById(R.id.buttonA);
        Button buttonB = findViewById(R.id.buttonB);
        Button buttonC = findViewById(R.id.buttonC);

        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVote("A");
                Toast.makeText(MainActivity.this, "You chose A", Toast.LENGTH_SHORT).show();
            }
        });

        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVote("B");
                Toast.makeText(MainActivity.this, "You chose B", Toast.LENGTH_SHORT).show();
            }
        });

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVote("C");
                Toast.makeText(MainActivity.this, "You chose C", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVote(String choice) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(SERVER_URL + "?choice=" + choice);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);
                    connection.connect();

                    // Get response code (for debugging purposes)
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Successfully sent vote to the server
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}