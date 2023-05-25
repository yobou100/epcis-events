package com.example.epcis_events;

import android.content.Context;
import androidx.lifecycle.LiveData;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import io.objectbox.android.ObjectBoxLiveData;

import java.util.List;

public class ObjectBoxManager {
    private static ObjectBoxManager instance;
    private BoxStore boxStore;
    private Box<Event> eventBox;
    private ObjectBoxLiveData<Event> eventLiveData;
    private Context context;

    private ObjectBoxManager(Context context) {
        this.context = context.getApplicationContext();
        boxStore = MyObjectBox.builder().androidContext(this.context).build();
        eventBox = boxStore.boxFor(Event.class);
        eventLiveData = new ObjectBoxLiveData<>(eventBox.query().build());
    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new ObjectBoxManager(context);
        }
    }

    public static ObjectBoxManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ObjectBoxManager is not initialized.");
        }
        return instance;
    }

    public Box<Event> getEventBox() {
        return eventBox;
    }

    public void storeEvent(Event event) {
        eventBox.put(event);
    }

    public void removeEvent(Event event) {
        eventBox.remove(event);
    }

    public LiveData<List<Event>> getEventLiveData() {
        return eventLiveData;
    }

    public void enableObjectBrowser() {
        new AndroidObjectBrowser(boxStore).start(context);
    }
}



