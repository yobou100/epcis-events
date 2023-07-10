package com.example.epcis_events;

import android.content.Context;
import android.util.Log;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.BoxStoreBuilder;
import io.objectbox.android.Admin;
import io.objectbox.exception.FileCorruptException;

public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        BoxStoreBuilder db = MyObjectBox.builder()
                .name("DB")
                .androidContext(context.getApplicationContext());
        try {
            boxStore = db.build();
        } catch (FileCorruptException e) { // Demonstrate handling issues caused by devices with a broken file system
            Log.w(MainActivity.EVENT, "File corrupt, trying previous data snapshot...", e);
            // Retrying requires ObjectBox 2.7.1+
            db.usePreviousCommit();
            boxStore = db.build();
        }

        if (BuildConfig.DEBUG) {
            Log.d(MainActivity.EVENT, String.format("Using ObjectBox %s (%s)",
                    BoxStore.getVersion(), BoxStore.getVersionNative()));
            // Enable ObjectBox Admin on debug builds.
            // https://docs.objectbox.io/data-browser
            new Admin(boxStore).start(context.getApplicationContext());
        }
    }
    protected static void destroy(Context context){

        Box<Event> tagsBox = get().boxFor(Event.class);
        boxStore = tagsBox.getStore();
        boxStore.removeAllObjects();
        boxStore.close();
        System.out.println(boxStore.isClosed());
    }

    public static BoxStore get() { return boxStore; }
    public static BoxStore getInstance() { return boxStore; }

    private void deleteAllItems(){
        BoxStore db = ObjectBox.getInstance();
        Box<Event> tagBox = db.boxFor(Event.class);
        tagBox.removeAll();
    }
}

