package com.dsunny.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dsunny.activity.base.AppBaseActivity;
import com.dsunny.activity.bean.TransferDetail;
import com.dsunny.common.SubwayData;
import com.dsunny.common.ViewHolder;
import com.dsunny.database.StationDao;
import com.dsunny.database.bean.Station;
import com.dsunny.common.AppConstants;
import com.dsunny.engine.AppHttpRequest;
import com.dsunny.engine.MultiSubwayMap;
import com.dsunny.engine.interfaces.ISubwayMap;
import com.dsunny.network.entity.Sentence;
import com.dsunny.subway.R;
import com.dsunny.util.AppUtil;
import com.dsunny.util.StringUtil;
import com.dsunny.util.ToastUtil;
import com.infrastructure.image.ImageLoader;
import com.infrastructure.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 搜索页面
 */
public class SearchActivity extends AppBaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String KEY_FROM_STATION = "from_station";
    private static final String KEY_TO_STATION = "to_station";

    private static final String MSG_FROM_STATION_EMPTY = "请输入起点站";
    private static final String MSG_TO_STATION_EMPTY = "请输入终点站";
    private static final String MSG_FROM_TO_STATION_IS_SAME = "起点站与终点站相同";
    private static final String MSG_FROM_STATION_NOT_EXIST = "起点站不可乘坐";
    private static final String MSG_TO_STATION_NOT_EXIST = "终点站不可乘坐";

    private PopupWindow mPopWin;
    private LineAdapter mLineAdapter;
    private StationAdapter mStationAdapter;
    private List<String> mLstLineNames;
    private List<String> mLstStationNames;
    private List<String> mLstStationNamesAndAbbrs;

    private StationDao mStationDao;
    private ISubwayMap mMultiSubwayMap;

    private Button mBtnSelectFromStation, mBtnSelectToStation, mBtnSearch;
    private EditText mEtFromStation, mEtToStation;

    private ImageView ivPicture;
    private TextView tvContent, tvNote;

    private boolean isWifiConnected;

    @Override
    protected void initVariables() {
        mStationDao = new StationDao();
        mMultiSubwayMap = new MultiSubwayMap();

        mLstLineNames = new ArrayList<>();
        mLstStationNames = new ArrayList<>();
        mLstStationNamesAndAbbrs = new ArrayList<>();

        // 线路名
        mLstLineNames.add(SubwayData.LINE_ALL);
        mLstLineNames.addAll(Arrays.asList(SubwayData.LINE_NAMES));

        // 车站名
        List<Station> lstStations = mStationDao.getAllStationNamesAndAbbrs();
        String preAbbr = "";
        String curAbbr;
        for (Station station : lstStations) {
            curAbbr = station.Abbr.substring(0, 1);
            if (!curAbbr.equals(preAbbr)) {
                mLstStationNamesAndAbbrs.add(curAbbr);
                preAbbr = curAbbr;
            }
            mLstStationNamesAndAbbrs.add(station.Name);
        }
        mLstStationNames.addAll(mLstStationNamesAndAbbrs);

        isWifiConnected = AppUtil.isWifiConnected(this);
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

        ivPicture = findAppViewById(R.id.iv_picture);
        tvContent = findAppViewById(R.id.tv_content);
        tvNote = findAppViewById(R.id.tv_note);

        if (savedInstanceState != null) {
            mEtFromStation.setText(savedInstanceState.getString(KEY_FROM_STATION, ""));
            mEtToStation.setText(savedInstanceState.getString(KEY_TO_STATION, ""));
        }
    }

    @Override
    protected void loadData() {
        final AppRequestCallback callback = new AppRequestCallback() {
            @Override
            public void onSuccess(String content) {
                Sentence sentence = JSON.parseObject(content, Sentence.class);
                if (sentence != null) {
                    ImageLoader.getInstance().displayImage(isWifiConnected ? sentence.getPicture2() : sentence.getPicture(), ivPicture);
                    tvContent.setText(sentence.getContent());
                    tvNote.setText(sentence.getNote());
                    LogUtil.d(sentence);
                }
            }
        };
        AppHttpRequest.getInstance().performRequest(this, "dsapi", null, callback);
    }

    @Override
    public void onClick(View view) {
        AppUtil.closeInputMethod(this);

        switch (view.getId()) {
            case R.id.btn_select_from_station:
                showPopupWindow(mBtnSelectFromStation);
                break;
            case R.id.btn_select_to_station:
                showPopupWindow(mBtnSelectToStation);
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
        outState.putString(KEY_FROM_STATION, mEtFromStation.getText().toString().trim());
        outState.putString(KEY_TO_STATION, mEtToStation.getText().toString().trim());
        super.onSaveInstanceState(outState);
    }

    /**
     * 验证起点终点车站是否合法
     */
    private void searchTransferDetail() {
        final String fromStationName = mEtFromStation.getText().toString().trim();
        final String toStationName = mEtToStation.getText().toString().trim();
        if (verifyFromToStation(fromStationName, toStationName)) {
            TransferDetail transferDetail = mMultiSubwayMap.search(fromStationName, toStationName);
            Intent intent = new Intent();
            intent.putExtra(AppConstants.KEY_TRANSFER_DETAIL, transferDetail);
            startAppActivity(AppConstants.ACTIVITY_DETAIL, intent);
        }
    }

    /**
     * 验证起点终点站是否合法
     *
     * @param fromStationName 起点站
     * @param toStationName   终点站
     * @return true，合法；false，不合法
     */
    private boolean verifyFromToStation(final String fromStationName, final String toStationName) {
        if (AppUtil.IsStringEmpty(fromStationName)) {
            ToastUtil.toast(MSG_FROM_STATION_EMPTY);
            return false;
        } else if (AppUtil.IsStringEmpty(toStationName)) {
            ToastUtil.toast(MSG_TO_STATION_EMPTY);
            return false;
        } else if (fromStationName.equals(toStationName)) {
            ToastUtil.toast(MSG_FROM_TO_STATION_IS_SAME);
            return false;
        } else if (!StringUtil.isAlphanumeric(fromStationName) || !mStationDao.isStationExists(fromStationName)) {
            ToastUtil.toast(MSG_FROM_STATION_NOT_EXIST);
            return false;
        } else if (!StringUtil.isAlphanumeric(toStationName) || !mStationDao.isStationExists(toStationName)) {
            ToastUtil.toast(MSG_TO_STATION_NOT_EXIST);
            return false;
        }
        return true;
    }


    /**
     * 选择线路车站
     *
     * @param clickButton 选择起点/终点按钮
     */
    private void showPopupWindow(final Button clickButton) {
        if (mPopWin == null || !mPopWin.isShowing()) {
            LinearLayout popwin = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.popwin_search_line_station, null);

            // 线路Adapter
            if (mLineAdapter == null) {
                mLineAdapter = new LineAdapter(mContext, mLstLineNames);
            } else {
                mLineAdapter.setSelectedPosition(0);
            }

            // 线路ListView
            ListView lvLines = (ListView) popwin.findViewById(R.id.lv_lines);
            lvLines.setAdapter(mLineAdapter);
            lvLines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != mLineAdapter.getSelectedPosition()) {
                        // 选中线路
                        mLineAdapter.setSelectedPosition(position);
                        mLineAdapter.notifyDataSetInvalidated();

                        // 更新车站列表
                        mLstStationNames.clear();
                        if (0 == position) {
                            mLstStationNames.addAll(mLstStationNamesAndAbbrs);
                        } else {
                            mLstStationNames.addAll(mStationDao.getLineAllStationNames(SubwayData.LINE_IDS[position - 1]));
                        }
                        mStationAdapter.notifyDataSetInvalidated();
                    }
                }
            });

            // 车站Adapter
            if (mStationAdapter == null) {
                mStationAdapter = new StationAdapter(mContext, mLstStationNames);
            } else {
                mLstStationNames.clear();
                mLstStationNames.addAll(mLstStationNamesAndAbbrs);
                mStationAdapter.notifyDataSetInvalidated();
            }

            // 车站ListView
            ListView lvStations = (ListView) popwin.findViewById(R.id.lv_stations);
            lvStations.setAdapter(mStationAdapter);
            lvStations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mLstStationNames.get(position).length() != 1) {
                        if (clickButton == mBtnSelectFromStation) {
                            mEtFromStation.setText(mLstStationNames.get(position));
                        } else if (clickButton == mBtnSelectToStation) {
                            mEtToStation.setText(mLstStationNames.get(position));
                        }
                        mPopWin.dismiss();
                    }
                }
            });

            final int popwinHeight = 8 * 40 + 4;// 每个item高40dp
            mPopWin = new PopupWindow(popwin, AppUtil.GetScreenWidth(), AppUtil.dp2px(popwinHeight));
            mPopWin.setBackgroundDrawable(new PaintDrawable());
            mPopWin.setFocusable(true);
            mPopWin.setOutsideTouchable(true);
            mPopWin.showAsDropDown(clickButton);
            mPopWin.update();
        }
    }

    /**
     * 线路Adapter
     */
    static class LineAdapter extends BaseAdapter {

        private Context mContext;
        private List<String> mLineNames;
        private int mSelectedPosition;

        public LineAdapter(Context context, List<String> lineNames) {
            mContext = context;
            mLineNames = lineNames;
            mSelectedPosition = 0;
        }

        @Override
        public int getCount() {
            return mLineNames != null ? mLineNames.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mLineNames != null && position < mLineNames.size() ? mLineNames.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_lines, null);
            }

            if (position == mSelectedPosition) {
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.search_popwin_line_selected));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            ViewHolder viewHolder = ViewHolder.get(convertView);
            TextView tvLine = viewHolder.getView(R.id.tv_line);
            tvLine.setText(mLineNames.get(position));

            return convertView;
        }

        public int getSelectedPosition() {
            return mSelectedPosition;
        }

        public void setSelectedPosition(final int selectedPosition) {
            this.mSelectedPosition = selectedPosition;
        }
    }

    /**
     * 车站Adapter
     */
    static class StationAdapter extends BaseAdapter {

        private Context mContext;
        private List<String> mStationNames;

        public StationAdapter(Context context, List<String> stationNames) {
            mContext = context;
            mStationNames = stationNames;
        }

        @Override
        public int getCount() {
            return mStationNames != null ? mStationNames.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mStationNames != null && position < mStationNames.size() ? mStationNames.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_stations, null);
            }

            if (mStationNames.get(position).length() == 1) {
                convertView.setBackgroundColor(Color.LTGRAY);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            ViewHolder viewHolder = ViewHolder.get(convertView);
            TextView tvLine = viewHolder.getView(R.id.tv_transfer_station);
            tvLine.setText(mStationNames.get(position));

            return convertView;
        }
    }

}
