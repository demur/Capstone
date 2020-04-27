package com.udacity.demur.capstone.model;

import android.app.Application;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.databinding.library.baseAdapters.BR;//https://stackoverflow.com/a/58028581
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.udacity.demur.capstone.database.FirestoreQueryLiveData;
import com.udacity.demur.capstone.database.Street;
import com.udacity.demur.capstone.database.StreetSeq;
import com.udacity.demur.capstone.database.Zone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements Observable {
    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private final LiveData<List<StreetSeq>> allStreets;
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
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (null == mUser) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mUser = mAuth.getCurrentUser();
                            }
                        }
                    });
        }
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        allZones = new FirestoreQueryLiveData<>(mDb.collection("zones"), Zone.class);
        allStreets = new FirestoreQueryLiveData<>(mDb.collection("streets"), StreetSeq.class);
    }

    public LiveData<List<StreetSeq>> getAllStreets() {
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
        callbacks.notifyChange(this, 0);
    }

    void notifyPropertyChanged(int fieldId) {
        callbacks.notifyChange(this, fieldId);
    }
}