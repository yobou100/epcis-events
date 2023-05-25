package com.example.epcis_events;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;


@Entity
public class Event {
    @Id
    public long id;

    private String eventType;
    private String action;
    private String eventTime;
    private String recordTime;
    private String readPoint;
    private String businessLocation;
    private String businessStep;
    private String disposition;
    private String extensions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Getter and setter methods for eventType
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    // Getter and setter methods for action
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    // Getter and setter methods for eventTime
    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    // Getter and setter methods for recordTime
    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    // Getter and setter methods for readPoint
    public String getReadPoint() {
        return readPoint;
    }

    public void setReadPoint(String readPoint) {
        this.readPoint = readPoint;
    }

    // Getter and setter methods for businessLocation
    public String getBusinessLocation() {
        return businessLocation;
    }

    public void setBusinessLocation(String businessLocation) {
        this.businessLocation = businessLocation;
    }

    // Getter and setter methods for businessStep
    public String getBusinessStep() {
        return businessStep;
    }

    public void setBusinessStep(String businessStep) {
        this.businessStep = businessStep;
    }

    // Getter and setter methods for disposition
    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    // Getter and setter methods for extensions
    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }
}
