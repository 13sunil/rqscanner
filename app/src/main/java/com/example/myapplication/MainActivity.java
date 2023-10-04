package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ImageView qrCodeImage;
    private TextView qrCodeContent;
    private TextView ssidText;
    private TextView passwordText;
    String ssid = null;
    String password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrCodeImage = findViewById(R.id.qrCodeImage);
        qrCodeContent = findViewById(R.id.qrcodeContent);
        ssidText = findViewById(R.id.ssidText);
        passwordText = findViewById(R.id.passwordText);

        // Set an OnClickListener on the QR code image
        qrCodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize the QR code scanner
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setOrientationLocked(true);
                integrator.setPrompt("Scan a QR Code");
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
            }
        });
    }

    // Handle the result of the QR code scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Display the scanned QR code content in the TextView
                String scannedContent = result.getContents();
                qrCodeContent.setText("Scanned QR Code Content: " + scannedContent);
                Log.d("ScannedContent", scannedContent);

                // Attempt to directly extract SSID and password based on position
                int ssidStart = scannedContent.indexOf("S:") + 2;
                int ssidEnd = scannedContent.indexOf(";", ssidStart);
                int passwordStart = scannedContent.indexOf("P:") + 2;
                int passwordEnd = scannedContent.indexOf(";", passwordStart);

                if (ssidStart >= 2 && ssidEnd > ssidStart && passwordStart >= 2 && passwordEnd > passwordStart) {
                    ssid = scannedContent.substring(ssidStart, ssidEnd);
                    password = scannedContent.substring(passwordStart, passwordEnd);
                    Log.d("SSID", ssid);
                    Log.d("Password", password);

                    // Update the TextViews with SSID and password
                    ssidText.setText("SSID: " + ssid);
                    passwordText.setText("Password: " + password);

                    // Hide the QR code image to focus on displaying SSID and password
                    qrCodeImage.setVisibility(View.GONE);

                    // Now, you can use them to connect to the Wi-Fi network programmatically.
                } else {
                    Log.d("SSID", "SSID is missing or malformed");
                    Log.d("Password", "Password is missing or malformed");
                    // Handle the case where SSID or password is missing or malformed
                    // You might want to display an error message to the user or take appropriate action.
                }
            } else {
                qrCodeContent.setText("No QR code data found.");
            }
        }
    }
}
