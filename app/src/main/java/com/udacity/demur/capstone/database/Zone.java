package com.udacity.demur.capstone.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLngBounds;
import com.udacity.demur.capstone.MainActivity;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = MainActivity.ZONES_TABLE_NAME)
public class Zone {
    @PrimaryKey(autoGenerate = true)
    final int id;
    private final String name;
    private final String desc;
    private final String bounds;
    private final String activeHours;
    private final int alienLimit;

    @Ignore
    private LatLngBounds bnds;
    @Ignore
    private List<Integer> streetList = new ArrayList<>();

    public Zone(int id, String name, String desc, String bounds, String activeHours, int alienLimit) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.bounds = bounds;
        this.activeHours = activeHours;
        this.alienLimit = alienLimit;
    }

    @NonNull
    @Override
    public String toString() {
        return (name + ". " + desc);
    }

    @Dao
    public interface Store {
        @Insert
        void insert(Zone zone);

        @Query("SELECT * FROM " + MainActivity.ZONES_TABLE_NAME + " ORDER BY id")
        List<Zone> all();

        @Query("SELECT * FROM " + MainActivity.ZONES_TABLE_NAME + " ORDER BY id")
        LiveData<List<Zone>> allLive();

        @Query("SELECT * FROM " + MainActivity.ZONES_TABLE_NAME + " WHERE id = :id")
        Zone getById(int id);

        @Query("SELECT id FROM " + MainActivity.ZONES_TABLE_NAME + "")
        List<Integer> getIds();

        @Query("SELECT * FROM " + MainActivity.ZONES_TABLE_NAME + " WHERE activeHours GLOB :pattern")
        List<Zone> getByPattern(String pattern);//LIKE

        @Query("SELECT id FROM " + MainActivity.ZONES_TABLE_NAME + " WHERE activeHours GLOB :pattern")
        List<Integer> getIdsByPattern(String pattern);//LIKE

        @Query("SELECT * FROM " + MainActivity.ZONES_TABLE_NAME + " WHERE SUBSTR(activeHours,:index,1)=:value")
        List<Zone> getByPattern(int index, int value);

        @Query("SELECT id FROM " + MainActivity.ZONES_TABLE_NAME + " WHERE SUBSTR(activeHours,:index,1)=:value")
        List<Integer> getIdsByPattern(int index, int value);
    }

    public int getId() {
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