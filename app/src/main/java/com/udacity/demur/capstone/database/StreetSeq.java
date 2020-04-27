package com.udacity.demur.capstone.database;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

public class StreetSeq {
    @DocumentId
    private final String streetName;
    private final List<Segment> segments;

    public StreetSeq(String streetName, List<Segment> segments) {
        this.streetName = streetName;
        this.segments = segments;
    }

    public StreetSeq() {
        this(null, null);
    }

    public String getStreetName() {
        return streetName;
    }

    public List<Segment> getSegments() {
        return segments;
    }
}