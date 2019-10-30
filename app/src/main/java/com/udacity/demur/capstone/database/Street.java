package com.udacity.demur.capstone.database;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.firestore.DocumentId;

import java.util.Calendar;
import java.util.List;

public class Street {
    @DocumentId
    private final String id;
    private final int zone;
    private final String streetName;
    private final String limits;
    private final String side;
    private final String coords;
    private final String bounds;
    private final int parentId;
    private final int section;
    private final int segment;
    private final String parentSide;
    private final String sweeping;

    private Polyline poly;
    private Boolean visible = false;
    private LatLngBounds bnds;
    private List<LatLng> points;
    private int availableHours;

    private Calendar parkingLimit;

    public Street(String id, int zone, String streetName, String limits, String side,
                  String coords, String bounds, int parentId, int section, int segment,
                  String parentSide, String sweeping) {
        this.id = id;
        this.zone = zone;
        this.streetName = streetName;
        this.limits = limits;
        this.side = side;
        this.coords = coords;
        this.bounds = bounds;
        this.parentId = parentId;
        this.section = section;
        this.segment = segment;
        this.parentSide = parentSide;
        this.sweeping = sweeping;
    }

    public Street() {
        this(null, -1, null, null, null, null, null, -1, -1, -1, null, null);
    }

    @NonNull
    public String toString() {
        return (streetName + " (" + side + ") between " + limits.replace(",", " and ")
                + " (section: " + section + ", segment:" + segment + ") in zone: " + zone);
    }

    public String getId() {
        return id;
    }

    public int getZone() {
        return zone;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getLimits() {
        return limits;
    }

    public String getSide() {
        return side;
    }

    public String getCoords() {
        return coords;
    }

    public String getBounds() {
        return bounds;
    }

    public int getParentId() {
        return parentId;
    }

    public int getSection() {
        return section;
    }

    public int getSegment() {
        return segment;
    }

    public String getParentSide() {
        return parentSide;
    }

    public String getSweeping() {
        return sweeping;
    }

    public Polyline getPoly() {
        return poly;
    }

    public void setPoly(Polyline poly) {
        this.poly = poly;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public LatLngBounds getBnds() {
        return bnds;
    }

    public void setBnds(LatLngBounds bnds) {
        this.bnds = bnds;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public int getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(int availableHours) {
        this.availableHours = availableHours;
    }

    public Calendar getParkingLimit() {
        return parkingLimit;
    }

    public void setParkingLimit(Calendar parkingLimit) {
        this.parkingLimit = parkingLimit;
    }
}