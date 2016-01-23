package com.dsunny.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dsunny.Bean.TransferDetail;
import com.dsunny.Bean.TransferRoute;
import com.dsunny.Bean.TransferSubRoute;
import com.dsunny.base.AppBaseActivity;
import com.dsunny.engine.AppConstants;
import com.dsunny.subway.R;
import com.dsunny.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 换乘路线
 */
public class TransferRouteActivity extends AppBaseActivity {

    private static final String KEY_POSITION = "position";
    private static final String KEY_TRANSFER_ROUTE = "transferroute";

    private static final String FROM_TO_STATION = "%s - %s";
    private static final String TRANSFER_ROUTE = "线路%d:大约用时%d分钟,票价%d元";
    private static final String TRANSFER_ROUTE2 = "线路%d:大约用时%d分钟,换乘%d次,票价%d元";

    private static final int VIEW_TYPE_TRANSFER_STATION = 0;
    private static final int VIEW_TYPE_INTERVAL_STATION = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private ViewPager mVpTransferRoute;
    private List<TransferRoute> mTransferRoutes;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        TransferDetail transferDetail = (TransferDetail) intent.getSerializableExtra(AppConstants.KEY_TRANSFER_DETAIL);
        mTransferRoutes = transferDetail.lstTransferRoute;

        setActionBarTitle(String.format(FROM_TO_STATION, transferDetail.fromStationName, transferDetail.toStationName));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_transfer_route);

        mVpTransferRoute = findAppViewById(R.id.vp_transfer_route);
        TransferRoutePagerAdapter adapter = new TransferRoutePagerAdapter(getSupportFragmentManager(), mTransferRoutes);
        mVpTransferRoute.setAdapter(adapter);
    }

    @Override
    protected void loadData() {

    }

    private static class Item {
        public int type;// 0:换乘站,1:区间车站
        public String content;

        public Item(int type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    /**
     * Pager适配器，展示每一条线路信息
     */
    private static class TransferRoutePagerAdapter extends FragmentStatePagerAdapter {

        private List<TransferRoute> mTransferRoutes;

        public TransferRoutePagerAdapter(FragmentManager fm, List<TransferRoute> transferRoutes) {
            super(fm);
            mTransferRoutes = transferRoutes;
        }

        @Override
        public Fragment getItem(int position) {
            TransferRouteFragment fragment = new TransferRouteFragment();
            Bundle args = new Bundle();
            args.putInt(KEY_POSITION, position);
            args.putSerializable(KEY_TRANSFER_ROUTE, mTransferRoutes.get(position));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTransferRoutes != null ? mTransferRoutes.size() : 0;
        }

    }

    /**
     * 展示每一条线路Listview的适配器
     */
    private static class TransferRouteAdapter extends BaseAdapter {

        private Context mContext;
        private List<Item> mItems;

        public TransferRouteAdapter(Context context, List<Item> items) {
            mContext = context;
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems != null ? mItems.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mItems != null && position < mItems.size() ? mItems.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            return mItems.get(position).type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                switch (getItemViewType(position)) {
                    case VIEW_TYPE_TRANSFER_STATION:
                        convertView = LayoutInflater.from(mContext)
                                .inflate(R.layout.item_route_transfer_station, new RelativeLayout(mContext), true);
                        break;
                    case VIEW_TYPE_INTERVAL_STATION:
                        convertView = LayoutInflater.from(mContext)
                                .inflate(R.layout.item_route_interval_staion, new RelativeLayout(mContext), true);
                        break;
                }
            }

            Item item = mItems.get(position);
            ViewHolder viewHolder = ViewHolder.get(convertView);
            switch (getItemViewType(position)) {
                case VIEW_TYPE_TRANSFER_STATION:
                    TextView tvTransferStation = viewHolder.getView(R.id.tv_transfer_station);
                    tvTransferStation.setText(item.content);
                    break;
                case VIEW_TYPE_INTERVAL_STATION:
                    TextView tvIntervalStation = viewHolder.getView(R.id.tv_interval_station);
                    tvIntervalStation.setText(item.content);
                    break;
            }

            return convertView;
        }
    }

    /**
     * 展示每一条线路的Fragment
     */
    public static class TransferRouteFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_transfer_route, container, false);
            ListView lvTransferRoute = (ListView) rootView.findViewById(R.id.lv_transfer_route);

            // 取参数
            Bundle args = getArguments();
            int position = args.getInt(KEY_POSITION);
            TransferRoute transferRoute = (TransferRoute) args.get(KEY_TRANSFER_ROUTE);
            List<Item> items = transferRoute2ListItem(transferRoute);

            // 添加ListView的HeadView
            View headView = inflater.inflate(R.layout.item_route_transfer_route, new RelativeLayout(getActivity()), true);
            TextView tvTransferRoute = (TextView) headView.findViewById(R.id.tv_transfer_route);
            final int size = transferRoute.lstTransferSubRoute.size();
            if (size == 1) {
                tvTransferRoute.setText(String.format(TRANSFER_ROUTE, position + 1, transferRoute.elapsedTime, transferRoute.ticketPrice));
            } else {
                tvTransferRoute.setText(String.format(TRANSFER_ROUTE2, position + 1, transferRoute.elapsedTime, size - 1, transferRoute.ticketPrice));
            }
            lvTransferRoute.addHeaderView(headView);

            // 添加数据并更新
            TransferRouteAdapter adapter = new TransferRouteAdapter(getActivity(), items);
            lvTransferRoute.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            return rootView;
        }

        /**
         * 将换乘线路信息转化为List<Item>
         *
         * @param transferRoute 换乘线路信息
         * @return List<Item>
         */
        private List<Item> transferRoute2ListItem(final TransferRoute transferRoute) {
            List<Item> lstItems = new ArrayList<>();

            // 换乘站名
            lstItems.add(new Item(VIEW_TYPE_TRANSFER_STATION, transferRoute.fromStationName));
            for (TransferSubRoute tsr : transferRoute.lstTransferSubRoute) {
                // 区间车站名
                for (String stationName : tsr.lstStationNames) {
                    lstItems.add(new Item(VIEW_TYPE_INTERVAL_STATION, stationName));
                }
                // 换乘站名
                lstItems.add(new Item(VIEW_TYPE_TRANSFER_STATION, transferRoute.toStationName));
            }

            return lstItems;
        }
    }

}
