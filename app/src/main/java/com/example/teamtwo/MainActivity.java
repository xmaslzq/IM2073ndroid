package com.example.teamtwo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private int questionNo = 1;
    private String serverIp = "10.0.2.2";
    private String serverPort = "9999";

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private int timeRemaining = 0;

    private TextView timerView; // ✨ For countdown display

    private Handler questionPollingHandler = new Handler();
    private Runnable questionPollingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerView = findViewById(R.id.textViewTimer);
        fetchQuestionFromServer(questionNo);  // Initial question fetch

        // Start polling the server for the current question number
        startQuestionPolling();
    }

    private void startQuestionPolling() {
        questionPollingRunnable = new Runnable() {
            @Override
            public void run() {
                fetchCurrentQuestionNo();  // Poll the server for the latest question number
                questionPollingHandler.postDelayed(this, 5000);  // Poll every 5 seconds
            }
        };
        questionPollingHandler.post(questionPollingRunnable);  // Start polling
    }

    private void fetchCurrentQuestionNo() {
        new Thread(() -> {
            try {
                // Poll the server to get the current question number
                String urlString = "http://" + serverIp + ":" + serverPort + "/androidMobileApp/questionState";
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

                // Parse the response to get the current question number
                JSONObject json = new JSONObject(response.toString());
                int serverQuestionNo = json.getInt("currentQuestionNo");

                runOnUiThread(() -> {
                    // Sync the question number on the app side
                    if (serverQuestionNo != questionNo) {
                        questionNo = serverQuestionNo;
                        fetchQuestionFromServer(questionNo);  // Fetch the corresponding question
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

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
                        TextView questionView = findViewById(R.id.textViewQuestion);
                        questionView.setText("End of quiz.");

                        findViewById(R.id.buttonChoiceA).setVisibility(View.GONE);
                        findViewById(R.id.buttonChoiceB).setVisibility(View.GONE);
                        findViewById(R.id.buttonChoiceC).setVisibility(View.GONE);
                        findViewById(R.id.buttonChoiceD).setVisibility(View.GONE);
                        findViewById(R.id.buttonNext).setVisibility(View.GONE);
                        timerView.setVisibility(View.GONE); // Hide timer
                    });
                    return;
                }

                String questionText = json.getString("questionText");
                String optionA = json.getString("optionA");
                String optionB = json.getString("optionB");
                String optionC = json.getString("optionC");
                String optionD = json.getString("optionD");
                timeRemaining = json.getInt("timeRemaining"); // ✨ Get timer from server

                runOnUiThread(() -> {
                    TextView questionView = findViewById(R.id.textViewQuestion);
                    questionView.setText("Q" + questionNo + ": " + questionText);

                    ((Button) findViewById(R.id.buttonChoiceA)).setText("A: " + optionA);
                    ((Button) findViewById(R.id.buttonChoiceB)).setText("B: " + optionB);
                    ((Button) findViewById(R.id.buttonChoiceC)).setText("C: " + optionC);
                    ((Button) findViewById(R.id.buttonChoiceD)).setText("D: " + optionD);

                    findViewById(R.id.buttonChoiceA).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonChoiceB).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonChoiceC).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonChoiceD).setVisibility(View.VISIBLE);
                    findViewById(R.id.buttonNext).setVisibility(View.VISIBLE);
                    timerView.setVisibility(View.VISIBLE);

                    startTimerCountdown(); // ✨ Start countdown
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ✨ Countdown timer logic
    private void startTimerCountdown() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timerView.setText("Time left: " + timeRemaining + "s");
                    timeRemaining--;
                    timerHandler.postDelayed(this, 1000);
                } else {
                    timerView.setText("Time's up!");
                    disableChoices();
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    // ✨ Disable voting after time is up
    private void disableChoices() {
        findViewById(R.id.buttonChoiceA).setEnabled(false);
        findViewById(R.id.buttonChoiceB).setEnabled(false);
        findViewById(R.id.buttonChoiceC).setEnabled(false);
        findViewById(R.id.buttonChoiceD).setEnabled(false);
    }

    public void onChoiceClick(View view) {
        String choice = "";

        int id = view.getId();
        if (id == R.id.buttonChoiceA) choice = "a";
        else if (id == R.id.buttonChoiceB) choice = "b";
        else if (id == R.id.buttonChoiceC) choice = "c";
        else if (id == R.id.buttonChoiceD) choice = "d";

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

    private void sendChoiceToServer(String choice, int questionNo) {
        new Thread(() -> {
            try {
                String fullUrl = "http://" + serverIp + ":" + serverPort +
                        "/androidMobileApp/select?questionNo=" + questionNo +
                        "&choice=" + choice;

                URL url = new URL(fullUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Vote for question #" + questionNo + " recorded.", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void onNextQuestion(View view) {
        questionNo++;
        fetchQuestionFromServer(questionNo);  // Fetch the next question from the server
        updateQuestionStateOnServer();  // Notify the server about the updated question number
    }

    private void updateQuestionStateOnServer() {
        new Thread(() -> {
            try {
                String urlString = "http://" + serverIp + ":" + serverPort + "/androidMobileApp/questionState";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                // Send the new question number to the server
                String data = "currentQuestionNo=" + questionNo;
                conn.getOutputStream().write(data.getBytes());

                // Read the response to ensure it's processed
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Handle any response (optional)
                runOnUiThread(() -> {
                    // Optional: You can show a Toast or any feedback here
                    Toast.makeText(MainActivity.this, "Question number updated to " + questionNo, Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
