package com.udacity.demur.capstone.database;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class FirestoreDocumentLiveData<T> extends LiveData<T> {

    private static final String TAG = "FirestoreDocLiveData";

    private final DocumentReference docRef;
    private Class<T> classType;
    private final DocumentEventListener listener = new DocumentEventListener();
    private ListenerRegistration listenerRegistration;

    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();

    public FirestoreDocumentLiveData(DocumentReference docRef, Class<T> classType) {
        this.docRef = docRef;
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
            listenerRegistration = docRef.addSnapshotListener(listener);
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

    private class DocumentEventListener implements EventListener<DocumentSnapshot> {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                Log.e(TAG, "Can't listen to document snapshot: " + documentSnapshot + ":::" + e.getMessage());
                return;
            }
            if (documentSnapshot != null) {
                postValue(documentSnapshot.toObject(classType));
            }
        }
    }
}