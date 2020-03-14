package com.infocoil.flamingo.activity.helper_fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;

import com.infocoil.flamingo.R;
import com.infocoil.flamingo.activity.MyView;

public class CustomTimePickerDialog extends AlertDialog implements OnClickListener {

    private onTimeChanged listener;
    private MyCustomTimePicker mTimePicker;



    protected CustomTimePickerDialog(Context context, onTimeChanged listener) {
        super(context);
        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.activity_dialog, null);

        this.listener = listener;

        setView(view);

        setButton(BUTTON_POSITIVE, "انتخاب", this);
        setButton(BUTTON_NEGATIVE, "رد", this);

        mTimePicker = view.findViewById(R.id.MyCustomTimePicker);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (listener != null) {
                    listener.onTimeChanged(mTimePicker, mTimePicker.getH(),
                            mTimePicker.getMinute());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }
}