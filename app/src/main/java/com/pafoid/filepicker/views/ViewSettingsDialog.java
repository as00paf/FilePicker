package com.pafoid.filepicker.views;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.pafoid.filepicker.R;


/**
 * Created by as00p on 2017-10-06.
 */

public class ViewSettingsDialog extends Dialog {

    public ViewSettingsDialog(@NonNull Context context) {
        super(context, R.style.Alert_Dialog_Style);

        setContentView(R.layout.dialog_view_settings);
        setTitle(R.string.view_settings);
    }


}
