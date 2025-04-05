package com.example.clickerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView selectedOptionText;
    private Button buttonOption1, buttonOption2, buttonOption3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        selectedOptionText = findViewById(R.id.selectedOptionText);
        buttonOption1 = findViewById(R.id.buttonOption1);
        buttonOption2 = findViewById(R.id.buttonOption2);
        buttonOption3 = findViewById(R.id.buttonOption3);

        // Set click listeners for the buttons
        buttonOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOptionText.setText("You chose: Option 1");
                displayInHTML("You chose: Option 1");
            }
        });

        buttonOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOptionText.setText("You chose: Option 2");
                displayInHTML("You chose: Option 2");
            }
        });

        buttonOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOptionText.setText("You chose: Option 3");
                displayInHTML("You chose: Option 3");
            }
        });
    }

    // Method to simulate showing the result in HTML format
    private void displayInHTML(String result) {
        // Here, you'd typically use a WebView or pass data to an actual HTML file in your assets.
        // This is just a simulation.

        // In a real-world scenario, you could use a WebView to show HTML content
        // or load HTML content dynamically.

        String htmlContent = "<html><body><h2>" + result + "</h2></body></html>";

        // Show the HTML content using WebView
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.loadData(htmlContent, "text/html", "UTF-8");
    }
}