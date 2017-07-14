package com.uamre.timetracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import java.util.Calendar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * Created by uamre on 05-07-2017.
 */

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText editTextDob;

    public DateDialog(View v){
        editTextDob = (EditText)v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Dialog onCreateDialog(Bundle saveedInstanceState){
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this, year,month,date);
    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        String date= day+"-"+(month+1)+"-"+year;
        editTextDob.setText(date);
    }
}
