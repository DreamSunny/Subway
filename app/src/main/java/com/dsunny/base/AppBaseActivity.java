package com.dsunny.base;

import android.view.Menu;
import android.view.MenuItem;

import com.dsunny.subway.R;
import com.infrastructure.activity.BaseActivity;

/**
 * Created by user on 2015/12/30.
 */
public abstract class AppBaseActivity extends BaseActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
