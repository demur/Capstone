package com.udacity.demur.capstone.database;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirestoreQueryLiveData<T> extends LiveData<List<T>> {

    private static final String TAG = "FirestoreQueryLiveData";

    private Query query;
    private Class<T> classType;
    private final QueryEventListener listener = new QueryEventListener();
    private ListenerRegistration listenerRegistration;

    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();

    public FirestoreQueryLiveData(Query query, Class<T> classType) {
        this.query = query;
        this.classType = classType;
    }

    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            listenerRegistration.remove();
            listenerRemovePending = false;
        }
    };

    @Override
    protected void onActive() {
        super.onActive();

        Log.d(TAG, "onActive");
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        } else {
            listenerRegistration = query.addSnapshotListener(listener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        Log.d(TAG, "onInactive: ");
        // Listener removal is schedule on a two second delay
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }

    private class QueryEventListener implements EventListener<QuerySnapshot> {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                Log.e(TAG, "Can't listen to query snapshots: " + querySnapshot + ":::" + e.getMessage());
                return;
            }
            if (querySnapshot != null) {
                postValue(querySnapshot.toObjects(classType));
            }
        }
    }
}