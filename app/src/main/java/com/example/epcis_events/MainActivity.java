package com.example.epcis_events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner eventTypeSpinner;
    private Spinner eventActionSpinner;
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

        eventTypeSpinner = findViewById(R.id.spinner_event_type);
        eventActionSpinner = findViewById(R.id.spinner_event_action);
        idEditText = findViewById(R.id.edit_text_id);
        eventTimeEditText = findViewById(R.id.edit_text_event_time);
        recordTimeEditText = findViewById(R.id.edit_text_record_time);
        readPointEditText = findViewById(R.id.edit_text_read_point);
        businessLocationEditText = findViewById(R.id.edit_text_business_location);
        businessStepEditText = findViewById(R.id.edit_text_business_step);
        dispositionEditText = findViewById(R.id.edit_text_disposition);
        extensionsEditText = findViewById(R.id.edit_text_extensions);

        // Initialize the Generate ID button
        Button generateIdButton = findViewById(R.id.button_generate_id);
        generateIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transformToUUID();
            }
        });
        Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    // All fields have valid input, proceed with saving
                    Intent intent = new Intent(MainActivity.this, SuccessActivity.class);
                    startActivity(intent);
                } else {
                    // Display an error message or highlight the fields with invalid input
                    Toast.makeText(MainActivity.this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void transformToUUID() {
        // Retrieve the ID input from the EditText
        String id = idEditText.getText().toString();
        // Check if the ID input is empty
        if (!id.isEmpty()) {
            try {
                // Parse the ID input as an integer
                int parsedId = Integer.parseInt(id);
                // Generate a UUID based on the parsed integer
                UUID uuid = new UUID(parsedId, 0);
                String uuidString = uuid.toString();
                idEditText.setText(uuidString);
            } catch (NumberFormatException e) {
                showToast("Invalid ID format. Please enter a valid integer value.");
            }
        } else {
            showToast("ID field is empty. Please enter a value before transforming.");
        }
    }
    private void generateEPCISEvent() {
        // Retrieve user input from EditText fields
        String eventType = eventTypeSpinner.getSelectedItem().toString();
        String action = eventActionSpinner.getSelectedItem().toString();
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
        String xmlContent = generateXMLContent(eventType, id, action,eventTime, recordTime, readPoint, businessLocation, businessStep, disposition, extensions);

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
    private boolean isInputValid() {
        // Check if all fields have non-empty input
        return !eventTypeSpinner.getSelectedItem().toString().isEmpty()
                && !eventActionSpinner.getSelectedItem().toString().isEmpty()
                && !idEditText.getText().toString().isEmpty()
                && !eventTimeEditText.getText().toString().isEmpty()
                && !recordTimeEditText.getText().toString().isEmpty()
                && !readPointEditText.getText().toString().isEmpty()
                && !businessLocationEditText.getText().toString().isEmpty()
                && !businessStepEditText.getText().toString().isEmpty()
                && !dispositionEditText.getText().toString().isEmpty()
                && !extensionsEditText.getText().toString().isEmpty();
    }
    @Override
    public void onClick(View v) {
        Context context;
        //Event event = new Event();
        if (v.getId() == R.id.button_generate_id) {
            // Handle generate ID button click
        } else if (v.getId() == R.id.button_save) {
            // Handle save button click
            if (isInputValid()) {
                // All fields have valid input, proceed with saving
                /* ObjectBoxManager objectBoxManager = ObjectBoxManager.getInstance(context);

                // Get the Event Box
                Box<Event> eventBox = objectBoxManager.getEventBox();

                // Create a new event object
                event.setEventType(eventTypeSpinner.getSelectedItem().toString());
                event.setAction(eventActionSpinner.getSelectedItem().toString());
                event.setEventTime(eventTimeEditText.getText().toString());
                event.setRecordTime(recordTimeEditText.getText().toString());
                event.setReadPoint(readPointEditText.getText().toString());
                event.setBusinessLocation(businessLocationEditText.getText().toString());
                event.setBusinessStep(businessStepEditText.getText().toString());
                event.setDisposition(dispositionEditText.getText().toString());
                event.setExtensions(extensionsEditText.getText().toString());
                // Save the event to ObjectBox
                eventBox.put(event);*/
                Intent intent = new Intent(MainActivity.this, SuccessActivity.class);
                startActivity(intent);
            } else {
                // Display an error message or highlight the fields with invalid input
                Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String generateXMLContent(String eventType, String id, String action, String eventTime, String recordTime, String readPoint, String businessLocation, String businessStep,
                                      String disposition, String extensions) {
        String xmlTemplate = "<epcisEvent><eventType>%s</eventType><id>%s</id><action>%s</action><eventTime>%s</eventTime><recordTime>%s</recordTime><readPoint>%s</readPoint><businessLocation>%s</businessLocation><businessStep>%s</businessStep><disposition>%s</disposition><extensions>%s</extensions></epcisEvent>";
        return String.format(xmlTemplate, eventType, id, action, eventTime, recordTime, readPoint, businessLocation, businessStep, disposition, extensions);
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
