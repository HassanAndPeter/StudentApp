package com.example.android.myloginandregister.Helper;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

//help from  https://codinginflow.com/tutorials/android/datepickerdialog
public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();//get calender instance
        int year = c.get(Calendar.YEAR);//get year
        int month = c.get(Calendar.MONTH);//get month
        int day = c.get(Calendar.DAY_OF_MONTH);// get day

        // return Dialog
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}