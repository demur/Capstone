package com.udacity.demur.capstone.database;

public class Segment {
    private final String bounds;
    private final String coords;
    private final String limits;
    private final String side;
    private final String sweepingId;
    private final int zone;

    public Segment(String bounds, String coords, String limits, String side, String sweepingId, int zone) {
        this.bounds = bounds;
        this.coords = coords;
        this.limits = limits;
        this.side = side;
        this.sweepingId = sweepingId;
        this.zone = zone;
    }

    public Segment() {
        this(null, null, null, null, null, -1);
    }

    public String getBounds() {
        return bounds;
    }

    public String getCoords() {
        return coords;
    }

    public String getLimits() {
        return limits;
    }

    public String getSide() {
        return side;
    }

    public String getSweepingId() {
        return sweepingId;
    }

    public int getZone() {
        return zone;
    }
}