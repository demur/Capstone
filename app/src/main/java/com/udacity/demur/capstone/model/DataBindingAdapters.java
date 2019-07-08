package com.udacity.demur.capstone.model;

import androidx.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DataBindingAdapters {
    //TODO clean this hack after update for com.android.support:design:28.0.0+
    @BindingAdapter("android:src")
    public static void setImageUri(FloatingActionButton view, String imageUri) {
        Boolean needToShow = false;
        if (view.isShown()) {
            view.hide();
            needToShow = true;
        }
        if (null == imageUri) {
            view.setImageURI(null);
        } else {
            view.setImageURI(Uri.parse(imageUri));
        }
        if (needToShow) {
            view.show();
        }
    }

    @BindingAdapter("android:src")
    public static void setImageUri(FloatingActionButton view, Uri imageUri) {
        Boolean needToShow = false;
        if (view.isShown()) {
            view.hide();
            needToShow = true;
        }
        view.setImageURI(imageUri);
        if (needToShow) {
            view.show();
        }
    }

    @BindingAdapter("android:src")
    public static void setImageDrawable(FloatingActionButton view, Drawable drawable) {
        Boolean needToShow = false;
        if (view.isShown()) {
            view.hide();
            needToShow = true;
        }
        view.setImageDrawable(drawable);
        if (needToShow) {
            view.show();
        }
    }

    @BindingAdapter("android:src")
    public static void setImageResource(FloatingActionButton view, int resource) {
        Boolean needToShow = false;
        if (view.isShown()) {
            view.hide();
            needToShow = true;
        }
        view.setImageResource(resource);
        if (needToShow) {
            view.show();
        }
    }
}