package com.infrastructure.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * 自定义ProgressDialog
 */
public class SubwayProgressDialog extends ProgressDialog {
    private Activity mParentActivity;

    public SubwayProgressDialog(Context context) {
        super(context);
        mParentActivity = (Activity) context;
    }

    @Override
    public void dismiss() {
        if (mParentActivity != null && !mParentActivity.isFinishing()) {
            super.dismiss();
        }
    }
}
