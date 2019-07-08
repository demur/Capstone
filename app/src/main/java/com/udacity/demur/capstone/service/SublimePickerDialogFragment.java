package com.udacity.demur.capstone.service;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.udacity.demur.capstone.MainActivity;
import com.udacity.demur.capstone.R;

public class SublimePickerDialogFragment extends DialogFragment {
    public interface Callback {
        void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute);
    }

    private Callback mCallback;

    private final SublimeListenerAdapter mListener;

    public SublimePickerDialogFragment() {
        mListener = new SublimeListenerAdapter() {
            @Override
            public void onCancelled() {
                dismiss();
            }

            @Override
            public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker,
                                                SelectedDate selectedDate,
                                                int hourOfDay, int minute,
                                                SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                                String recurrenceRule) {
                if (mCallback != null) {
                    mCallback.onDateTimeRecurrenceSet(selectedDate, hourOfDay, minute);
                }
                dismiss();
            }
        };
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SublimePicker mSublimePicker = (SublimePicker) getActivity()
                .getLayoutInflater().inflate(R.layout.sublime_picker, container);

        Bundle arguments = getArguments();
        SublimeOptions options = null;

        // Options can be null, in which case, default options are used.
        if (arguments != null) {
            options = arguments.getParcelable(MainActivity.SUBLIME_OPTIONS_KEY);
        }

        mSublimePicker.initializePicker(options, mListener);
        return mSublimePicker;
    }
}