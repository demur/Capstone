package com.udacity.demur.capstone.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.udacity.demur.capstone.MainActivity;

import java.util.Calendar;
import java.util.List;

@Entity(tableName = MainActivity.STREETS_TABLE_NAME)
public class Street {
    @PrimaryKey(autoGenerate = true)
    private final int id;
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

    @Ignore
    private Polyline poly;
    @Ignore
    private Boolean visible = false;
    @Ignore
    private LatLngBounds bnds;
    @Ignore
    private List<LatLng> points;
    @Ignore
    private int availableHours;

    @Ignore
    private Calendar parkingLimit;

    public Street(int id, int zone, String streetName, String limits, String side,
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

    @NonNull
    @Override
    public String toString() {
        return (streetName + " (" + side + ") between " + limits.replace(",", " and ")
                + " (section: " + section + ", segment:" + segment + ") in zone: " + zone);
    }

    @Dao
    public interface Store {
        @Insert
        void insert(Street street);

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " ORDER BY id")
        List<Street> all();

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " ORDER BY id")
        LiveData<List<Street>> allLive();

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE id = :id")
        Street getById(int id);

        @Query("SELECT id FROM " + MainActivity.STREETS_TABLE_NAME + "")
        List<Integer> getIds();

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE zone = :zoneId")
        List<Street> getByZone(int zoneId);

        @Query("SELECT id FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE zone = :zoneId")
        List<Integer> getIdsByZone(int zoneId);

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE parentId = :parentId")
        List<Street> getByParent(int parentId);

        @Query("SELECT id FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE parentId = :parentId")
        List<Integer> getIdsByParent(int parentId);

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE streetName = :streetName")
        List<Street> getByName(String streetName);

        @Query("SELECT id FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE streetName = :streetName")
        List<Integer> getIdsByName(String streetName);

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE streetName = :streetName AND zone = :zoneId")
        List<Street> getByNameAndZone(String streetName, int zoneId);

        @Query("SELECT id FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE streetName = :streetName AND zone = :zoneId")
        List<Integer> getIdsByNameAndZone(String streetName, int zoneId);

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE zone IN(:zonesIds) AND sweeping GLOB :sweepingPattern")
        List<Street> getByZoneAndSweeping(int[] zonesIds, String sweepingPattern);//LIKE

        @Query("SELECT id FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE zone IN(:zonesIds) AND sweeping GLOB :sweepingPattern")
        List<Integer> getIdsByZoneAndSweeping(int[] zonesIds, String sweepingPattern);//LIKE

        @Query("SELECT * FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE zone IN(:zonesIds) AND SUBSTR(sweeping,:index,1)=:value")
        List<Street> getByZoneAndSweeping(int[] zonesIds, int index, int value);

        @Query("SELECT id FROM " + MainActivity.STREETS_TABLE_NAME + " WHERE zone IN(:zonesIds) AND SUBSTR(sweeping,:index,1)=:value")
        List<Integer> getIdsByZoneAndSweeping(int[] zonesIds, int index, int value);
    }

    public int getId() {
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