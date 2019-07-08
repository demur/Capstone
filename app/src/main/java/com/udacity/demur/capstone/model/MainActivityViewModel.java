package com.udacity.demur.capstone.model;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.annotation.NonNull;
import android.util.SparseArray;

import com.google.android.gms.maps.model.Marker;
import com.udacity.demur.capstone.BR;
import com.udacity.demur.capstone.database.ParkingDatabase;
import com.udacity.demur.capstone.database.Street;
import com.udacity.demur.capstone.database.Zone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements Observable {
    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private final LiveData<List<Street>> allStreets;
    private final LiveData<List<Zone>> allZones;
    private List<Street> streets = new ArrayList<>();
    private SparseArray<Zone> zones = new SparseArray<>();
    private List<Integer> detectedZones = new ArrayList<>();
    private List<Marker> zoneLabels = new ArrayList<>();
    private Marker parkingMarker;
    private int permitZone = -1;
    private int parkingDuration;
    private int parkingMarkerPolylineIndex;
    private Boolean hasBeenFailedRenderAttempt;
    private Boolean isMapRefreshNeeded;
    private Boolean isMarkerSetupMode;
    private Boolean isActualTimeUsed;
    private Boolean isDataPrepared;
    private Boolean zonesOverCity;
    private Date customDate;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        ParkingDatabase mDb = ParkingDatabase.get(this.getApplication());
        allStreets = mDb.streetStore().allLive();
        allZones = mDb.zoneStore().allLive();
    }

    public LiveData<List<Street>> getAllStreets() {
        return allStreets;
    }

    public LiveData<List<Zone>> getAllZones() {
        return allZones;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public void setStreets(List<Street> streets) {
        this.streets = streets;
    }

    public SparseArray<Zone> getZones() {
        return zones;
    }

    public void setZones(SparseArray<Zone> zones) {
        this.zones = zones;
    }

    public List<Integer> getDetectedZones() {
        return detectedZones;
    }

    public void setDetectedZones(List<Integer> detectedZones) {
        this.detectedZones = detectedZones;
    }

    public int getPermitZone() {
        return permitZone;
    }

    public void setPermitZone(int permitZone) {
        this.permitZone = permitZone;
    }

    public int getParkingDuration() {
        return parkingDuration;
    }

    public void setParkingDuration(int parkingDuration) {
        this.parkingDuration = parkingDuration;
    }

    public int getParkingMarkerPolylineIndex() {
        return parkingMarkerPolylineIndex;
    }

    public void setParkingMarkerPolylineIndex(int parkingMarkerPolylineIndex) {
        this.parkingMarkerPolylineIndex = parkingMarkerPolylineIndex;
    }

    public Boolean getHasBeenFailedRenderAttempt() {
        return hasBeenFailedRenderAttempt;
    }

    public void setHasBeenFailedRenderAttempt(Boolean hasBeenFailedRenderAttempt) {
        this.hasBeenFailedRenderAttempt = hasBeenFailedRenderAttempt;
    }

    public Boolean getMapRefreshNeeded() {
        return isMapRefreshNeeded;
    }

    public void setMapRefreshNeeded(Boolean mapRefreshNeeded) {
        isMapRefreshNeeded = mapRefreshNeeded;
    }

    public Boolean getMarkerSetupMode() {
        return isMarkerSetupMode;
    }

    public void setMarkerSetupMode(Boolean markerSetupMode) {
        isMarkerSetupMode = markerSetupMode;
    }

    public Boolean getActualTimeUsed() {
        return isActualTimeUsed;
    }

    public void setActualTimeUsed(Boolean actualTimeUsed) {
        isActualTimeUsed = actualTimeUsed;
    }

    public Boolean getDataPrepared() {
        return isDataPrepared;
    }

    public void setDataPrepared(Boolean dataPrepared) {
        isDataPrepared = dataPrepared;
    }

    public Boolean getZonesOverCity() {
        return zonesOverCity;
    }

    public void setZonesOverCity(Boolean zonesOverCity) {
        this.zonesOverCity = zonesOverCity;
    }

    public Date getCustomDate() {
        return customDate;
    }

    public void setCustomDate(Date customDate) {
        this.customDate = customDate;
    }

    public List<Marker> getZoneLabels() {
        return zoneLabels;
    }

    public void setZoneLabels(List<Marker> zoneLabels) {
        this.zoneLabels = zoneLabels;
    }

    @Bindable
    public Marker getParkingMarker() {
        return parkingMarker;
    }

    public void setParkingMarker(Marker parkingMarker) {
        this.parkingMarker = parkingMarker;
        notifyPropertyChanged(BR.parkingMarker);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}