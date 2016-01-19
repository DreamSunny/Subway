package com.dsunny.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dsunny.Bean.TransferDetail;
import com.dsunny.base.AppBaseActivity;
import com.dsunny.db.StationDao;
import com.dsunny.engine.SubwayMap;
import com.dsunny.subway.R;
import com.dsunny.utils.Utils;


/**
 * 搜索页面
 */
public class SearchActivity extends AppBaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private StationDao mStationDao;
    private SubwayMap mSubwayMap;

    private Button mBtnSelectFromStation, mBtnSelectToStation, mBtnSearch;
    private EditText mEtFromStation, mEtToStation;


    @Override
    protected void initVariables() {
        mStationDao = new StationDao();
        mSubwayMap = new SubwayMap();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);

        mBtnSelectFromStation = findAppViewById(R.id.btn_select_from_station);
        mBtnSelectFromStation.setOnClickListener(this);
        mBtnSelectToStation = findAppViewById(R.id.btn_select_to_station);
        mBtnSelectToStation.setOnClickListener(this);
        mBtnSearch = findAppViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);

        mEtFromStation = findAppViewById(R.id.et_from_station);
        mEtFromStation.setOnEditorActionListener(this);
        mEtToStation = findAppViewById(R.id.et_to_station);
        mEtToStation.setOnEditorActionListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View view) {
        Utils.closeInputMethod(this);

        switch (view.getId()) {
            case R.id.btn_select_from_station:
                break;
            case R.id.btn_select_to_station:
                break;
            case R.id.btn_search:
                searchTransferDetail();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEARCH:
                searchTransferDetail();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 验证起点终点车站是否合法
     */
    private void searchTransferDetail() {
        String fromStationName = mEtFromStation.getText().toString().trim();
        String toStationName = mEtToStation.getText().toString().trim();
        if (verifyFromToStation(fromStationName, toStationName)) {
            TransferDetail transferDetail = mSubwayMap.search(fromStationName, toStationName);
        }
    }

    /**
     * 验证起点终点站是否合法
     *
     * @param fromStationName 起点站
     * @param toStationName   终点站
     * @return true，合法；false，不合法
     */
    private boolean verifyFromToStation(String fromStationName, String toStationName) {
        if (Utils.IsStringEmpty(fromStationName)) {
            Utils.toast(mContext, "");
            return false;
        } else if (Utils.IsStringEmpty(toStationName)) {
            Utils.toast(mContext, "");
            return false;
        } else if (fromStationName.equals(toStationName)) {
            Utils.toast(mContext, "");
            return false;
        } else if (!Utils.isAlphanumeric(fromStationName) || !mStationDao.isStationExists(fromStationName)) {
            Utils.toast(mContext, "");
            return false;
        } else if (!Utils.isAlphanumeric(toStationName) || !mStationDao.isStationExists(toStationName)) {
            Utils.toast(mContext, "");
            return false;
        }
        return true;
    }

}
