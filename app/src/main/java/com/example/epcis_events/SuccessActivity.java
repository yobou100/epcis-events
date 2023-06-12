package com.example.epcis_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Button backButton = findViewById(R.id.button_back);

        // Set a click listener for the button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to go back
                finish();
                // Clear the input fields and go back to MainActivity
                Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        // Display the success message
        TextView messageTextView = findViewById(R.id.text_success_message);
        messageTextView.setText("The event has been saved successfully!");
    }
}