package com.infrastructure.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

/**
 * 自定义Dialog
 */
public class SubwayDialog extends Dialog {
    private Activity mParentActivity;

    public SubwayDialog(Context context) {
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
