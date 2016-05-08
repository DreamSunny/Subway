package com.dsunny.engine;

import com.dsunny.common.SubwayData;
import com.dsunny.database.bean.Transfer;
import com.dsunny.engine.base.BaseSubwayMap;
import com.dsunny.util.AppUtil;
import com.dsunny.util.SubwayUtil;
import com.infrastructure.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 从地铁图上查询两站之间换乘信息(多线路)
 * 1. 获取所有换乘线路，以此加载地铁图
 * 2. 计算换乘路径，(A->B, Line, Distance)
 * 3. 翻译换乘路径，返回查询结果
 */
public class MultiSubwayMap extends BaseSubwayMap {

    /**
     * 获取起点到终点换乘路线详细信息，例如：09-01-02
     *
     * @param lstFromToLineIds 起点终点线路集合
     * @return 起点到终点线路换乘详细集合
     */
    @Override
    protected List<String[]> getFromToTransferRouteLineIds(final List<String[]> lstFromToLineIds) {
        List<String[]> lstTransferRouteLineIds;
        if (mMinTransferTimes == 0) {
            // 起点终点在同一线路
            lstTransferRouteLineIds = new ArrayList<>();
            if (lstFromToLineIds.size() == 1) {
                final String lineId = lstFromToLineIds.get(0)[0];
                lstTransferRouteLineIds.add(new String[]{lineId});
                // 线路存在环路时特殊，考虑添加横穿线路减少乘车时间
                if (SubwayUtil.isLineExistLoop(lineId)) {
                    for (String lid : SubwayUtil.getCrossLineIds(lineId)) {
                        lstTransferRouteLineIds.add(new String[]{lineId, lid});
                    }
                }
            } else {
                // 添加每一条线路ID(from和to所在的线路ID相同)
                for (String[] fromToLineIds : lstFromToLineIds) {
                    lstTransferRouteLineIds.add(new String[]{fromToLineIds[0]});
                }
            }
        } else if (mMinTransferTimes == 1) {
            // 换乘一次，例如：9号线-1号线
            lstTransferRouteLineIds = new ArrayList<>(lstFromToLineIds);

            // 添加起点终点线路换乘一次的共有线路
            for (String[] fromToLineIds : lstFromToLineIds) {
                final int from = getIndexOfLinesTransferEdges(fromToLineIds[0]);
                final int to = getIndexOfLinesTransferEdges(fromToLineIds[1]);
                List<String> fromDirectTransferLineIds = new ArrayList<>();
                List<String> toDirectTransferLineIds = new ArrayList<>();
                for (int i = 0; i < SubwayData.LINE_EDGES.length; i++) {
                    if (SubwayData.LINE_TRANSFERS[from][i] == 1) {
                        fromDirectTransferLineIds.add(SubwayData.LINE_EDGES[i]);
                    }
                    if (SubwayData.LINE_TRANSFERS[to][i] == 1) {
                        toDirectTransferLineIds.add(SubwayData.LINE_EDGES[i]);
                    }
                }
                // 起点和终点换乘一次的共有线路
                fromDirectTransferLineIds.retainAll(toDirectTransferLineIds);
                for (String lid : fromDirectTransferLineIds) {
                    if (!lid.equals(SubwayData.LINE_99)) {
                        lstTransferRouteLineIds.add(new String[]{fromToLineIds[0], lid, fromToLineIds[1]});
                    }
                }
            }

        } else {
            // 起点终点不在同一线路
            LineMap lineMap = new LineMap();
            mMinTransferTimes += 1;
            lineMap.searchTransferRouteLineIds(lstFromToLineIds);
            lstTransferRouteLineIds = new ArrayList<>(lineMap.getTransferRouteLineIds());
        }
        return lstTransferRouteLineIds;
    }

    /**
     * 根据换乘路线信息加载换乘数据，例如：加载TRANSFER表中01,02,09号线换乘数据
     *
     * @param transferRouteLineIds 换乘路线详细信息
     * @param fromStationId        起点车站ID，取所有相同车站名的车站ID的最小值
     * @param toStationId          终点车站ID，取所有相同车站名的车站ID的最小值
     */
    @Override
    protected void createSubwayMap(final String[] transferRouteLineIds, final String fromStationId, final String toStationId) {
        // 清空临接表的数据
        if (!AppUtil.IsListEmpty(mLstHeads)) {
            mLstHeads.clear();
        }
        // 起点终点是否是普通车站及所在线路ID
        final boolean fromIsOrdinaryStation = mStationDao.isOrdinaryStation(fromStationId);
        final boolean toIsOrdinaryStation = mStationDao.isOrdinaryStation(toStationId);
        // 如果起点终点在同一线路，toStationLineId取值与fromStationLineId相同
        final String fromStationLineId = transferRouteLineIds[0];
        final String toStationLineId = mMinTransferTimes == 0 ? fromStationLineId : transferRouteLineIds[transferRouteLineIds.length - 1];

        // 如果起点终点均为普通车站，且在同一线路，且具有相同的区间(相临接的非普通车站相同)，则只加载此路线
        if (fromIsOrdinaryStation
                && toIsOrdinaryStation
                && fromStationLineId.equals(toStationLineId)
                && mLineDao.isFromToStationInSameInterval(fromStationId, toStationId)) {
            // 添加线路换乘信息
            List<Transfer> lstTransfer = mTransferDao.getLineTransfers(fromStationLineId);
            for (Transfer transfer : lstTransfer) {
                addHeadAndItems(transfer.FromSID, transfer.ToSID, transfer.LID, transfer.Distance);
                addHeadAndItems(transfer.ToSID, transfer.FromSID, transfer.LID, transfer.Distance);
            }
            // 插入相同换乘区间的车站
            if (fromStationId.compareTo(toStationId) < 0) {
                insertStationToSubwayMap(fromStationLineId.substring(0, 2), fromStationId, toStationId);
            } else {
                insertStationToSubwayMap(fromStationLineId.substring(0, 2), toStationId, fromStationId);
            }
        } else {
            // 如果存在机场线，添加机场线换乘信息
            String[] newTransferRouteLineIds = new String[transferRouteLineIds.length];
            for (int i = 0; i < transferRouteLineIds.length; i++) {
                if (transferRouteLineIds[i].equals(SubwayData.LINE_99)) {
                    // 机场线东直门至三元桥是双向的，其余为单向
                    final List<Transfer> lstTransfer = mTransferDao.getLineTransfers(SubwayData.LINE_99);
                    for (int j = 0; j < lstTransfer.size(); j++) {
                        Transfer transfer = lstTransfer.get(j);
                        if (j == 0) {
                            addHeadAndItems(transfer.FromSID, transfer.ToSID, transfer.LID, transfer.Distance);
                            addHeadAndItems(transfer.ToSID, transfer.FromSID, transfer.LID, transfer.Distance);
                        } else {
                            addHeadAndItems(transfer.FromSID, transfer.ToSID, transfer.LID, transfer.Distance);
                        }
                    }
                    // 不再查询机场线
                    newTransferRouteLineIds[i] = "";
                } else {
                    // 处理14线AB段
                    newTransferRouteLineIds[i] = transferRouteLineIds[i].substring(0, 2);
                }
            }
            // 查询其余线路换乘信息(不包括机场线)
            List<Transfer> lstTransfer = mTransferDao.getLinesTransfers(newTransferRouteLineIds);
            for (Transfer transfer : lstTransfer) {
                addHeadAndItems(transfer.FromSID, transfer.ToSID, transfer.LID, transfer.Distance);
                addHeadAndItems(transfer.ToSID, transfer.FromSID, transfer.LID, transfer.Distance);
            }
            // 如何起点线路或终点线路为八通线时，[四惠-四惠东]换车信息添加了两次，需删除一条
            if (!fromStationLineId.equals(toStationLineId)
                    && (fromStationLineId.equals(SubwayData.LINE_94) || toStationLineId.equals(SubwayData.LINE_94))) {
                delHeadAndItems(SubwayData.STATION_ID_SIHUI, SubwayData.STATION_ID_SIHUIDONG);
                delHeadAndItems(SubwayData.STATION_ID_SIHUIDONG, SubwayData.STATION_ID_SIHUI);
            }
            // 如果起点为普通车站，向地铁图插入车站
            if (fromIsOrdinaryStation) {
                LogUtil.d("insertStationToSubwayMap{" + fromStationLineId + "," + fromStationId + "}");
                insertStationToSubwayMap(fromStationLineId.substring(0, 2), fromStationId);
            }
            // 如果终点为普通车站，向地铁图插入车站
            if (toIsOrdinaryStation) {
                LogUtil.d("insertStationToSubwayMap{" + toStationLineId + "," + toStationId + "}");
                insertStationToSubwayMap(toStationLineId.substring(0, 2), toStationId);
            }
        }
    }

}
