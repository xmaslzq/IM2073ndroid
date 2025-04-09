package com.example.teamtwo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private int questionNo = 1; // default fallback
    private String serverIp = "10.0.2.2"; // Use emulator IP for local server
    private String serverPort = "9999";   // Your actual port

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch the first question
        fetchQuestionFromServer(questionNo);
    }

    // Function to fetch question from server
    private void fetchQuestionFromServer(int questionNo) {
        new Thread(() -> {
            try {
                String urlString = "http://" + serverIp + ":" + serverPort +
                        "/androidMobileApp/display?questionNo=" + questionNo;

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());

                if (json.has("end")) {
                    runOnUiThread(() -> {
                        // Display "End of quiz" message
                        TextView questionView = findViewById(R.id.textViewQuestion);
                        questionView.setText("End of quiz.");

                        // Disable and hide the options and Next button
                        findViewById(R.id.buttonChoiceA).setVisibility(View.GONE);
                        findViewById(R.id.buttonChoiceB).setVisibility(View.GONE);
                        findViewById(R.id.buttonChoiceC).setVisibility(View.GONE);
                        findViewById(R.id.buttonChoiceD).setVisibility(View.GONE);
                        findViewById(R.id.buttonNext).setVisibility(View.GONE); // Optionally hide the Next button
                    });
                    return;
                }

                String questionText = json.getString("questionText");
                String optionA = json.getString("optionA");
                String optionB = json.getString("optionB");
                String optionC = json.getString("optionC");
                String optionD = json.getString("optionD");

                runOnUiThread(() -> {
                    // Update question and options
                    TextView questionView = findViewById(R.id.textViewQuestion);
                    questionView.setText("Q" + questionNo + ": " + questionText);

                    ((Button) findViewById(R.id.buttonChoiceA)).setText("A: " + optionA);
                    ((Button) findViewById(R.id.buttonChoiceB)).setText("B: " + optionB);
                    ((Button) findViewById(R.id.buttonChoiceC)).setText("C: " + optionC);
                    ((Button) findViewById(R.id.buttonChoiceD)).setText("D: " + optionD);

                    // Make sure options are visible when a new question is fetched
                    findViewById(R.id.buttonChoiceA).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonChoiceB).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonChoiceC).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonChoiceD).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonNext).setVisibility(View.VISIBLE);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Handle vote choice button clicks
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

        // Get question number from TextView (e.g. "Q8: What is Java?")
        TextView questionTextView = findViewById(R.id.textViewQuestion);
        String text = questionTextView.getText().toString();
        String questionNoStr = text.split(":")[0].replaceAll("[^0-9]", "");

        try {
            questionNo = Integer.parseInt(questionNoStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        sendChoiceToServer(choice, questionNo);
    }

    // Function to send the vote to the server
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

                // Optional: Log server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Show a toast on the main thread after sending vote
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Vote for question #" + questionNo + " recorded.", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Handle the Next Question button click
    public void onNextQuestion(View view) {
        questionNo++;
        fetchQuestionFromServer(questionNo);  // Fetch and display the next question
    }
}
