package com.udacity.demur.capstone.database;

import com.google.firebase.firestore.DocumentId;

import java.util.HashMap;

public class SweepingPatterns {
    @DocumentId
    private final String name;
    private final HashMap<String, String> map;

    public SweepingPatterns(String name, HashMap<String, String> map) {
        this.name = name;
        this.map = map;
    }

    public SweepingPatterns() {
        this(null, null);
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getMap() {
        return map;
    }
}