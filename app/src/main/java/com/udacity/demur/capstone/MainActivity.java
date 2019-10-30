package com.udacity.demur.capstone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.google.maps.android.ui.IconGenerator;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPickerListener;
import com.udacity.demur.capstone.database.Street;
import com.udacity.demur.capstone.database.Zone;
import com.udacity.demur.capstone.databinding.ActivityMainBinding;
import com.udacity.demur.capstone.databinding.NavDateTimeSwitchBinding;
import com.udacity.demur.capstone.databinding.NavDurationSwitchBinding;
import com.udacity.demur.capstone.databinding.NavZonePermitBinding;
import com.udacity.demur.capstone.databinding.SnackbarBinding;
import com.udacity.demur.capstone.model.MainActivityViewModel;
import com.udacity.demur.capstone.service.AppExecutors;
import com.udacity.demur.capstone.service.SublimePickerDialogFragment;
import com.udacity.demur.capstone.service.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnCameraIdleListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 8143;
    public static final String CAMERA = "com.udacity.demur.capstone.extra.CAMERA";
    public static final String FOCUS_ON_MARKER = "focusOnMarker";

    public static final String SHARED_PREFS_NAME = "Settings";
    public static final String SHARED_PREFS_CAM_LAT_KEY = "camLat";
    public static final String SHARED_PREFS_CAM_LNG_KEY = "camLng";
    public static final String SHARED_PREFS_CAM_ZOOM_KEY = "camZoom";
    public static final String SHARED_PREFS_PARKING_LAT_LNG_KEY = "parkingLatLng";
    public static final String SHARED_PREFS_PARKING_LIMIT_KEY = "parkingLimit";
    public static final String SHARED_PREFS_PARKING_START_KEY = "parkingStart";

    public static final String SUBLIME_OPTIONS_KEY = "SUBLIME_OPTIONS";

    ActivityMainBinding mMainBinding;
    NavDateTimeSwitchBinding mNavDateBinding;
    NavDurationSwitchBinding mNavDurationBinding;
    NavZonePermitBinding mNavZonePermitBinding;
    SnackbarBinding mSnackbarBinding;

    private Context mContext;
    private GoogleMap mMap;
    private PolyDrafter mPolyDrafter;
    private MainActivityViewModel mViewModel;
    private SharedPreferences mSharedPrefs;
    private SparseArray<Zone> mZones = null;
    private List<Street> mStreets = null;
    public static final List<Date> mHolidays = Utilities.getHoliDates();
    private static final Object LOCK = new Object();
    public static final SphericalMercatorProjection PROJECTION = new SphericalMercatorProjection(1);
    List<Integer> visibleStreets = new ArrayList<>();
    List<Integer> detectedZones = new ArrayList<>();
    Bounds mCameraBounds;
    private Boolean isDataPrepared = false;
    private Boolean hasBeenFailedRenderAttempt = false;
    public static double mMinDistMarker2Poly = -1;
    public static List<LatLng> mNearestPolyPointsList;
    public List<Integer> mNearestPolylineList;
    private Boolean isMarkerSetupMode = false;
    private Boolean mSyntheticCameraMove = false;
    private Snackbar snackbar;
    private LatLng mMapCenter4MarkerSetup;
    private int mParkingMarkerPolylineIndex;
    private Calendar mParkingLimit;
    private Calendar mParkingStart;
    private int mPermitZone = -1;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat mNavViewSDF = new SimpleDateFormat("ha MM/dd/yy");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat markerSDF = new SimpleDateFormat("h:mm'\u00A0'a EEE,'\u00A0'MMM'\u00A0'dd");
    private Boolean isActualTimeUsed = true;
    private Date mCustomDate;
    private int mParkingDuration = 2;
    private Boolean isMapRefreshNeeded = false;
    private Boolean mFocusOnMarker = false;
    private Boolean mZonesOverCity = false;
    private List<Marker> mZoneLabels = new ArrayList<>();
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mContext = this;

        mFocusOnMarker = (getIntent().hasExtra(CAMERA) && getIntent().getStringExtra(CAMERA).equals(FOCUS_ON_MARKER));

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mViewModel.getAllStreets().observe(MainActivity.this, allStreetsObserver);
        mViewModel.getAllZones().observe(MainActivity.this, allZonesObserver);
        mMainBinding.setViewModel(mViewModel);

        mMainBinding.drawerLayout.addDrawerListener(drawerListener);
        mMainBinding.navView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        mNavDateBinding = NavDateTimeSwitchBinding.inflate(getLayoutInflater());
        mNavDateBinding.setViewModel(mViewModel);
        mMainBinding.navView.getMenu().findItem(R.id.nav_date).setActionView(mNavDateBinding.getRoot());
        mNavDurationBinding = NavDurationSwitchBinding.inflate(getLayoutInflater());
        mNavDurationBinding.setViewModel(mViewModel);
        mMainBinding.navView.getMenu().findItem(R.id.nav_duration).setActionView(mNavDurationBinding.getRoot());
        mNavZonePermitBinding = NavZonePermitBinding.inflate(getLayoutInflater());
        mNavZonePermitBinding.setViewModel(mViewModel);
        mMainBinding.navView.getMenu().findItem(R.id.nav_zone_permit).setActionView(mNavZonePermitBinding.getRoot());

        setSupportActionBar(mMainBinding.toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (null != actionbar) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerToggle = setupDrawerToggle();
        mMainBinding.drawerLayout.addDrawerListener(drawerToggle);

        mMainBinding.fab.setOnClickListener(fabClickListener);

        mSharedPrefs = getApplicationContext().getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        mSharedPrefs.registerOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);

        setupNavView();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_silver));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        prepareData();

        if (mSharedPrefs.contains(SHARED_PREFS_CAM_LAT_KEY) && mSharedPrefs.contains(SHARED_PREFS_CAM_LNG_KEY) && mSharedPrefs.contains(SHARED_PREFS_CAM_ZOOM_KEY)) {
            TypedValue typedValue = new TypedValue();

            getResources().getValue(R.dimen.map_camera_target_lat, typedValue, true);
            float camLat = mSharedPrefs.getFloat(SHARED_PREFS_CAM_LAT_KEY, typedValue.getFloat());
            getResources().getValue(R.dimen.map_camera_target_lng, typedValue, true);
            float camLng = mSharedPrefs.getFloat(SHARED_PREFS_CAM_LNG_KEY, typedValue.getFloat());
            getResources().getValue(R.dimen.map_camera_zoom, typedValue, true);
            float camZoom = mSharedPrefs.getFloat(SHARED_PREFS_CAM_ZOOM_KEY, typedValue.getFloat());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(camLat, camLng), camZoom));
        }
        if (null == mViewModel.getParkingMarker() && mSharedPrefs.contains(SHARED_PREFS_PARKING_LAT_LNG_KEY) && mSharedPrefs.contains(SHARED_PREFS_PARKING_LIMIT_KEY)) {
            String[] latLng = mSharedPrefs.getString(SHARED_PREFS_PARKING_LAT_LNG_KEY, "").split(",");
            if (latLng.length == 2) {
                mParkingLimit = new GregorianCalendar();
                mParkingLimit.setTimeInMillis(mSharedPrefs.getLong(SHARED_PREFS_PARKING_LIMIT_KEY, 0));
                if (mSharedPrefs.contains(SHARED_PREFS_PARKING_START_KEY)) {
                    mParkingStart = new GregorianCalendar();
                    mParkingStart.setTimeInMillis(mSharedPrefs.getLong(SHARED_PREFS_PARKING_START_KEY, 0));
                } else {
                    mParkingStart = null;
                }
                mViewModel.setParkingMarker(mMap.addMarker(baseMarkerOptions()
                        .position(new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1])))
                        .title(Utilities.periodInBetween(mContext, mParkingLimit, mParkingStart))
                        .snippet(Utilities.formatMarkerSnippet(mContext, mParkingLimit, mParkingStart))
                ));
                mViewModel.getParkingMarker().showInfoWindow();
                invalidateOptionsMenu();
            }
        }
        if (mFocusOnMarker && null != mViewModel.getParkingMarker()) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(mViewModel.getParkingMarker().getPosition()));
            mFocusOnMarker = false;
        }

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(cameraMoveStartedListener);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (null != mMap) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                requestLocationPermission();
            }
        } else {
            if (null != mMap) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(
                    mMainBinding.coordinator,
                    R.string.permission_explanation,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.permission_request_launch_approval, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                        }
                    }).show();
        } else {
            Snackbar.make(
                    mMainBinding.coordinator,
                    R.string.permission_warning,
                    Snackbar.LENGTH_SHORT
            ).show();
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (null != mMap) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            }
        }
    }

    private void prepareData() {
        if (!isDataPrepared && null != mStreets && null != mZones) {
            mMap.clear();
            synchronized (LOCK) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int lSize = mStreets.size();
                        visibleStreets = new ArrayList<>();
                        detectedZones = new ArrayList<>();
                        for (int i = 0; i < lSize; i++) {
                            Street street = mStreets.get(i);
                            if (!detectedZones.contains(street.getZone())) {
                                detectedZones.add(street.getZone());
                            }
                            if (null != mZones.get(street.getZone())) {
                                mZones.get(street.getZone()).streetListAdd(i);
                            }
                            mStreets.get(i).setBnds(Utilities.str2llb(street.getBounds()));
                            mStreets.get(i).setPoints(Utilities.str2lllist(street.getCoords()));
                        }
                        if (detectedZones.size() > 0) {
                            int zoneQuantity = detectedZones.size();
                            LatLngBounds.Builder cameraBoundsBuilder = LatLngBounds.builder();
                            for (int h = 0; h < zoneQuantity; h++) {
                                cameraBoundsBuilder.include(mZones.get(detectedZones.get(h)).getBnds().northeast);
                                cameraBoundsBuilder.include(mZones.get(detectedZones.get(h)).getBnds().southwest);
                            }
                            final LatLngBounds cameraBounds = cameraBoundsBuilder.build();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.setLatLngBoundsForCameraTarget(cameraBounds);
                                }
                            });
                        }
                        Collections.sort(detectedZones);
                        mViewModel.setDetectedZones(detectedZones);

                        setupSpinner();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshNavView();
                            }
                        });

                        isDataPrepared = true;
                        if (hasBeenFailedRenderAttempt) {
                            hasBeenFailedRenderAttempt = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mCameraBounds = Utilities.llb2b(mMap.getProjection().getVisibleRegion().latLngBounds);
                                        executePolyDrafter();
                                    } catch (IllegalStateException e) {
                                        // layout not yet initialized
                                        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
                                        if (mapView.getViewTreeObserver().isAlive()) {
                                            mapView.getViewTreeObserver().addOnGlobalLayoutListener(
                                                    new ViewTreeObserver.OnGlobalLayoutListener() {
                                                        @Override
                                                        public void onGlobalLayout() {
                                                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                                            try {
                                                                mCameraBounds = Utilities.llb2b(mMap.getProjection().getVisibleRegion().latLngBounds);
                                                                executePolyDrafter();
                                                            } catch (IllegalStateException e) {
                                                                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                                                    @Override
                                                                    public void onMapLoaded() {
                                                                        mCameraBounds = Utilities.llb2b(mMap.getProjection().getVisibleRegion().latLngBounds);
                                                                        executePolyDrafter();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private void setupNavView() {
        mNavDateBinding.navDateTimeIcon.setOnClickListener(datePickerClickListener);
        mNavDateBinding.tvDateTimeDisplay.setOnClickListener(datePickerClickListener);

        mNavDateBinding.scDateTime.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isMapRefreshNeeded = !mZonesOverCity;
                        isActualTimeUsed = !isChecked;
                        mNavDateBinding.navDateTimeIcon.setColorFilter(ContextCompat.getColor(mContext, isChecked ? R.color.nav_view_action_enabled : R.color.nav_view_action_disabled));
                        mNavDateBinding.tvDateTimeDisplay.setTextColor(ContextCompat.getColor(mContext, isChecked ? R.color.nav_view_action_enabled : R.color.nav_view_action_disabled));

                        mNavDateBinding.tvDateTimeDisplay.getBackground().setColorFilter(ContextCompat.getColor(mContext, isChecked ? R.color.nav_view_action_enabled : R.color.nav_view_action_disabled), PorterDuff.Mode.SRC_IN);

                        mNavDateBinding.navDateTimeIcon.setClickable(isChecked);
                        mNavDateBinding.navDateTimeIcon.setFocusable(isChecked);
                        mNavDateBinding.tvDateTimeDisplay.setClickable(isChecked);
                        mNavDateBinding.tvDateTimeDisplay.setFocusable(isChecked);
                        if (isChecked) {
                            if (isDataPrepared && !mZonesOverCity) {
                                datePickerClickListener.onClick(null);
                            }
                        } else {
                            mNavDateBinding.tvDateTimeDisplay.setText(mNavViewSDF.format(new Date()));
                            mCustomDate = null;
                        }
                    }
                });

        mNavDurationBinding.snpDuration.setListener(new ScrollableNumberPickerListener() {
            @Override
            public void onNumberPicked(int value) {
                isMapRefreshNeeded = !mZonesOverCity;
                mParkingDuration = value;
            }
        });
    }

    private void setupSpinner() {
        final int zoneQuantity = detectedZones.size();
        final Integer[] zoneListIndexes = new Integer[zoneQuantity + 1];
        final String[] zoneListLabels = new String[zoneQuantity + 1];
        int selPos = zoneQuantity;
        for (int y = 0; y < zoneQuantity; y++) {
            zoneListIndexes[y] = detectedZones.get(y);
            zoneListLabels[y] = getString(R.string.spinner_option_zone, detectedZones.get(y));
            if (mPermitZone == detectedZones.get(y)) {
                selPos = y;
            }
        }
        zoneListIndexes[zoneQuantity] = -1;
        zoneListLabels[zoneQuantity] = getString(R.string.spinner_option_no_permit);

        final int spinnerSelectedPosition = selPos;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNavZonePermitBinding.zoneSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, zoneListLabels));
                mNavZonePermitBinding.zoneSpinner.setSelection(spinnerSelectedPosition);
                mNavZonePermitBinding.zoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mPermitZone = zoneListIndexes[position];
                        isMapRefreshNeeded = !mZonesOverCity;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
    }

    private void refreshNavView() {
        mNavDateBinding.scDateTime.setChecked(!isActualTimeUsed);
        mNavDateBinding.navDateTimeIcon
                .setColorFilter(isActualTimeUsed ? ContextCompat.getColor(mContext, R.color.nav_view_action_disabled) : ContextCompat.getColor(mContext, R.color.nav_view_action_enabled));//, android.graphics.PorterDuff.Mode.SRC_IN
        mNavDateBinding.tvDateTimeDisplay
                .setText(mNavViewSDF.format(isActualTimeUsed ? new Date() : null != mCustomDate ? mCustomDate : new Date()));
        mNavDateBinding.tvDateTimeDisplay
                .setTextColor(ContextCompat.getColor(mContext, isActualTimeUsed ? R.color.nav_view_action_disabled : R.color.nav_view_action_enabled));
        mNavDateBinding.tvDateTimeDisplay.getBackground()
                .setColorFilter(ContextCompat.getColor(mContext, isActualTimeUsed ? R.color.nav_view_action_disabled : R.color.nav_view_action_enabled), PorterDuff.Mode.SRC_IN);
        mNavDurationBinding.snpDuration.setValue(mParkingDuration);
    }

    GoogleMap.OnCameraMoveStartedListener cameraMoveStartedListener = new GoogleMap.OnCameraMoveStartedListener() {
        @Override
        public void onCameraMoveStarted(int reason) {
            if (isMarkerSetupMode) {
                if (mSyntheticCameraMove && reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    mMap.stopAnimation();
                    mSyntheticCameraMove = false;
                }
                mMainBinding.ivTip.setVisibility(View.INVISIBLE);
                mMainBinding.tvTooltip.setVisibility(View.INVISIBLE);
                if (null != mSnackbarBinding && null != mSnackbarBinding.actionSave) {
                    mSnackbarBinding.actionSave.setEnabled(false);
                }
            }
        }
    };

    @Override
    public void onCameraIdle() {
        if (isMarkerSetupMode) {
            if (null != mMap && null != mMap.getCameraPosition()) {
                mMapCenter4MarkerSetup = mMap.getCameraPosition().target;
            } else {
                hasBeenFailedRenderAttempt = true;
                return;
            }
        }
        if (null != mViewModel.getParkingMarker() && null != mParkingLimit && null == mParkingStart) {
            mViewModel.getParkingMarker().setTitle(Utilities.periodInBetween(mContext, mParkingLimit, null));
            mViewModel.getParkingMarker().setSnippet(Utilities.formatMarkerSnippet(mContext, mParkingLimit, null));
        }
        if (mSyntheticCameraMove) {
            Street targetStreet = mStreets.get(mParkingMarkerPolylineIndex);
            if (null != mSnackbarBinding && null != mSnackbarBinding.actionSave) {
                mMainBinding.tvTooltip.setText(Utilities.formatMarkerPlaceHolderTitle(mContext, targetStreet.getParkingLimit(), mParkingStart, targetStreet.getAvailableHours() >= 0));
                mMainBinding.tvTooltip.setVisibility(View.VISIBLE);
                mMainBinding.ivTip.setVisibility(View.VISIBLE);
                mSnackbarBinding.actionSave.setEnabled(true);
            }
            mSyntheticCameraMove = false;
            return;
        }
        if (isDataPrepared) {
            if (!mZonesOverCity) {
                mCameraBounds = Utilities.llb2b(mMap.getProjection().getVisibleRegion().latLngBounds);
                executePolyDrafter();
            }
        } else {
            hasBeenFailedRenderAttempt = true;
        }
    }

    private void executePolyDrafter() {
        if (null != mPolyDrafter && mPolyDrafter.getStatus() != AsyncTask.Status.FINISHED) {
            mPolyDrafter.cancel(true);
        }
        mPolyDrafter = new PolyDrafter();
        mPolyDrafter.execute();
    }

    class PolyDrafter extends AsyncTask<String, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMainBinding.progressBar.setProgress(0);
            mMainBinding.progressBar.setVisibility(View.VISIBLE);
            if (null == mCameraBounds) {
                mCameraBounds = Utilities.llb2b(mMap.getProjection().getVisibleRegion().latLngBounds);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            final int streetSize = mStreets.size();
            if (mZonesOverCity) {
                int[] zoneColors = getResources().getIntArray(R.array.zones);
                for (int p = 0; p < streetSize; p++) {
                    final int pIndex = p;
                    final int color = zoneColors[mStreets.get(p).getZone()];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null == mStreets.get(pIndex).getPoly()) {
                                mStreets.get(pIndex).setPoly(
                                        mMap.addPolyline(
                                                new PolylineOptions()
                                                        .geodesic(true)
                                                        .jointType(2)
                                                        .startCap(new RoundCap())
                                                        .endCap(new RoundCap())
                                                        .color(color)
                                                        .zIndex(100)
                                                        .addAll(mStreets.get(pIndex).getPoints())
                                                        .visible(true)
                                        )
                                );
                            } else {
                                mStreets.get(pIndex).getPoly().setColor(color);
                                mStreets.get(pIndex).getPoly().setVisible(true);
                            }
                        }
                    });
                    publishProgress((int) ((p / (float) streetSize) * 100));
                }
                return null;
            }

            final int zoneSize = mZones.size();
            List<Integer> visibleZones = new ArrayList<>();

            final int colorAllowed = ContextCompat.getColor(mContext, R.color.parking_allowed);
            final int colorLimited = ContextCompat.getColor(mContext, R.color.parking_limited);
            final int colorProhibited = ContextCompat.getColor(mContext, R.color.parking_prohibited);

            int permitOfZone = mPermitZone;
            Calendar calend = new GregorianCalendar();

            Date targetDate = !isActualTimeUsed && null != mCustomDate ? mCustomDate : new Date();
            calend.setTime(targetDate);
            if (isMarkerSetupMode && !isActualTimeUsed && null != mCustomDate) {
                mParkingStart = (Calendar) calend.clone();
            }

            int minuteOfHour = calend.get(Calendar.MINUTE);
            double hourFraction = minuteOfHour / 60.0;
            HashMap<String, Integer> sweepingHash = new HashMap<>();
            HashMap<String, Integer> zoneHoursHash = new HashMap<>();
            int availHours = 0;
            double availFloat = 0;

            for (int i = 0; i < zoneSize; i++) {
                int key = mZones.keyAt(i);
                if (mCameraBounds.intersects(Utilities.llb2b(mZones.get(key).getBnds()))) {
                    visibleZones.add(key);
                }
            }
            for (int j = 0; j < streetSize; j++) {
                final int jIndex = j;
                if (visibleZones.contains(mStreets.get(j).getZone())) {
                    if (null == mStreets.get(j).getBnds()) {
                        mStreets.get(j).setBnds(Utilities.str2llb(mStreets.get(j).getBounds()));
                    }
                    if (null != mStreets.get(j).getBnds() && mCameraBounds.intersects(Utilities.llb2b(mStreets.get(j).getBnds()))) {
                        Calendar parkingLimit = null;
                        String sweepingPattern = mStreets.get(j).getSweeping();
                        if (sweepingHash.containsKey(sweepingPattern)) {
                            availHours = sweepingHash.get(sweepingPattern);
                        } else {
                            availHours = Utilities.getAvailHours(sweepingPattern, (Calendar) calend.clone());
                            sweepingHash.put(sweepingPattern, availHours);
                        }
                        if (permitOfZone != mStreets.get(j).getZone()) {
                            String activeHoursPattern = mZones.get(mStreets.get(j).getZone()).getActiveHours();
                            int unrstdHours = 0;
                            if (zoneHoursHash.containsKey(activeHoursPattern)) {
                                unrstdHours = zoneHoursHash.get(activeHoursPattern);
                            } else {
                                unrstdHours = Utilities.getAvailHours(activeHoursPattern, (Calendar) calend.clone());
                                zoneHoursHash.put(activeHoursPattern, unrstdHours);
                            }
                            int alienLimit = mZones.get(mStreets.get(j).getZone()).getAlienLimit();
                            if (availHours >= 0 && unrstdHours >= 0) {
                                if (unrstdHours == 0 && alienLimit > 0 &&
                                        (alienLimit < availHours || (alienLimit == availHours && minuteOfHour == 0))) {
                                    parkingLimit = (null == mParkingStart ? new GregorianCalendar() : (Calendar) mParkingStart.clone());
                                    parkingLimit.add(Calendar.HOUR, alienLimit);
                                }
                                availHours = Math.min(availHours, unrstdHours + alienLimit);
                            } else if (availHours < 0 && unrstdHours >= 0) {
                                if (Math.abs(availHours) >= unrstdHours) {
                                    Calendar tmpCalend = (Calendar) calend.clone();
                                    tmpCalend.add(Calendar.HOUR, Math.abs(availHours));
                                    int rstdHours = Utilities.getAvailHours(activeHoursPattern, tmpCalend);
                                    if (rstdHours < 0 && alienLimit == 0) {
                                        availHours -= rstdHours;
                                    }
                                }
                            } else if (availHours >= 0 && unrstdHours < 0) {
                                if (availHours > 0 && alienLimit > 0) {
                                    if (Math.abs(unrstdHours) > alienLimit) {
                                        if (alienLimit < availHours) {
                                            parkingLimit = (null == mParkingStart ? new GregorianCalendar() : (Calendar) mParkingStart.clone());
                                            parkingLimit.add(Calendar.HOUR, alienLimit);
                                        }
                                        availHours = Math.min(availHours, alienLimit);
                                    }
                                } else if (alienLimit == 0) {
                                    if (availHours > Math.abs(unrstdHours)) {
                                        availHours = unrstdHours;
                                    } else {
                                        Calendar tmpCalend = (Calendar) calend.clone();
                                        tmpCalend.add(Calendar.HOUR, Math.abs(unrstdHours));
                                        int nextAvailHours = Utilities.getAvailHours(sweepingPattern, tmpCalend);
                                        int rstdHours = Utilities.getAvailHours(activeHoursPattern, tmpCalend);
                                        // For more common approach this block needs recursive function
                                        if (nextAvailHours >= 0 && rstdHours >= 0) {
                                            availHours = unrstdHours;
                                        } else if (nextAvailHours < 0 && rstdHours >= 0) {
                                            availHours = unrstdHours + nextAvailHours;
                                        } else if (nextAvailHours >= 0 && rstdHours < 0) {
                                            availHours = unrstdHours + rstdHours;
                                        } else if (nextAvailHours < 0 && rstdHours < 0) {
                                            availHours = unrstdHours + Math.min(nextAvailHours, rstdHours);
                                        }
                                    }
                                }
                            } else if (availHours < 0 && unrstdHours < 0) {
                                // Same recursive function needed here for common approach to be adopted
                                if (Math.abs(unrstdHours) > availHours) {
                                    if (alienLimit == 0) {
                                        availHours = unrstdHours;
                                    }
                                }
                            }
                        }

                        final int color;
                        if (availHours < 0) {
                            availFloat = (double) availHours + (null == parkingLimit ? hourFraction : 0);
                            color = colorProhibited;
                        } else {
                            availFloat = (double) availHours - (null == parkingLimit ? hourFraction : 0);
                            if (availFloat < 0.25) {
                                color = colorProhibited;
                            } else if (availFloat < mParkingDuration) {
                                color = colorLimited;
                            } else {
                                color = colorAllowed;
                            }
                        }
                        if (!visibleStreets.contains(j)) {
                            if (null == mStreets.get(j).getPoly()) {
                                if (null == mStreets.get(j).getPoints()) {
                                    mStreets.get(j).setPoints(Utilities.str2lllist(mStreets.get(j).getCoords()));
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mStreets.get(jIndex).setPoly(
                                                mMap.addPolyline(
                                                        new PolylineOptions()
                                                                .geodesic(true)
                                                                .jointType(2)
                                                                .startCap(new RoundCap())
                                                                .endCap(new RoundCap())
                                                                .color(color)
                                                                .zIndex(100)
                                                                .addAll(mStreets.get(jIndex).getPoints())
                                                                .visible(true)
                                                )
                                        );
                                    }
                                });
                                mStreets.get(j).setVisible(true);
                                visibleStreets.add(j);
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mStreets.get(jIndex).getPoly().setColor(color);
                                        mStreets.get(jIndex).getPoly().setVisible(true);
                                    }
                                });
                            }
                            if (!mStreets.get(j).isVisible()) {
                                mStreets.get(j).setVisible(true);
                                visibleStreets.add(j);
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mStreets.get(jIndex).getPoly().setColor(color);
                                }
                            });
                        }
                        if (null == parkingLimit) {
                            parkingLimit = (null == mParkingStart ? new GregorianCalendar() : (Calendar) mParkingStart.clone());
                            parkingLimit.add(Calendar.HOUR, Math.abs(availHours));
                            parkingLimit.set(Calendar.MINUTE, 0);
                            parkingLimit.set(Calendar.SECOND, 0);
                            parkingLimit.set(Calendar.MILLISECOND, 0);
                        }
                        mStreets.get(j).setAvailableHours(availHours);
                        mStreets.get(j).setParkingLimit(parkingLimit);
                        if (isMarkerSetupMode) {
                            int result = Utilities.findShortestDistance(mMapCenter4MarkerSetup, mStreets.get(j).getPoints());
                            if (result != -1) {
                                if (result == 0) {
                                    mNearestPolylineList = new ArrayList<>();
                                }
                                int count = mNearestPolyPointsList.size() - mNearestPolylineList.size();
                                for (int q = 0; q < count; q++) {
                                    mNearestPolylineList.add(j);
                                }
                            }
                        }
                    } else {
                        if (mStreets.get(j).isVisible()) {
                            mStreets.get(j).setVisible(false);
                            if (null != mStreets.get(j).getPoly()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mStreets.get(jIndex).getPoly().setVisible(false);
                                    }
                                });
                            }
                        }
                        if (visibleStreets.contains(j)) {
                            visibleStreets.remove(Integer.valueOf(j));
                        }
                    }
                } else {
                    if (visibleStreets.contains(j)) {
                        visibleStreets.remove(Integer.valueOf(j));
                    }
                    if (mStreets.get(j).isVisible()) {
                        if (null != mStreets.get(j).getPoly()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mStreets.get(jIndex).getPoly().isVisible()) {
                                        mStreets.get(jIndex).getPoly().setVisible(false);
                                    }
                                }
                            });
                        }
                        mStreets.get(j).setVisible(false);
                    }
                }
                publishProgress((int) ((j / (float) streetSize) * 100));
            }
            mViewModel.setStreets(mStreets);
            mViewModel.setZones(mZones);
            return null;
        }

        @Override
        public void onProgressUpdate(Integer... progress) {
            mMainBinding.progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void v) {
            if (isMarkerSetupMode) {
                if (mNearestPolyPointsList.size() > 0 && mNearestPolylineList.size() > 0) {
                    int pointIndex = 0;
                    if (mNearestPolyPointsList.size() != 1 && (new ArrayList<>(new HashSet<>(mNearestPolylineList))).size() != 1) {
                        int maxAvailablePeriod = mStreets.get(mNearestPolylineList.get(0)).getAvailableHours();
                        int listSize = mNearestPolylineList.size();
                        for (int w = 1; w < listSize; w++) {
                            if (mStreets.get(mNearestPolylineList.get(w)).getAvailableHours() > maxAvailablePeriod) {
                                pointIndex = w;
                                maxAvailablePeriod = mStreets.get(mNearestPolylineList.get(w)).getAvailableHours();
                            }
                        }
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mNearestPolyPointsList.get(pointIndex)));
                    mParkingMarkerPolylineIndex = mNearestPolylineList.get(pointIndex);
                    mSyntheticCameraMove = true;
                }
                mNearestPolylineList = new ArrayList<>();
                mNearestPolyPointsList = new ArrayList<>();
                mMinDistMarker2Poly = -1;
            }
            if (mZonesOverCity && mZoneLabels.size() == 0) {
                int[] zoneColors = getResources().getIntArray(R.array.zones);
                int zoneQuantity = detectedZones.size();
                IconGenerator iconFactory = new IconGenerator(mContext);
                iconFactory.setColor(getResources().getColor(R.color.light_gray));
                for (int y = 0; y < zoneQuantity; y++) {
                    String zoneTitle = getString(R.string.spinner_option_zone, detectedZones.get(y));
                    SpannableStringBuilder ssbTitle = new SpannableStringBuilder(zoneTitle);
                    ssbTitle.setSpan(new ForegroundColorSpan(zoneColors[detectedZones.get(y)]), 0, zoneTitle.length(), SPAN_INCLUSIVE_INCLUSIVE);
                    ssbTitle.setSpan(new StyleSpan(BOLD), 0, zoneTitle.length(), SPAN_INCLUSIVE_INCLUSIVE);
                    mZoneLabels.add(mMap.addMarker(
                            new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(ssbTitle)))
                                    .position(mZones.get(detectedZones.get(y)).getBnds().getCenter())
                                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
                    ));
                }
            }
            mMainBinding.progressBar.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onPause() {
        if (null != mMap.getCameraPosition()) {
            mSharedPrefs.edit()
                    .putFloat(SHARED_PREFS_CAM_LAT_KEY, (float) mMap.getCameraPosition().target.latitude)
                    .putFloat(SHARED_PREFS_CAM_LNG_KEY, (float) mMap.getCameraPosition().target.longitude)
                    .putFloat(SHARED_PREFS_CAM_ZOOM_KEY, mMap.getCameraPosition().zoom)
                    .commit();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_share).setVisible(null != mViewModel.getParkingMarker() && null != mParkingLimit && mViewModel.getParkingMarker().isVisible());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mMainBinding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_share:
                shareParkingLocation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareParkingLocation() {
        if (null != mViewModel.getParkingMarker() && null != mParkingLimit) {
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(getString(
                            R.string.message_to_share,
                            mViewModel.getParkingMarker().getPosition().latitude,
                            mViewModel.getParkingMarker().getPosition().longitude,
                            getString(R.string.period_till, markerSDF.format(mParkingLimit.getTime()))))
                    .setChooserTitle(R.string.share_location_choose_title)
                    .startChooser();
            return;
        }
        Toast toast = Toast.makeText(mContext, R.string.nothing_to_share, Toast.LENGTH_LONG);
        toast.getView().getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.snackbar_warning_background), PorterDuff.Mode.SRC_IN);
        ((TextView) toast.getView().findViewById(android.R.id.message)).setTextColor(ContextCompat.getColor(mContext, R.color.snackbar_text));
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);
        toast.show();
    }

    View.OnClickListener datePickerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isActualTimeUsed) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(null != mCustomDate ? mCustomDate : new Date());

                SublimePickerDialogFragment pickerFrag = new SublimePickerDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(SUBLIME_OPTIONS_KEY, new SublimeOptions()
                        .setDisplayOptions((SublimeOptions.ACTIVATE_DATE_PICKER | SublimeOptions.ACTIVATE_TIME_PICKER) & ~SublimeOptions.ACTIVATE_RECURRENCE_PICKER)
                        .setCanPickDateRange(false)
                        .setDateParams(calendar)
                        .setTimeParams(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                );
                pickerFrag.setCallback(getTimePickerCallbackListener());
                pickerFrag.setArguments(bundle);
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getSupportFragmentManager(), "SublimePickerDialogFragment");
            }
        }
    };

    public SublimePickerDialogFragment.Callback getTimePickerCallbackListener() {
        return new SublimePickerDialogFragment.Callback() {
            @Override
            public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute) {
                Calendar calend = new GregorianCalendar();
                calend.set(Calendar.YEAR, selectedDate.getEndDate().get(Calendar.YEAR));
                calend.set(Calendar.MONTH, selectedDate.getEndDate().get(Calendar.MONTH));
                calend.set(Calendar.DAY_OF_MONTH, selectedDate.getEndDate().get(Calendar.DATE));
                calend.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calend.set(Calendar.MINUTE, minute);
                calend.set(Calendar.SECOND, 0);
                calend.set(Calendar.MILLISECOND, 0);
                Date targetDate = calend.getTime();

                mCustomDate = targetDate;
                mNavDateBinding.tvDateTimeDisplay.setText(mNavViewSDF.format(targetDate));
                isMapRefreshNeeded = !mZonesOverCity;
            }
        };
    }

    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {
        }

        @Override
        public void onDrawerOpened(@NonNull View view) {
        }

        @Override
        public void onDrawerClosed(@NonNull View view) {
            if (isMapRefreshNeeded) {
                executePolyDrafter();
                isMapRefreshNeeded = false;
            }
        }

        @Override
        public void onDrawerStateChanged(int i) {
        }
    };

    ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(
                MainActivity.this,
                mMainBinding.drawerLayout,
                mMainBinding.toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_look4parking:
                    if (!menuItem.isCheckable()) {
                        menuItem.setCheckable(true);
                    }
                    menuItem.setChecked(true);
                    if (mZonesOverCity) {
                        mZonesOverCity = false;
                        isMapRefreshNeeded = true;
                        modeSwitchCleanUp();
                    }
                    break;
                case R.id.nav_zones_over_downtown:
                    mMainBinding.drawerLayout.closeDrawers();
                    if (!menuItem.isCheckable()) {
                        menuItem.setCheckable(true);
                    }
                    if (!mZonesOverCity) {
                        mZonesOverCity = true;
                        isMapRefreshNeeded = true;
                        modeSwitchCleanUp();
                    }
                    menuItem.setChecked(true);
                    break;
                case R.id.nav_about:
                    mMainBinding.drawerLayout.closeDrawers();
                    Intent aboutIntent = new Intent(mContext, AboutActivity.class);
                    startActivity(aboutIntent, ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    break;
                case R.id.nav_report:
                    mMainBinding.drawerLayout.closeDrawers();
                    Intent reportIntent = new Intent(mContext, ReportActivity.class);
                    startActivity(reportIntent, ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    final View.OnClickListener fabClickListener = new View.OnClickListener() {
        @SuppressLint("ApplySharedPref")
        @Override
        public void onClick(View view) {
            mMainBinding.fab.hide();
            if (null == mViewModel.getParkingMarker()) {
                showSnackbar();
            } else {
                invalidateOptionsMenu();
                mViewModel.getParkingMarker().setVisible(false);
                mViewModel.getParkingMarker().remove();
                mViewModel.setParkingMarker(null);
                mParkingLimit = null;
                mParkingStart = null;
                mSharedPrefs.edit()
                        .remove(SHARED_PREFS_PARKING_LAT_LNG_KEY)
                        .remove(SHARED_PREFS_PARKING_LIMIT_KEY)
                        .remove(SHARED_PREFS_PARKING_START_KEY)
                        .commit();
                mMainBinding.fab.show();
            }
        }
    };

    private void showSnackbar() {
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        snackbar = Snackbar.make(mMainBinding.coordinator, "", Snackbar.LENGTH_INDEFINITE);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (!mZonesOverCity) {
                    mMainBinding.fab.show();
                }
                mMainBinding.marker.setVisibility(View.INVISIBLE);
                mMainBinding.ivTip.setVisibility(View.INVISIBLE);
                mMainBinding.tvTooltip.setVisibility(View.INVISIBLE);
                isMarkerSetupMode = false;
                mSnackbarBinding = null;
                super.onDismissed(transientBottomBar, event);
            }
        });

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        CoordinatorLayout.LayoutParams parentParams = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();
        parentParams.setMargins(0, 0, 0, 0);
        layout.setLayoutParams(parentParams);
        layout.setPadding(0, 0, 0, 0);
        layout.setLayoutParams(parentParams);

        mSnackbarBinding = SnackbarBinding.inflate(getLayoutInflater());

        mSnackbarBinding.actionSave.setEnabled(false);
        mSnackbarBinding.actionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mPolyDrafter && mPolyDrafter.getStatus() != AsyncTask.Status.FINISHED) {
                    return;
                }
                Street pSt = mStreets.get(mParkingMarkerPolylineIndex);
                if (pSt.getAvailableHours() < 0) {
                    Toast toast = Toast.makeText(mContext, R.string.cant_save_prohibited, Toast.LENGTH_LONG);
                    toast.getView().getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.snackbar_warning_background), PorterDuff.Mode.SRC_IN);
                    ((TextView) toast.getView().findViewById(android.R.id.message)).setTextColor(ContextCompat.getColor(mContext, R.color.snackbar_text));
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);
                    toast.show();
                    return;
                }
                mParkingLimit = pSt.getParkingLimit();
                mViewModel.setParkingMarker(mMap.addMarker(baseMarkerOptions()
                        .position(mMapCenter4MarkerSetup)
                        .title(Utilities.periodInBetween(mContext, mParkingLimit, mParkingStart))
                        .snippet(Utilities.formatMarkerSnippet(mContext, mParkingLimit, mParkingStart))
                ));
                mViewModel.getParkingMarker().showInfoWindow();
                invalidateOptionsMenu();
                mSharedPrefs.edit()
                        .putString(SHARED_PREFS_PARKING_LAT_LNG_KEY, mMapCenter4MarkerSetup.latitude + "," + mMapCenter4MarkerSetup.longitude)
                        .putLong(SHARED_PREFS_PARKING_LIMIT_KEY, mParkingLimit.getTimeInMillis())
                        .apply();
                if (null != mParkingStart) {
                    mSharedPrefs.edit()
                            .putLong(SHARED_PREFS_PARKING_START_KEY, mParkingStart.getTimeInMillis())
                            .apply();
                }
                snackbar.dismiss();
            }
        });

        mSnackbarBinding.actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        layout.addView(mSnackbarBinding.getRoot(), objLayoutParams);

        snackbar.show();
        mMainBinding.marker.setVisibility(View.VISIBLE);
        isMarkerSetupMode = true;
        onCameraIdle();
    }

    private void modeSwitchCleanUp() {
        if (mZonesOverCity && isMarkerSetupMode) {
            snackbar.dismiss();
        }
        if (mZonesOverCity) {
            mMainBinding.fab.hide();
        } else {
            mMainBinding.fab.show();
        }
        mMainBinding.navView.getMenu().findItem(R.id.nav_date).setVisible(!mZonesOverCity);
        mMainBinding.navView.getMenu().findItem(R.id.nav_duration).setVisible(!mZonesOverCity);
        mMainBinding.navView.getMenu().findItem(R.id.nav_zone_permit).setVisible(!mZonesOverCity);
        if (null != mViewModel.getParkingMarker()) {
            mViewModel.getParkingMarker().setVisible(!mZonesOverCity);
            mViewModel.getParkingMarker().showInfoWindow();
            invalidateOptionsMenu();
        }
        if (!mZonesOverCity && mZoneLabels.size() > 0) {
            int labelsSize = mZoneLabels.size();
            for (int g = 0; g < labelsSize; g++) {
                mZoneLabels.get(g).setVisible(false);
                mZoneLabels.get(g).remove();
            }
            mZoneLabels = new ArrayList<>();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mViewModel.setParkingDuration(mParkingDuration);
        mViewModel.setActualTimeUsed(isActualTimeUsed);
        mViewModel.setCustomDate(mCustomDate);
        mViewModel.setDataPrepared(isDataPrepared);
        mViewModel.setHasBeenFailedRenderAttempt(hasBeenFailedRenderAttempt);
        mViewModel.setMapRefreshNeeded(isMapRefreshNeeded);
        mViewModel.setMarkerSetupMode(isMarkerSetupMode);
        mViewModel.setZonesOverCity(mZonesOverCity);
        mViewModel.setParkingMarkerPolylineIndex(mParkingMarkerPolylineIndex);
        mViewModel.setPermitZone(mPermitZone);
        mViewModel.setZoneLabels(mZoneLabels);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mStreets = mViewModel.getStreets();
        mZones = mViewModel.getZones();
        detectedZones = mViewModel.getDetectedZones();
        mParkingDuration = mViewModel.getParkingDuration();
        isActualTimeUsed = mViewModel.getActualTimeUsed();
        mCustomDate = mViewModel.getCustomDate();
        isDataPrepared = mViewModel.getDataPrepared();
        hasBeenFailedRenderAttempt = mViewModel.getHasBeenFailedRenderAttempt();
        isMapRefreshNeeded = mViewModel.getMapRefreshNeeded();
        isMarkerSetupMode = mViewModel.getMarkerSetupMode();
        mZonesOverCity = mViewModel.getZonesOverCity();
        mParkingMarkerPolylineIndex = mViewModel.getParkingMarkerPolylineIndex();
        mPermitZone = mViewModel.getPermitZone();
        mZoneLabels = mViewModel.getZoneLabels();
    }

    final Observer<List<Street>> allStreetsObserver = new Observer<List<Street>>() {
        @Override
        public void onChanged(@Nullable final List<Street> allStreets) {
            if (null != allStreets && null != mMap) {
                mStreets = allStreets;
                handleDataPrepOnObserverExec();
            }
        }
    };

    final Observer<List<Zone>> allZonesObserver = new Observer<List<Zone>>() {
        @Override
        public void onChanged(@Nullable final List<Zone> allZones) {
            if (null != allZones && null != mMap) {
                mZones = new SparseArray<>();
                for (Zone zone : allZones) {
                    int zoneId = Integer.parseInt(zone.getId());
                    mZones.append(zoneId, zone);
                    String[] zBndCoords = zone.getBounds().replaceAll("\\(|\\)", "").split(",");
                    if (zBndCoords.length == 4) {
                        mZones.get(zoneId).setBnds(new LatLngBounds(
                                new LatLng(Double.parseDouble(zBndCoords[0]), Double.parseDouble(zBndCoords[1])),
                                new LatLng(Double.parseDouble(zBndCoords[2]), Double.parseDouble(zBndCoords[3]))
                        ));
                    }
                }
                handleDataPrepOnObserverExec();
            }
        }
    };

    void handleDataPrepOnObserverExec() {
        isDataPrepared = false;
        Boolean isInfoWindowShown = false;
        if (null != mViewModel.getParkingMarker()) {
            isInfoWindowShown = mViewModel.getParkingMarker().isInfoWindowShown();
        }
        prepareData();
        if (null != mViewModel.getParkingMarker()) {
            mViewModel.setParkingMarker(mMap.addMarker(baseMarkerOptions()
                    .position(mViewModel.getParkingMarker().getPosition())
                    .title(mViewModel.getParkingMarker().getTitle())
                    .snippet(mViewModel.getParkingMarker().getSnippet())
            ));
            if (isInfoWindowShown) {
                mViewModel.getParkingMarker().showInfoWindow();
            }
        }
        onCameraIdle();
    }

    MarkerOptions baseMarkerOptions() {
        return new MarkerOptions()
                .anchor(0.27f, 0.93f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking))
                .infoWindowAnchor(0.27f, 0f);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSpinner();
        refreshNavView();
        if (null != mViewModel.getParkingMarker()) {
            if (mFocusOnMarker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(mViewModel.getParkingMarker().getPosition()));
                mFocusOnMarker = false;
            }
        } else if (isMarkerSetupMode) {
            fabClickListener.onClick(null);
        }
        if (mZonesOverCity) {
            modeSwitchCleanUp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPrefsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SHARED_PREFS_PARKING_LIMIT_KEY)) {
                Intent intent = new Intent(mContext, SavedParkingAppWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = AppWidgetManager.getInstance(getApplication())
                        .getAppWidgetIds(new ComponentName(getApplication(), SavedParkingAppWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mFocusOnMarker = (intent.hasExtra(CAMERA) && intent.getStringExtra(CAMERA).equals(FOCUS_ON_MARKER));
    }
}