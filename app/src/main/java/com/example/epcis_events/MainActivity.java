package com.example.epcis_events;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText eventTypeEditText;
    private EditText idEditText;
    private EditText eventTimeEditText;
    private EditText recordTimeEditText;
    private EditText readPointEditText;
    private EditText businessLocationEditText;
    private EditText businessStepEditText;
    private EditText dispositionEditText;
    private EditText extensionsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventTypeEditText = findViewById(R.id.edit_text_event_type);
        idEditText = findViewById(R.id.edit_text_id);
        eventTimeEditText = findViewById(R.id.edit_text_event_time);
        recordTimeEditText = findViewById(R.id.edit_text_record_time);
        readPointEditText = findViewById(R.id.edit_text_read_point);
        businessLocationEditText = findViewById(R.id.edit_text_business_location);
        businessStepEditText = findViewById(R.id.edit_text_business_location);
        dispositionEditText = findViewById(R.id.edit_text_business_location);
        extensionsEditText = findViewById(R.id.edit_text_business_location);

        Button generateButton = findViewById(R.id.button_generate);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateEPCISEvent();
            }
        });
        // Initialize the Generate ID button
        Button generateIdButton = findViewById(R.id.button_generate_id);
        generateIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transformToUUID();
            }
        });
    }

    private void transformToUUID() {
        // Retrieve the ID input from the EditText
        String id = idEditText.getText().toString();

        // Transform the ID input to a UUID format
        try {
            UUID uuid = UUID.fromString(id);
            String uuidString = uuid.toString();
            idEditText.setText(uuidString);
        } catch (IllegalArgumentException e) {
            showToast("Invalid ID format. Please enter a valid Integer value.");
        }
    }

    private void generateEPCISEvent() {
        // Retrieve user input from EditText fields
        String eventType = eventTypeEditText.getText().toString();
        String id = idEditText.getText().toString();
        String eventTime = eventTimeEditText.getText().toString();
        String recordTime = recordTimeEditText.getText().toString();
        String readPoint = readPointEditText.getText().toString();
        String businessLocation = businessLocationEditText.getText().toString();
        String businessStep = businessStepEditText.getText().toString();
        String disposition = dispositionEditText.getText().toString();
        String extensions = extensionsEditText.getText().toString();
        // Retrieve other input fields accordingly

        // Generate the XML content using the retrieved input values
        String xmlContent = generateXMLContent(eventType, id, eventTime, recordTime, readPoint, businessLocation, businessStep, disposition, extensions);

        // Save the XML content to a file (e.g., locally on the device's storage)
        try {
            File file = new File(getFilesDir(), "epcis_event.xml");
            FileWriter writer = new FileWriter(file);
            writer.write(xmlContent);
            writer.close();
            showToast("XML file generated and saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Error occurred while saving the XML file.");
        }
    }

    private String generateXMLContent(String eventType, String id, String eventTime, String recordTime, String readPoint, String businessLocation, String businessStep,
                                      String disposition, String extensions) {
        String xmlTemplate = "<epcisEvent><eventType>%s</eventType><id>%s</id><eventTime>%s</eventTime><recordTime>%s</recordTime><readPoint>%s</readPoint><businessLocation>%s</businessLocation><businessStep>%s</businessStep><disposition>%s</disposition><extensions>%s</extensions></epcisEvent>";
        return String.format(xmlTemplate, eventType, id, eventTime, recordTime, readPoint, businessLocation, businessStep, disposition, extensions);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
