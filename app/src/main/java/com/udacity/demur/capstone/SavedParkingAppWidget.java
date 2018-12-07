package com.udacity.demur.capstone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.MODE_PRIVATE;

public class SavedParkingAppWidget extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.saved_parking_app_widget);
        Intent appIntent = new Intent(context, MainActivity.class);
        SharedPreferences sharedPrefs = context.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE);
        if (sharedPrefs.contains(MainActivity.SHARED_PREFS_PARKING_LIMIT_KEY)) {
            Calendar parkingLimit = new GregorianCalendar();
            parkingLimit.setTimeInMillis(sharedPrefs.getLong(MainActivity.SHARED_PREFS_PARKING_LIMIT_KEY, 0));
            views.setTextViewText(R.id.appwidget_message, context.getResources().getString(R.string.move_by, MainActivity.markerSDF.format(parkingLimit.getTime())));
            appIntent.putExtra(MainActivity.CAMERA, MainActivity.FOCUS_ON_MARKER);
        } else {
            views.setTextViewText(R.id.appwidget_message, context.getResources().getString(R.string.appwidget_no_info));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.ll_appwidget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}