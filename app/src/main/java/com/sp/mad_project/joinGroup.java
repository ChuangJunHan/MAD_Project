package com.sp.mad_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class joinGroup extends AppCompatActivity {

    private Button joinWithKeyButton, joinWithQrButton;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        joinWithKeyButton = findViewById(R.id.joinWithKeyButton);
        joinWithQrButton = findViewById(R.id.joinWithQrButton);

        // Get the logged-in user from the intent
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (loggedInUser == null || loggedInUser.isEmpty()) {
            // Redirect to login if no user is logged in
            Intent intent = new Intent(joinGroup.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        joinWithKeyButton.setOnClickListener(v -> {
            // Pass loggedInUser to joinWithKey activity
            Intent intent = new Intent(joinGroup.this, joinWithKey.class);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });

        joinWithQrButton.setOnClickListener(v -> {
            // Pass loggedInUser while scanning QR code
            scanCode();
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(joinWithQr.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(joinGroup.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
        }
    });
}
