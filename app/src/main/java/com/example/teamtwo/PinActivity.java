package com.example.teamtwo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PinActivity extends AppCompatActivity {

    private EditText pinEditText;
    private Button submitPinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        pinEditText = findViewById(R.id.pinInput);
        submitPinButton = findViewById(R.id.submitPin);

        submitPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinEditText.getText().toString();
                if (!pin.isEmpty()) {
                    validatePin(pin);
                } else {
                    Toast.makeText(PinActivity.this, "Please enter the pin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validatePin(String pin) {
        // Validate the pin via an HTTP request to the server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // URL to validate the pin
                    URL url = new URL("http://10.0.2.2:9999/androidMobileApp/pinServlet?action=validate&pin=" + pin);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d("PIN_RESPONSE", "Server response: '" + response.toString().trim() + "'");

                    if ("valid".equals(response.toString().trim())) {
                        runOnUiThread(() -> {
                            Toast.makeText(PinActivity.this, "Pin valid! Redirecting...", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PinActivity.this, MainActivity.class));
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(PinActivity.this, "Invalid Pin", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
