package com.example.epcis_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import io.objectbox.Box;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Spinner eventTypeSpinner;
    private Spinner eventActionSpinner;
    private Spinner eventEPCS;
    private Spinner eventQuantities;
    private EditText idEditText;
    private EditText eventTimeEditText;
    private EditText recordTimeEditText;
    private EditText readPointEditText;
    private EditText businessLocationEditText;
    private EditText businessStepEditText;
    private EditText dispositionEditText;
    private EditText bizTransactionEditText;
    private EditText sourcesEditText;
    private EditText destinationsEditText;

    private EditText extensionsEditText;
    private ObjectBoxManager objectBoxManager;
    private Box<Event> eventBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objectBoxManager = ObjectBoxManager.getInstance(this);
        //objectBoxManager.initialize(this);
        eventBox = objectBoxManager.getEventBox();

        eventTypeSpinner = findViewById(R.id.spinner_event_type);
        eventActionSpinner = findViewById(R.id.spinner_event_action);
        idEditText = findViewById(R.id.edit_text_id);
        eventTimeEditText = findViewById(R.id.edit_text_event_time);
        recordTimeEditText = findViewById(R.id.edit_text_record_time);
        eventEPCS = findViewById(R.id.spinner_epcs);
        eventQuantities = findViewById(R.id.spinner_quantities);
        readPointEditText = findViewById(R.id.edit_text_read_point);
        businessLocationEditText = findViewById(R.id.edit_text_business_location);
        businessStepEditText = findViewById(R.id.edit_text_business_step);
        dispositionEditText = findViewById(R.id.edit_text_disposition);
        extensionsEditText = findViewById(R.id.edit_text_extensions);
        bizTransactionEditText = findViewById(R.id.edit_text_biz_transactions);
        sourcesEditText = findViewById(R.id.edit_text_sources);
        destinationsEditText = findViewById(R.id.edit_text_destinations);

        // Initialize the Generate ID button
        Button generateIdButton = findViewById(R.id.button_generate_id);

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
                finish(); // Optional: Call finish() to close the MainActivity and prevent it from being shown when the user presses the back button
            }
        });
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
                    Event event = createEventObject();
                    eventBox.put(event);
                    showToast("Event saved successfully!");
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
        String epcs = eventEPCS.getSelectedItem().toString();
        String quantities = eventQuantities.getSelectedItem().toString();
        String readPoint = readPointEditText.getText().toString();
        String businessLocation = businessLocationEditText.getText().toString();
        String businessStep = businessStepEditText.getText().toString();
        String bizTransaction = bizTransactionEditText.getText().toString();
        String sources = sourcesEditText.getText().toString();
        String destinations = destinationsEditText.getText().toString();
        String disposition = dispositionEditText.getText().toString();
        String extensions = extensionsEditText.getText().toString();
        // Retrieve other input fields accordingly

        // Generate the XML content using the retrieved input values
        String xmlContent = generateXMLContent(eventType, id, action,eventTime, recordTime, epcs, quantities, readPoint, businessLocation, businessStep, bizTransaction, sources, destinations, disposition, extensions);

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
                && !bizTransactionEditText.getText().toString().isEmpty()
                && !sourcesEditText.getText().toString().isEmpty()
                && !destinationsEditText.getText().toString().isEmpty()
                && !dispositionEditText.getText().toString().isEmpty()
                && !extensionsEditText.getText().toString().isEmpty();
    }
    /*@Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_generate_id) {
            transformToUUID();
        } else if (v.getId() == R.id.button_save) {
            // Handle save button click
            if (isInputValid()) {
                // All fields have valid input, proceed with saving
                Event event = createEventObject();
                eventBox.put(event);
                showToast("Event saved successfully!");
                Intent intent = new Intent(MainActivity.this, SuccessActivity.class);
                startActivity(intent);
            } else {
                // Display an error message or highlight the fields with invalid input
                Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
    private Event createEventObject() {
        Event event = new Event();
        event.setEventType(eventTypeSpinner.getSelectedItem().toString());
        event.setAction(eventActionSpinner.getSelectedItem().toString());
        // Set the ID to 0 for the case of inserting a new entity
        event.setId(0);
        event.setEventTime(eventTimeEditText.getText().toString());
        event.setRecordTime(recordTimeEditText.getText().toString());
        event.setEpcs(eventEPCS.getSelectedItem().toString());
        event.setEpcs(eventQuantities.getSelectedItem().toString());
        event.setReadPoint(readPointEditText.getText().toString());
        event.setBusinessLocation(businessLocationEditText.getText().toString());
        event.setBusinessStep(businessStepEditText.getText().toString());
        event.setBizTransactions(bizTransactionEditText.getText().toString());
        event.setSources(sourcesEditText.getText().toString());
        event.setDestinations(destinationsEditText.getText().toString());
        event.setDisposition(dispositionEditText.getText().toString());
        event.setExtensions(extensionsEditText.getText().toString());
        return event;
    }
    private String generateXMLContent(String eventType, String id, String action, String eventTime, String recordTime, String epcs, String quantities, String readPoint, String businessLocation, String businessStep,
                                      String bizTransaction, String sources, String destinations, String disposition, String extensions) {
        String xmlTemplate = "<epcisEvent><eventType>%s</eventType><id>%s</id><action>%s</action><eventTime>%s</eventTime><recordTime>%s</recordTime><epcs>%s</epcs><quantities>%s</quantities><readPoint>%s</readPoint><businessLocation>%s</businessLocation><businessStep>%s</businessStep><bizTransaction>%s</bizTransaction><sources>%s</sources><destinations>%s</destinations><disposition>%s</disposition><extensions>%s</extensions></epcisEvent>";
        return String.format(xmlTemplate, eventType, id, action, eventTime, recordTime, epcs, quantities, readPoint, businessLocation, businessStep, disposition, extensions);
    }
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
