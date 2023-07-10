package com.example.epcis_events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.callback.Callback;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import static com.example.epcis_events.ObjectBox.get;

public class MainActivity extends AppCompatActivity implements Callback {
    private EditText endpointEditText;

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
    public static final String EVENT = "ObjectBoxExample";
    private void showDateTimePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String selectedDateTime = dateFormat.format(calendar.getTime());

                        editText.setText(selectedDateTime);
                    }
                }, hour, minute, DateFormat.is24HourFormat(MainActivity.this));

                timePickerDialog.show();
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectBox.init(this);
        setContentView(R.layout.activity_main);

        objectBoxManager = ObjectBoxManager.getInstance(this);
        eventBox = objectBoxManager.getEventBox();
        endpointEditText = findViewById(R.id.edit_text_endpoint);
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
                finish();// Optional: Call finish() to close the MainActivity and prevent it from being shown when the user presses the back button
                Box<Event> eventBox = get().boxFor(Event.class);
                BoxStore boxStore = eventBox.getStore();
                boxStore.removeAllObjects();
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
                    String endpoint = String.valueOf(findViewById(R.id.edit_text_endpoint));
                    // All fields have valid input, proceed with saving
                    Event event = createEventObject();
                    String eventContent = String.valueOf(event);
                    eventBox.put(event);
                    sendEventToEndpoint(endpoint,eventContent);
                    Intent intent = new Intent(MainActivity.this, SuccessActivity.class);
                    startActivity(intent);
                } else {
                    // Display an error message or highlight the fields with invalid input
                    Toast.makeText(MainActivity.this, "Please fill in all the mandatory fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        eventTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog(eventTimeEditText);
            }
        });
        recordTimeEditText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){showDateTimePickerDialog(recordTimeEditText);}
        });
    }
    //method for adding another epc
    public void onAddAnotherClicked(View view) {
        LinearLayout layout = findViewById(R.id.layout_epcs);

        Spinner newSpinner = new Spinner(this);
        newSpinner.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newSpinner.setPrompt(getString(R.string.event_epc_prompt));
        //newSpinner.setTextSize(16);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.epcs,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(adapter);

        layout.addView(newSpinner);
    }
    //method for adding another quantity
    public void onAddAnotherClicked_quantity(View view) {
        LinearLayout layout = findViewById(R.id.layout_quantity);

        Spinner newSpinner = new Spinner(this);
        newSpinner.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newSpinner.setPrompt(getString(R.string.event_quantity_prompt));
        //newSpinner.setTextSize(16);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.quantities,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(adapter);

        layout.addView(newSpinner);
    }
    //method for adding another Biz transaction,sources ...
    public void onAddAnotherClicked_text(View view) {
        LinearLayout layout = findViewById(R.id.layout_text);
        LinearLayout layout2 = findViewById(R.id.layout_text2);
        LinearLayout layout3 = findViewById(R.id.layout_text3);
        LinearLayout layout4 = findViewById(R.id.layout_text4);

        EditText editText = createNewEditText();

        layout.addView(editText);

        EditText editText2 = createNewEditText();

        layout2.addView(editText2);

        EditText editText3 = createNewEditText();

        layout3.addView(editText3);

        EditText editText4 = createNewEditText();

        layout4.addView(editText4);
    }

    private EditText createNewEditText() {
        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setHint("Enter text");
        editText.setTextSize(16);
        return editText;
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
    private void sendEventToEndpoint(String endpoint, String xmlContent) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("text/xml"), xmlContent);
        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Event sent successfully to the endpoint.");
                        }
                    });
                } else {
                    final int responseCode = response.code();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Failed to send event to the endpoint. Server returned: " + responseCode);
                        }
                    });
                }
            }
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Failed to send event to the endpoint.");
                    }
                });
            }
        });
    }
    private void generateEPCISEvent() {
        String endpoint = endpointEditText.getText().toString();
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
        sendEventToEndpoint(endpoint, xmlContent);
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
