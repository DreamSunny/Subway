package com.dsunny.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dsunny.activity.bean.TransferDetail;
import com.dsunny.activity.bean.TransferRoute;
import com.dsunny.activity.bean.TransferSubRoute;
import com.dsunny.activity.base.AppBaseActivity;
import com.dsunny.engine.AppConstants;
import com.dsunny.subway.R;
import com.dsunny.util.Util;
import com.dsunny.common.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果展示页面
 */
public class TransferDetailActivity extends AppBaseActivity {

    private static final String FROM_TO_STATION = "%s - %s";
    private static final String TRANSFER_ROUTE = "线路%d:大约用时%d分钟,票价%d元";
    private static final String TRANSFER_ROUTE2 = "线路%d:大约用时%d分钟,换乘%d次,票价%d元";
    private static final String TRANSFER_DIRECTION = "换乘%s,%s方向";
    private static final String TRANSFER_DIRECTION2 = "换乘%s,%s方向,途径%d站";

    private static final int VIEW_TYPE_TRANSFER_ROUTE = 0;
    private static final int VIEW_TYPE_TRANSFER_STATION = 1;
    private static final int VIEW_TYPE_TRANSFER_DIRECTION = 2;
    private static final int VIEW_TYPE_COUNT = 3;

    private ListView lvTransferDetail;
    private TransferDetail mTransferDetail;
    private List<Item> mItems;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        mTransferDetail = (TransferDetail) intent.getSerializableExtra(AppConstants.KEY_TRANSFER_DETAIL);
        mItems = transferDetail2ListItem(mTransferDetail);

        setActionBarTitle(String.format(FROM_TO_STATION, mTransferDetail.fromStationName, mTransferDetail.toStationName));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_transfer_detail);

        lvTransferDetail = findAppViewById(R.id.lv_transfer_detail);
        TransferDetailAdapter adapter = new TransferDetailAdapter(mContext, mItems);
        lvTransferDetail.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        lvTransferDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mItems != null && position < mItems.size()) {
                    final Item item = mItems.get(position);
                    switch (item.type) {
                        case VIEW_TYPE_TRANSFER_ROUTE:
                            Intent intent = new Intent();
                            intent.putExtra(AppConstants.KEY_TRANSFER_DETAIL, mTransferDetail);
                            final String transferRouteNumber = Util.getFirstNumberInString(mItems.get(position).content);
                            intent.putExtra(AppConstants.KEY_TRANSFER_ROUTE_NUMBER, Util.string2Int(transferRouteNumber));
                            startAppActivity(AppConstants.ACTIVITY_TRANSFER_ROUTE, intent);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void loadData() {

    }

    /**
     * 将TransferDetail转换为List<Item>
     *
     * @param transferDetail 换乘详情
     * @return List<Item>
     */
    private List<Item> transferDetail2ListItem(final TransferDetail transferDetail) {
        int transferRouteNumber = 1;
        List<Item> lstItems = new ArrayList<>();
        for (TransferRoute td : transferDetail.lstTransferRoute) {
            // 线路X
            final int size = td.lstTransferSubRoute.size();
            if (size == 1) {
                lstItems.add(new Item(VIEW_TYPE_TRANSFER_ROUTE, String.format(TRANSFER_ROUTE, transferRouteNumber++, td.elapsedTime, td.ticketPrice)));
            } else {
                lstItems.add(new Item(VIEW_TYPE_TRANSFER_ROUTE, String.format(TRANSFER_ROUTE2, transferRouteNumber++, td.elapsedTime, size - 1, td.ticketPrice)));
            }
            // 换乘站名
            lstItems.add(new Item(VIEW_TYPE_TRANSFER_STATION, td.lstTransferSubRoute.get(0).fromStationName));
            for (TransferSubRoute tsr : td.lstTransferSubRoute) {
                // 换乘方向
                final int size2 = tsr.lstStationNames.size();
                if (size2 == 0) {
                    lstItems.add(new Item(VIEW_TYPE_TRANSFER_DIRECTION, String.format(TRANSFER_DIRECTION, tsr.lineName, tsr.transferDirection)));
                } else {
                    lstItems.add(new Item(VIEW_TYPE_TRANSFER_DIRECTION, String.format(TRANSFER_DIRECTION2, tsr.lineName, tsr.transferDirection, size2)));
                }
                // 换乘站名
                lstItems.add(new Item(VIEW_TYPE_TRANSFER_STATION, tsr.toStationName));
            }
        }

        return lstItems;
    }

    private static class Item {
        public int type;// 0:换乘路线,1:换乘站,2:换乘方向
        public String content;

        public Item(int type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    /**
     * 换乘详情适配器
     */
    private static class TransferDetailAdapter extends BaseAdapter {

        private Context mContext;
        private List<Item> mItems;

        public TransferDetailAdapter(Context context, List<Item> items) {
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
                    case VIEW_TYPE_TRANSFER_ROUTE:
                        convertView = LayoutInflater.from(mContext)
                                .inflate(R.layout.item_detail_transfer_route, parent, false);
                        break;
                    case VIEW_TYPE_TRANSFER_STATION:
                        convertView = LayoutInflater.from(mContext)
                                .inflate(R.layout.item_detail_transfer_station, parent, false);
                        break;
                    case VIEW_TYPE_TRANSFER_DIRECTION:
                        convertView = LayoutInflater.from(mContext)
                                .inflate(R.layout.item_detail_transfer_direction, parent, false);
                        break;
                }
            }

            final Item item = mItems.get(position);
            final ViewHolder viewHolder = ViewHolder.get(convertView);
            switch (getItemViewType(position)) {
                case VIEW_TYPE_TRANSFER_ROUTE:
                    TextView tvTransferRoute = viewHolder.getView(R.id.tv_transfer_route);
                    tvTransferRoute.setText(item.content);
                    break;
                case VIEW_TYPE_TRANSFER_STATION:
                    TextView tvTransferStation = viewHolder.getView(R.id.tv_transfer_station);
                    tvTransferStation.setText(item.content);
                    break;
                case VIEW_TYPE_TRANSFER_DIRECTION:
                    TextView tvTransferDirection = viewHolder.getView(R.id.tv_transfer_direction);
                    tvTransferDirection.setText(item.content);
                    break;
            }

            return convertView;
        }
    }

}
