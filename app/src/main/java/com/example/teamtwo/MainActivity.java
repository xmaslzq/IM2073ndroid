package com.example.teamtwo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.net.HttpURLConnection;
import java.net.URL;
import com.example.teamtwo.R;

public class MainActivity extends AppCompatActivity {

    // Use runtime string for the server URL
    private String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the server URL dynamically at runtime
        String serverIp = "172.26.48.1"; // replace with your actual server IP
        String serverPort = "9999"; // replace with your actual server port
        int questionNo = 8; // You can make this dynamic later if needed
        serverUrl = "http://" + serverIp + ":" + serverPort + "/androidMobileApp/select?questionNo=" + questionNo + "&choice=";
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

        sendChoiceToServer(choice);
    }

    private void sendChoiceToServer(String choice) {
        new Thread(() -> {
            try {
                URL url = new URL(serverUrl + choice);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode(); // trigger the request
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
