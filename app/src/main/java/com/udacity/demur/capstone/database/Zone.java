package com.udacity.demur.capstone.database;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    @DocumentId
    private final String id;
    private final String name;
    private final String desc;
    private final String bounds;
    private final String activeHours;
    private final int alienLimit;

    private LatLngBounds bnds;
    private List<Integer> streetList = new ArrayList<>();

    public Zone(String id, String name, String desc, String bounds, String activeHours, int alienLimit) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.bounds = bounds;
        this.activeHours = activeHours;
        this.alienLimit = alienLimit;
    }

    public Zone() {
        this(null, null, null, null, null, -1);
    }

    @NonNull
    public String toString() {
        return (name + ". " + desc);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getBounds() {
        return bounds;
    }

    public String getActiveHours() {
        return activeHours;
    }

    public int getAlienLimit() {
        return alienLimit;
    }

    public LatLngBounds getBnds() {
        return bnds;
    }

    public void setBnds(LatLngBounds bnds) {
        this.bnds = bnds;
    }

    public List<Integer> getStreetList() {
        return streetList;
    }

    public void setStreetList(List<Integer> streetList) {
        this.streetList = streetList;
    }

    public void streetListAdd(int streetId) {
        this.streetList.add(streetId);
    }

    public Boolean streetListContains(int streetId) {
        return this.streetList.contains(streetId);
    }
}