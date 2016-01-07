package com.dsunny.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.dsunny.engine.AppConstants;
import com.dsunny.subway.R;
import com.infrastructure.activity.BaseActivity;
import com.infrastructure.net.RequestCallback;

/**
 * Created by user on 2015/12/30.
 */
public abstract class AppBaseActivity extends BaseActivity {

    protected ProgressDialog mProgressDialog;

    public abstract class AbstractRequestCallback implements RequestCallback {
        @Override
        public void onSuccess(String content) {

        }

        @Override
        public void onFail(String errorMsg) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            new AlertDialog.Builder(mContext)
                    .setTitle("出错啦")
                    .setMessage(errorMsg)
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    public void setActionBarTitle(String title) {
        mActionBar.setTitle(title == null ? "" : title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.action_about_me:
                startAppActivity(AppConstants.ACTIVITY_ABOUT_ME);
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    protected void startAppActivity(String activity) {
        startAppActivity(activity, null);
    }

    protected void startAppActivityForResult(String activity, int requestCode) {
        startAppActivityForResult(activity, requestCode, null);
    }

    protected void startAppActivity(String activity, Intent intent) {
        try {
            Class activityClass = Class.forName(activity);
            if (intent == null) {
                startActivity(new Intent(mContext, activityClass));
            } else {
                intent.setClass(mContext, activityClass);
                startActivity(intent);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void startAppActivityForResult(String activity, int requestCode, Intent intent) {
        try {
            Class activityClass = Class.forName(activity);
            if (intent == null) {
                startActivity(new Intent(mContext, activityClass));
            } else {
                intent.setClass(mContext, activityClass);
                startActivityForResult(intent, requestCode);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
