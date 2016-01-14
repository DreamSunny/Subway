package com.dsunny.engine;

import com.dsunny.Bean.Transfer;
import com.dsunny.Bean.TransferRoute;
import com.dsunny.Bean.TransferSubRoute;
import com.dsunny.db.LineDao;
import com.dsunny.db.StationDao;
import com.dsunny.db.TransferDao;
import com.dsunny.utils.Utils;
import com.infrastructure.utils.UtilsLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 从地铁图上查询两站之间换乘信息
 * 1. 获取所有换乘线路，以此加载地铁图
 * 2. 计算换乘路径，(A->B, Line, Distance)
 * 3. 翻译换乘路径，返回查询结果
 */
public class SubwayMap {

    /**
     * 临接表的头部，指向车站(sid)能够到达的车站集合(Item)
     */
    private static class Head {
        String sid;
        List<Item> lstItems;

        public Head(String sid, List<Item> lstItems) {
            this.sid = sid;
            this.lstItems = lstItems;
        }
    }

    /**
     * 临接表的项，记录车站ID，车站所在线路，站间距离
     */
    private static class Item {
        String sid;
        String lid;
        int distance;

        public Item(String sid, String lid, int distance) {
            this.sid = sid;
            this.lid = lid;
            this.distance = distance;
        }
    }

    /**
     * 搜索换乘路线详细信息
     */
    private class LineMap {
        private Stack<Integer> mStack;// 存储当前遍历路径
        private List<String[]> mLstTransferRouteLineIds;// 14号线分为AB段
        private boolean[] isVisited;// 表示线路是否被访问过

        public LineMap() {
            mStack = new Stack<>();
            mLstTransferRouteLineIds = new ArrayList<>();
            isVisited = new boolean[SubwayData.LINES_TRANSFER_EDGES.length];
        }

        /**
         * 换乘路线详细信息集合，例如：9号线-1号线-2号线
         *
         * @return 换乘路线详细信息
         */
        public List<String[]> getTransferRouteLineIds() {
            return mLstTransferRouteLineIds;
        }

        /**
         * 搜索换乘路线详细信息
         *
         * @param lstFromToLineIds 起点终点线路集合
         */
        public void searchTransferRouteLineIds(final List<String[]> lstFromToLineIds) {
            for (String[] lids : lstFromToLineIds) {
                final int from = getIndexOfLinesTransferEdges(lids[0]);
                final int to = getIndexOfLinesTransferEdges(lids[1]);
                DFS(from, to);
            }
        }

        /**
         * 深度优先搜索
         *
         * @param from 起点线路在LINES_TRANSFER_EDGES数组的下标，14号线分为AB段
         * @param to   终点线路在LINES_TRANSFER_EDGES数组的下标，14号线分为AB段
         */
        private void DFS(final int from, final int to) {
            if (SubwayData.LINES_TRANSFER[from][to] == 1) {
                int i = 0;
                String[] lineIds = new String[mStack.size() + 2];
                for (int index : mStack) {
                    lineIds[i++] = SubwayData.LINES_TRANSFER_EDGES[index];
                }
                lineIds[i++] = SubwayData.LINES_TRANSFER_EDGES[from];
                lineIds[i] = SubwayData.LINES_TRANSFER_EDGES[to];
                mLstTransferRouteLineIds.add(lineIds);
            } else {
                mStack.push(from);
                isVisited[from] = true;
                for (int i = 0; i < mMinTransferTimes; i++) {
                    if (!isVisited[i] && SubwayData.LINES_TRANSFER[from][i] == 1 && mStack.size() < mMinTransferTimes) {
                        DFS(i, to);
                    }
                }
                isVisited[from] = false;
                mStack.pop();
            }
        }
    }

    /**
     * 临接表头部的集合
     */
    private List<Head> mLstHeads;

    private LineDao mLineDao;// 查询线路相关信息
    private StationDao mStationDao;// 查询车站相关信息
    private TransferDao mTransferDao;// 查询换乘数据信息

    private String mFromStationName;// 起点车站名
    private String mToStationName;// 终点车站名
    private int mMinTransferTimes;// 起始到终点线路的最少换乘次数

    /**
     * 换乘路线详细信息
     */
    public SubwayMap() {
        mLstHeads = new ArrayList<>();
        mLineDao = new LineDao();
        mStationDao = new StationDao();
        mTransferDao = new TransferDao();
    }

    /**
     * 查询换乘详情，例如：丰台科技园-北京站
     *
     * @param fromStationName 起点车站名
     * @param toStationName   终点车站名
     * @return 换乘详情
     */
    public TransferRoute search(final String fromStationName, final String toStationName) {
        mFromStationName = fromStationName;
        mToStationName = toStationName;

        // 起点车站名对应的车站ID集合
        List<String> lstFromStationIds = mStationDao.getStationIdsByStationName(fromStationName);
        UtilsLog.d(lstFromStationIds);
        // 终点车站名对应的车站ID集合
        List<String> lstToStationIds = mStationDao.getStationIdsByStationName(toStationName);
        UtilsLog.d(lstToStationIds);
        // 起点终点线路集合
        List<String[]> lstFromToLineIds = getFromToLineIds(lstFromStationIds, lstToStationIds);
        UtilsLog.d(lstFromToLineIds);
        // 起点到终点换乘路线详细信息
        List<String[]> lstTransferRouteLineIds = getFromToTransferRouteLineIds(lstFromToLineIds);
        UtilsLog.d(lstTransferRouteLineIds);
        // 遍历所有起点到终点换乘路线详细信息，搜索换乘信息
        TransferRoute transferRoute = new TransferRoute();
        for (String[] lids : lstTransferRouteLineIds) {
            createSubwayMap(lids, lstFromStationIds.get(0), lstToStationIds.get(0));
            searchTransferRoute(lstFromStationIds.get(0), lstToStationIds.get(0));
        }

        return generateTransferDetail(transferRoute);
    }

    /**
     * 获取起点终点线路集合，换乘站包含多条线路结果，14号线分为AB段，例如：09-02
     *
     * @param lstFromSids 起点车站ID集合
     * @param lstToSids   终点车站ID集合
     * @return 线路集合
     */
    private List<String[]> getFromToLineIds(final List<String> lstFromSids, final List<String> lstToSids) {
        List<String> lstFromLines = getLineIdsFromStationIds(lstFromSids);// 起点线路集合
        List<String> listToLines = getLineIdsFromStationIds(lstToSids);// 终点线路集合

        mMinTransferTimes = SubwayData.LINES_NAME.length;// 线路i到j的最小换乘次数
        List<String[]> lstFromToLines = new ArrayList<>();// 起点终点是最少换乘的线路集合
        for (String from : lstFromLines) {
            final int i = getIndexOfLinesTransferEdges(from);
            for (String to : listToLines) {
                final int j = getIndexOfLinesTransferEdges(to);
                if (SubwayData.LINES_TRANSFER[i][j] < mMinTransferTimes) {
                    lstFromToLines.clear();
                    lstFromToLines.add(new String[]{from, to});
                    mMinTransferTimes = SubwayData.LINES_TRANSFER[i][j];
                } else if (SubwayData.LINES_TRANSFER[i][j] == mMinTransferTimes) {
                    lstFromToLines.add(new String[]{from, to});
                }
            }
        }

        return lstFromToLines;
    }

    /**
     * 根据车站ID获取线路ID，14号线分为AB段
     *
     * @param lstSids 车站ID集合
     * @return 线路集合
     */
    private List<String> getLineIdsFromStationIds(final List<String> lstSids) {
        List<String> lstLines = new ArrayList<>();
        for (String sid : lstSids) {
            final String lid = sid.substring(0, 2);
            if (SubwayData.LINE_14.equals(lid)) {
                if (sid.compareTo(SubwayData.ID_LINE_14A[1]) <= 0) {
                    lstLines.add(SubwayData.LINE_14A);
                } else if (sid.compareTo(SubwayData.ID_LINE_14B[0]) >= 0) {
                    lstLines.add(SubwayData.LINE_14B);
                } else {
                    lstLines.add(SubwayData.LINE_14);
                }
            } else {
                lstLines.add(lid);
            }
        }
        return lstLines;
    }

    /**
     * 获取线路ID的LINES_TRANSFER_EDGES数组下标，14号线分为AB段
     *
     * @param lid 线路ID
     * @return 数组下标，即线路在LINES_TRANSFER数组的下标
     */
    private int getIndexOfLinesTransferEdges(final String lid) {
        for (int i = 0; i < SubwayData.LINES_TRANSFER_EDGES.length; i++) {
            if (SubwayData.LINES_TRANSFER_EDGES[i].equals(lid)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取起点到终点换乘路线详细信息，例如：09-01-02
     *
     * @param lstFromToLineIds 起点终点线路集合
     * @return 起点到终点线路换乘详细集合
     */
    private List<String[]> getFromToTransferRouteLineIds(final List<String[]> lstFromToLineIds) {
        List<String[]> lstTransferRouteLineIds;
        if (mMinTransferTimes == 0) {
            // 起点终点在同一线路
            lstTransferRouteLineIds = new ArrayList<>();
            if (lstFromToLineIds.size() == 1) {
                String lineId = lstFromToLineIds.get(0)[0];
                lstTransferRouteLineIds.add(new String[]{lineId});
                // 机场线特殊
                if (SubwayData.LINE_99.equals(lineId)) {
                    // 特殊(东直门-三元桥)
                    if ((SubwayData.STATION_DONGZHIMEN.equals(mFromStationName)
                            && SubwayData.STATION_SANYUANQIAO.equals(mToStationName))
                            || (SubwayData.STATION_DONGZHIMEN.equals(mToStationName)
                            && SubwayData.STATION_SANYUANQIAO.equals(mFromStationName))) {
                        lstTransferRouteLineIds.add(new String[]{SubwayData.LINE_10, SubwayData.LINE_13});
                    }
                }
                // 线路存在环路时特殊，考虑添加横穿线路减少乘车时间
                if (mLineDao.isLineExistLoop(lineId)) {
                    for (String lid : mLineDao.getCrossLineIds(lineId)) {
                        lstTransferRouteLineIds.add(new String[]{lineId, lid});
                    }
                }
            } else {
                // 特殊(四惠-四惠东)
                lstTransferRouteLineIds.add(new String[]{SubwayData.LINE_01});
                lstTransferRouteLineIds.add(new String[]{SubwayData.LINE_94});
            }
        } else if (mMinTransferTimes == 1) {
            // 直达，例如：9号线-1号线
            lstTransferRouteLineIds = new ArrayList<>(lstFromToLineIds);
        } else {
            // 起点终点不在同一线路
            LineMap lineMap = new LineMap();
            lineMap.searchTransferRouteLineIds(lstFromToLineIds);
            lstTransferRouteLineIds = new ArrayList<>(lineMap.getTransferRouteLineIds());
        }
        return lstTransferRouteLineIds;
    }

    /**
     * 根据换乘路线信息加载换乘数据，例如：加载TRANSFER表中01,02,09号线换乘数据
     *
     * @param transferRouteLineIds 换乘路线详细信息
     * @param fromSid              起点车站ID，取所有相同车站名的车站ID的最小值
     * @param toSid                终点车站ID，取所有相同车站名的车站ID的最小值
     */
    private void createSubwayMap(final String[] transferRouteLineIds, final String fromSid, final String toSid) {
        // 清空临接表的数据
        if (!Utils.IsListEmpty(mLstHeads)) {
            mLstHeads.clear();
        }
        // 起点终点是否是普通车站及所在线路ID
        final boolean isFromStationisOrdinary = mStationDao.isOrdinaryStation(fromSid);
        final boolean isToStationisOrdinary = mStationDao.isOrdinaryStation(toSid);
        final String fromStationLineId = fromSid.substring(0, 2);
        final String toStationLineId = toSid.substring(0, 2);
        // 如果起点终点均为普通车站，且在同一线路，且具有相同的区间(相临接的非普通车站相同)，则只加载此路线
        if (isFromStationisOrdinary
                && isToStationisOrdinary
                && fromStationLineId.equals(toStationLineId)
                && mLineDao.isFromToStationInSameInterval(fromSid, toSid)) {
            final List<Transfer> lstTransfer = mTransferDao.getLineTransfers(fromStationLineId);
            for (Transfer transfer : lstTransfer) {
                addHeadAndItems(transfer.FromSID, transfer.ToSID, transfer.LID, transfer.Distance);
                addHeadAndItems(transfer.ToSID, transfer.FromSID, transfer.LID, transfer.Distance);
            }
            return;
        }
        // 如果存在机场线，添加机场线换乘信息
        String[] newTransferRouteLineIds = new String[transferRouteLineIds.length];
        for (int i = 0; i < transferRouteLineIds.length; i++) {
            if (SubwayData.LINE_99.equals(transferRouteLineIds[i])) {
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
        final List<Transfer> lstTransfer = mTransferDao.getLinesTransfers(newTransferRouteLineIds);
        for (Transfer transfer : lstTransfer) {
            addHeadAndItems(transfer.FromSID, transfer.ToSID, transfer.LID, transfer.Distance);
            addHeadAndItems(transfer.ToSID, transfer.FromSID, transfer.LID, transfer.Distance);
        }
        // 如果起点为普通车站，向地铁图插入车站
        if (isFromStationisOrdinary) {
            insertStationToSubwayMap(fromStationLineId, fromSid);
        }
        // 如果终点为普通车站，向地铁图插入车站
        if (isToStationisOrdinary) {
            insertStationToSubwayMap(toStationLineId, toSid);
        }
    }

    /**
     * 地铁图中以临接表形式保存换乘信息，例如：0908(七里庄)，0913(郭公庄车)，09，6025
     *
     * @param fromStationId Head部，记录图中所有车站
     * @param toStationId   Item部，记录某一车站能够到达的所有车站
     * @param lineId        Head->Item所在线路ID
     * @param distance      Head->Item之间的距离
     */
    private void addHeadAndItems(final String fromStationId, final String toStationId, final String lineId, final int distance) {
        Item item = new Item(toStationId, lineId, distance);
        for (Head head : mLstHeads) {
            if (head.sid.equals(fromStationId)) {
                head.lstItems.add(item);
                return;
            }
        }
        // 如果Head中不存在fromStationId，则创建并添加到mLstHeads中
        Head head = new Head(fromStationId, new ArrayList<Item>());
        head.lstItems.add(item);
        mLstHeads.add(head);
    }

    /**
     * 删除Head->Item，例如：0908(七里庄)-0913(郭公庄车)
     *
     * @param fromStationId Head部
     * @param toStationId   Item部
     */
    private void delHeadAndItems(final String fromStationId, final String toStationId) {
        for (Head head : mLstHeads) {
            if (head.sid.equals(fromStationId)) {
                for (Item item : head.lstItems) {
                    if (item.sid.equals(toStationId)) {
                        head.lstItems.remove(item);
                        if (head.lstItems.size() == 0) {
                            mLstHeads.remove(head);
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * 向换乘区间插入新车站，例如：向[七里庄,郭公庄]插入丰台科技园，则变为[七里庄,丰台科技园]，[丰台科技园,郭公庄]
     *
     * @param lid 插入的车站线路ID
     * @param sid 插入的车站ID
     */
    private void insertStationToSubwayMap(final String lid, final String sid) {
        // 获取普通车站的临接车站，List长度为2，表示[from,to]
        final List<String> lstAdjacentStationIds = mLineDao.getAdjacentStationIds(sid);
        // 获取所有与from同名的车站ID
        final List<String> lstFromStationIds = mStationDao.getStationIdsByStationId(lstAdjacentStationIds.get(0));
        // 获取所有与to同名的车站ID
        final List<String> lstToStationIds = mStationDao.getStationIdsByStationId(lstAdjacentStationIds.get(1));

        // 获取[from,to]车站ID的最小值，即SubwayMap中的车站ID
        String minFromStationId = lstFromStationIds.get(0);
        String minToStationId = lstToStationIds.get(0);
        // 双向删除[from,to]
        delHeadAndItems(minFromStationId, minToStationId);
        delHeadAndItems(minToStationId, minFromStationId);

        // 获取from所在lid的车站ID，用于计算站间距离
        String fromLineStationId = "";
        for (String id : lstFromStationIds) {
            if (lid.equals(id.substring(0, 2))) {
                fromLineStationId = id;
            }
        }
        // 获取[from,insert]的站间距离
        int fromInsertDistance = mLineDao.getIntervalDistance(fromLineStationId, sid);
        // 双向添加[from,insert]
        addHeadAndItems(sid, minFromStationId, lid, fromInsertDistance);
        addHeadAndItems(minFromStationId, sid, lid, fromInsertDistance);

        // 获取to所在lid的车站ID，用于计算站间距离
        String toLineStationId = "";
        for (String id : lstToStationIds) {
            if (lid.equals(id.substring(0, 2))) {
                toLineStationId = id;
            }
        }
        // 获取[insert,to]的站间距离
        int insertToDistance = mLineDao.getIntervalDistance(toLineStationId, sid);
        // 双向添加[insert,to]
        addHeadAndItems(minFromStationId, sid, lid, insertToDistance);
        addHeadAndItems(sid, minFromStationId, lid, insertToDistance);
    }

    /**
     * 从抽象图中搜索最短路径，车站ID为所有ID的最小值，例如：1号线军事博物馆站(0109)，9号线军事博物馆站(0904)，则传0109
     *
     * @param fromSid 起点车站ID
     * @param toSid   终点车站ID
     * @return ？？？
     */
    private List<TransferSubRoute> searchTransferRoute(final String fromSid, final String toSid) {
        int size = mLstHeads.size();// SubwayMap中结点总数，即车站总数
        int[] distance = new int[size];// From车站到各车站的距离
        int[] previous = new int[size];// 各车站的前驱车站索引值
        boolean[] visited = new boolean[size];// From车站是否已遍历各车站

        // from车站在mLstHeads的索引值
        int fromStationIndex = 0;
        for (Head head : mLstHeads) {
            if (head.sid.equals(fromSid)) {
                break;
            }
            fromStationIndex++;
        }
        // to车站在mLstHeads的索引值
        int toStationIndex = 0;
        for (Head head : mLstHeads) {
            if (head.sid.equals(toSid)) {
                break;
            }
            toStationIndex++;
        }

        // 初始化From指各车站之间的距离，每一个车站的前驱车站下标，默认各车站均未访问
        for (int i = 0; i < size; i++) {
            distance[i] = getFromToDistanceByIndex(fromStationIndex, i);
            previous[i] = fromStationIndex;
            visited[i] = false;
        }
        previous[fromStationIndex] = -1;
        visited[fromStationIndex] = true;

        // 迪杰斯特拉算法，14号分为AB段，任意两点可达
        while (!visited[toStationIndex]) {
            int current = -1;
            // 寻找当前最小的路径
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                if (!visited[i] && distance[i] < min) {
                    min = distance[i];
                    current = i;
                }
            }
            // 标记已获取到最短路径
            visited[current] = true;
            // 修正当前最短路径和前驱结点
            for (int i = 0; i < size; i++) {
                int tmp = getFromToDistanceByIndex(current, i);
                if (tmp != Integer.MAX_VALUE) {
                    tmp += min;
                }
                if (!visited[i] && tmp < distance[i]) {
                    distance[i] = tmp;
                    previous[i] = current;
                }
            }
        }

        return null;
    }

    /**
     * 获取From-To站间距离，From在mLstHeads列表，To在Head的lstItems列表
     *
     * @param fromIndex From在mLstHeads列表的索引
     * @param toIndex   To在lstItems列表的索引
     * @return From-To站间距离，如果From-To不可达，则返回Integer.MAX_VALUE
     */
    private int getFromToDistanceByIndex(final int fromIndex, final int toIndex) {
        Head head = mLstHeads.get(fromIndex);
        if (head != null) {
            Item item = head.lstItems.get(toIndex);
            if (item != null) {
                return item.distance;
            }
        }
        return Integer.MAX_VALUE;
    }


    /**
     * @param transferDetail
     * @return
     */
    private TransferRoute generateTransferDetail(TransferRoute transferDetail) {
        transferDetail.fromStationName = mFromStationName;
        transferDetail.toStationName = mToStationName;
        return transferDetail;
    }

}
