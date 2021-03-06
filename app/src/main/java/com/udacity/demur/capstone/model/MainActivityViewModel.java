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
import com.udacity.demur.capstone.database.FirestoreDocumentLiveData;
import com.udacity.demur.capstone.database.FirestoreQueryLiveData;
import com.udacity.demur.capstone.database.Street;
import com.udacity.demur.capstone.database.StreetSeq;
import com.udacity.demur.capstone.database.SweepingPatterns;
import com.udacity.demur.capstone.database.Zone;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements Observable {
    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private final LiveData<List<StreetSeq>> allStreets;
    private final LiveData<List<Zone>> allZones;
    private final LiveData<SweepingPatterns> sweepingPatterns;
    private List<Street> streets = new ArrayList<>();
    private SparseArray<Zone> zones = new SparseArray<>();
    private HashMap<String, String> sweepingPatternsHash = new HashMap<>();
    private List<Integer> detectedZones = new ArrayList<>();
    private List<Marker> zoneLabels = new ArrayList<>();
    private Marker parkingMarker;
    private int permitZone = -1;
    private int parkingDuration;
    private int parkingMarkerPolylineIndex;
    private boolean hasBeenFailedRenderAttempt;
    private boolean isMapRefreshNeeded;
    private boolean isMarkerSetupMode;
    private boolean isActualTimeUsed;
    private boolean isDataPrepared;
    private boolean zonesOverCity;
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
        sweepingPatterns = new FirestoreDocumentLiveData<>(mDb.collection("patterns")
                .document("sweeping"), SweepingPatterns.class);
        allStreets = new FirestoreQueryLiveData<>(mDb.collection("streets"), StreetSeq.class);
    }

    public LiveData<List<StreetSeq>> getAllStreets() {
        return allStreets;
    }

    public LiveData<List<Zone>> getAllZones() {
        return allZones;
    }

    public LiveData<SweepingPatterns> getSweepingPatterns() {
        return sweepingPatterns;
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

    public HashMap<String, String> getSweepingPatternsHash() {
        return sweepingPatternsHash;
    }

    public void setSweepingPatternsHash(HashMap<String, String> sweepingPatternsHash) {
        this.sweepingPatternsHash = sweepingPatternsHash;
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

    public boolean getHasBeenFailedRenderAttempt() {
        return hasBeenFailedRenderAttempt;
    }

    public void setHasBeenFailedRenderAttempt(boolean hasBeenFailedRenderAttempt) {
        this.hasBeenFailedRenderAttempt = hasBeenFailedRenderAttempt;
    }

    public boolean getMapRefreshNeeded() {
        return isMapRefreshNeeded;
    }

    public void setMapRefreshNeeded(boolean mapRefreshNeeded) {
        isMapRefreshNeeded = mapRefreshNeeded;
    }

    public boolean getMarkerSetupMode() {
        return isMarkerSetupMode;
    }

    public void setMarkerSetupMode(boolean markerSetupMode) {
        isMarkerSetupMode = markerSetupMode;
    }

    public boolean getActualTimeUsed() {
        return isActualTimeUsed;
    }

    public void setActualTimeUsed(boolean actualTimeUsed) {
        isActualTimeUsed = actualTimeUsed;
    }

    public boolean getDataPrepared() {
        return isDataPrepared;
    }

    public void setDataPrepared(boolean dataPrepared) {
        isDataPrepared = dataPrepared;
    }

    public boolean getZonesOverCity() {
        return zonesOverCity;
    }

    public void setZonesOverCity(boolean zonesOverCity) {
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