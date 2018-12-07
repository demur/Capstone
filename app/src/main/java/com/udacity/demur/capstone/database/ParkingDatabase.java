package com.udacity.demur.capstone.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory;

@Database(version = 1, entities = {Zone.class, Street.class}, exportSchema = false)
public abstract class ParkingDatabase extends RoomDatabase {
    public abstract Zone.Store zoneStore();
    public abstract Street.Store streetStore();

    private static final String TAG = ParkingDatabase.class.getSimpleName();

    private static final String DATABASE_NAME = "parking.db";
    private static volatile ParkingDatabase sInstance = null;

    public synchronized static ParkingDatabase get(Context ctxt) {
        if (sInstance == null) {
            sInstance = create(ctxt);
        }

        return (sInstance);
    }

    private static ParkingDatabase create(Context ctxt) {
        RoomDatabase.Builder<ParkingDatabase> b = Room
                .databaseBuilder(ctxt.getApplicationContext(), ParkingDatabase.class, DATABASE_NAME);
        return (b.openHelperFactory(new AssetSQLiteOpenHelperFactory()).build());
    }
}