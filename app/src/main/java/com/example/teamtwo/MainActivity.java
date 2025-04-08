package com.example.teamtwo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private int questionNo = 1; // default fallback
    private String serverIp = "172.22.208.1"; // your actual server IP
    private String serverPort = "9999";       // your actual port

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onChoiceClick(View view) {
        String choice = "";

        int id = view.getId();

        if (id == R.id.buttonChoiceA) {
            choice = "a";
        } else if (id == R.id.buttonChoiceB) {
            choice = "b";
        } else if (id == R.id.buttonChoiceC) {
            choice = "c";
        } else if (id == R.id.buttonChoiceD) {
            choice = "d";
        }

        // Get question text from TextView (e.g. "Q8: What is Java?")
        TextView questionTextView = findViewById(R.id.textViewQuestion);
        String text = questionTextView.getText().toString();

        // Extract number using regex
        Pattern pattern = Pattern.compile("Q(\\d+):");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            questionNo = Integer.parseInt(matcher.group(1));
        }

        sendChoiceToServer(choice, questionNo);
    }

    private void sendChoiceToServer(String choice, int questionNo) {
        new Thread(() -> {
            try {
                String fullUrl = "http://" + serverIp + ":" + serverPort +
                        "/androidMobileApp/select?questionNo=" + questionNo +
                        "&choice=" + choice;

                URL url = new URL(fullUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                // Optional: read and log server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                System.out.println("Server response: " + response.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void onNextQuestion(View view) {
        questionNo++; // or you can set it based on logic
        // Optionally update the TextView with the next question text
    }
}
