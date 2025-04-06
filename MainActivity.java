
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String serverIp = "YOUR_PC_IP"; // change this
    int serverPort = 8080; // your Tomcat port

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnA = findViewById(R.id.buttonA);
        Button btnB = findViewById(R.id.buttonB);
        Button btnC = findViewById(R.id.buttonC);

        btnA.setOnClickListener(v -> sendChoice("a"));
        btnB.setOnClickListener(v -> sendChoice("b"));
        btnC.setOnClickListener(v -> sendChoice("c"));
    }

    private void sendChoice(String choice) {
        new Thread(() -> {
            try {
                URL url = new URL("http://" + serverIp + ":" + serverPort + "/clicker/select?choice=" + choice);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.getResponseCode(); // Trigger the request
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
