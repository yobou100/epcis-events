package com.example.epcis_events;

import android.content.Context;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class ObjectBoxManager {
    private static ObjectBoxManager instance;
    private BoxStore boxStore;

    private ObjectBoxManager(Context context) {
        // Private constructor to enforce singleton pattern
        boxStore = MyObjectBox.builder().androidContext(context.getApplicationContext()).build();
    }

    public static synchronized ObjectBoxManager getInstance(Context context) {
        if (instance == null) {
            instance = new ObjectBoxManager(context);
        }
        return instance;
    }

    public Box<Event> getEventBox() {
        return boxStore.boxFor(Event.class);
    }

    // Other methods and fields
}



