package com.dsunny.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.dsunny.engine.AppConstants;
import com.dsunny.subway.R;
import com.dsunny.utils.Utils;
import com.infrastructure.activity.BaseActivity;
import com.infrastructure.cache.CacheManager;
import com.infrastructure.net.RequestCallback;
import com.umeng.analytics.MobclickAgent;

/**
 * 封装业务相关的公用逻辑
 */
public abstract class AppBaseActivity extends BaseActivity {

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 抽象回调类
     */
    protected abstract class AppRequestCallback implements RequestCallback {
        @Override
        public void onSuccess(String content) {

        }

        @Override
        public void onFail(String errorMsg) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            Utils.toast(mContext, errorMsg);
        }

        @Override
        public void onCookieExpired() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            Utils.toast(mContext, "Cookie失效");
        }
    }

    /**
     * 设置ActionBar标题
     *
     * @param title 标题名称
     */
    protected void setActionBarTitle(String title) {
        mActionBar.setTitle(title == null ? "" : title);
    }

    /**
     * 启动Activity
     *
     * @param activity 当前Activity
     */
    protected void startAppActivity(String activity) {
        startAppActivity(activity, null);
    }

    /**
     * 启动Activity
     *
     * @param activity    当前Activity
     * @param requestCode 请求Code
     */
    protected void startAppActivityForResult(String activity, int requestCode) {
        startAppActivityForResult(activity, requestCode, null);
    }

    /**
     * 启动Activity
     *
     * @param activity 当前Activity
     * @param intent   请求Intent
     */
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

    /**
     * 启动Activity
     *
     * @param activity    当前Activity
     * @param requestCode 请求Code
     * @param intent      请求Intent
     */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about_me:
                startAppActivity(AppConstants.ACTIVITY_ABOUT_ME);
                break;
            case R.id.action_clear_cache:
                CacheManager.getInstance().clearAllData();
                break;
            case R.id.action_settings:
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
