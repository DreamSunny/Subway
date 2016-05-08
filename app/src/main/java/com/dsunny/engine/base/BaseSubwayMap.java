package com.dsunny.engine.base;

import com.dsunny.activity.bean.TransferDetail;
import com.dsunny.activity.bean.TransferRoute;
import com.dsunny.activity.bean.TransferSubRoute;
import com.dsunny.common.SubwayData;
import com.dsunny.database.LineDao;
import com.dsunny.database.StationDao;
import com.dsunny.database.TransferDao;
import com.dsunny.engine.interfaces.ISubwayMap;
import com.dsunny.util.AppUtil;
import com.dsunny.util.SubwayUtil;
import com.infrastructure.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 抽象地铁图基类
 */
public abstract class BaseSubwayMap implements ISubwayMap {

    /**
     * 临接表头部的集合
     */
    protected List<Head> mLstHeads;

    protected LineDao mLineDao;// 查询线路相关信息
    protected StationDao mStationDao;// 查询车站相关信息
    protected TransferDao mTransferDao;// 查询换乘数据信息

    protected String mFromStationName;// 起点车站名
    protected String mToStationName;// 终点车站名

    protected int mMinTransferTimes;// 起始到终点线路的最少换乘次数

    public BaseSubwayMap() {
        mLstHeads = new ArrayList<>();
        mLineDao = new LineDao();
        mStationDao = new StationDao();
        mTransferDao = new TransferDao();
    }

    /**
     * 临接表的头部，指向车站(sid)能够到达的车站集合(Item)
     */
    protected static class Head {
        public String sid;
        public List<Item> lstItems;

        public Head(String sid) {
            this.sid = sid;
            this.lstItems = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "Head{" +
                    "sid='" + sid + '\'' +
                    ", lstItems=" + lstItems +
                    '}';
        }
    }

    /**
     * 临接表的项，记录车站ID，车站所在线路，站间距离
     */
    protected static class Item {
        public String sid;
        public String lid;
        public int distance;

        public Item(String sid, String lid, int distance) {
            this.sid = sid;
            this.lid = lid;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "sid='" + sid + '\'' +
                    ", lid='" + lid + '\'' +
                    ", distance=" + distance +
                    '}';
        }
    }

    /**
     * 搜索换乘路线详细信息
     */
    protected class LineMap {
        public Stack<Integer> mStack;// 存储当前遍历路径
        public List<String[]> mLstTransferRouteLineIds;// 14号线分为AB段
        public boolean[] isVisited;// 表示线路是否被访问过

        public LineMap() {
            mStack = new Stack<>();
            mLstTransferRouteLineIds = new ArrayList<>();
            isVisited = new boolean[SubwayData.LINE_EDGES.length];
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
                int from = getIndexOfLinesTransferEdges(lids[0]);
                int to = getIndexOfLinesTransferEdges(lids[1]);
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
            if (SubwayData.LINE_TRANSFERS[from][to] == 1) {
                int i = 0;
                String[] lineIds = new String[mStack.size() + 2];
                for (int index : mStack) {
                    lineIds[i++] = SubwayData.LINE_EDGES[index];
                }
                lineIds[i++] = SubwayData.LINE_EDGES[from];
                lineIds[i] = SubwayData.LINE_EDGES[to];
                mLstTransferRouteLineIds.add(lineIds);
            } else {
                mStack.push(from);
                isVisited[from] = true;
                for (int i = 0; i < SubwayData.LINE_EDGES.length; i++) {
                    if (!isVisited[i]
                            && (i != SubwayData.LINE_EDGES.length - 1)
                            && SubwayData.LINE_TRANSFERS[from][i] == 1
                            && mStack.size() < mMinTransferTimes) {
                        DFS(i, to);
                    }
                }
                isVisited[from] = false;
                mStack.pop();
            }
        }
    }

    /**
     * @param fromStationName 起点站
     * @param toStationName   终点站
     * @return 换乘结果
     */
    @Override
    public TransferDetail search(String fromStationName, String toStationName) {
        mFromStationName = fromStationName;
        mToStationName = toStationName;

        // 起点车站名对应的车站ID集合
        List<String> lstFromStationIds = mStationDao.getStationIdsByStationName(fromStationName);
        LogUtil.d("lstFromStationIds = " + lstFromStationIds);

        // 终点车站名对应的车站ID集合
        List<String> lstToStationIds = mStationDao.getStationIdsByStationName(toStationName);
        LogUtil.d("lstToStationIds = " + lstToStationIds);

        // 起点终点线路集合
        List<String[]> lstFromToLineIds = getFromToLineIds(lstFromStationIds, lstToStationIds);
        if (LogUtil.DEBUG) {
            LogUtil.d("lstFromToLineIds = " + AppUtil.ListArrayAsString(lstFromToLineIds));
        }

        // 起点到终点换乘路线详细信息
        List<String[]> lstTransferRouteLineIds = getFromToTransferRouteLineIds(lstFromToLineIds);
        if (LogUtil.DEBUG) {
            LogUtil.d("lstTransferRouteLineIds = " + AppUtil.ListArrayAsString(lstTransferRouteLineIds));
        }

        // 遍历所有起点到终点换乘路线详细信息，搜索换乘信息
        TransferDetail transferDetail = new TransferDetail();
        transferDetail.fromStationName = mFromStationName;
        transferDetail.toStationName = mToStationName;
        transferDetail.lstTransferRoute = new ArrayList<>();
        for (String[] lids : lstTransferRouteLineIds) {
            // 构建临接表建图
            createSubwayMap(lids, lstFromStationIds.get(0), lstToStationIds.get(0));
            LogUtil.d("mLstHeads = " + mLstHeads);
            // 从图中搜索两点之间最短距离
            TransferRoute transferRoute = searchTransferRoute(lstFromStationIds.get(0), lstToStationIds.get(0));
            LogUtil.d(transferRoute);
            // 添加换乘路线
            updateTransferDetail(transferDetail, transferRoute);
            LogUtil.d(transferDetail);
        }

        LogUtil.d("result = " + transferDetail);
        return transferDetail;
    }

    /**
     * 获取起点终点线路集合，换乘站包含多条线路结果，14号线分为AB段，例如：09-02
     *
     * @param lstFromStationIds 起点车站ID集合
     * @param lstToStationIds   终点车站ID集合
     * @return 线路集合
     */
    protected List<String[]> getFromToLineIds(final List<String> lstFromStationIds, final List<String> lstToStationIds) {
        boolean isAddAirportLine = false;
        // 起点车站是T2或T3航站楼时加载机场线
        for (String sid : lstFromStationIds) {
            if (sid.equals(SubwayData.STATION_ID_T2) || sid.equals(SubwayData.STATION_ID_T3)) {
                isAddAirportLine = true;
                break;
            }
        }
        // 终点车站是T2或T3航站楼时加载机场线
        for (String sid : lstToStationIds) {
            if (sid.equals(SubwayData.STATION_ID_T2) || sid.equals(SubwayData.STATION_ID_T3)) {
                isAddAirportLine = true;
                break;
            }
        }

        List<String> lstFromLineIds = getLineIdsFromStationIds(lstFromStationIds);// 起点线路集合
        List<String> lstToLineIds = getLineIdsFromStationIds(lstToStationIds);// 终点线路集合

        mMinTransferTimes = SubwayData.LINE_MAX_TRANSFER_TIMES;// 线路i到j的最小换乘次数
        List<String[]> lstFromToLineIds = new ArrayList<>();// 起点终点是最少换乘的线路集合
        for (String fromLineId : lstFromLineIds) {
            if (fromLineId.equals(SubwayData.LINE_99) && !isAddAirportLine) {
                continue;
            }
            final int i = getIndexOfLinesTransferEdges(fromLineId);
            for (String toLineId : lstToLineIds) {
                if (toLineId.equals(SubwayData.LINE_99) && !isAddAirportLine) {
                    continue;
                }
                final int j = getIndexOfLinesTransferEdges(toLineId);
                if (SubwayData.LINE_TRANSFERS[i][j] < mMinTransferTimes) {
                    lstFromToLineIds.clear();
                    lstFromToLineIds.add(new String[]{fromLineId, toLineId});
                    mMinTransferTimes = SubwayData.LINE_TRANSFERS[i][j];
                } else if (SubwayData.LINE_TRANSFERS[i][j] == mMinTransferTimes) {
                    lstFromToLineIds.add(new String[]{fromLineId, toLineId});
                }
            }
        }

        return lstFromToLineIds;
    }

    /**
     * 获取起点到终点换乘路线详细信息，例如：09-01-02
     *
     * @param lstFromToLineIds 起点终点线路集合
     * @return 起点到终点线路换乘详细集合
     */
    protected abstract List<String[]> getFromToTransferRouteLineIds(final List<String[]> lstFromToLineIds);

    /**
     * 根据换乘路线信息加载换乘数据，例如：加载TRANSFER表中01,02,09号线换乘数据
     *
     * @param transferRouteLineIds 换乘路线详细信息
     * @param fromStationId        起点车站ID，取所有相同车站名的车站ID的最小值
     * @param toStationId          终点车站ID，取所有相同车站名的车站ID的最小值
     */
    protected abstract void createSubwayMap(final String[] transferRouteLineIds, final String fromStationId, final String toStationId);

    /**
     * 获取线路ID的LINES_TRANSFER_EDGES数组下标，14号线分为AB段
     *
     * @param lineId 线路ID
     * @return 数组下标，即线路在LINES_TRANSFER数组的下标
     */
    protected int getIndexOfLinesTransferEdges(final String lineId) {
        for (int i = 0; i < SubwayData.LINE_EDGES.length; i++) {
            if (lineId.equals(SubwayData.LINE_EDGES[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据车站ID获取线路ID，14号线分为AB段
     *
     * @param lstStationIds 车站ID集合
     * @return 线路集合
     */
    protected List<String> getLineIdsFromStationIds(final List<String> lstStationIds) {
        List<String> lstLineIds = new ArrayList<>();
        for (String sid : lstStationIds) {
            final String lid = sid.substring(0, 2);
            if (lid.equals(SubwayData.LINE_14)) {
                if (sid.compareTo(SubwayData.LINE_14A_IDS[1]) <= 0) {
                    lstLineIds.add(SubwayData.LINE_14A);
                } else if (sid.compareTo(SubwayData.LINE_14B_IDS[0]) >= 0) {
                    lstLineIds.add(SubwayData.LINE_14B);
                } else {
                    lstLineIds.add(SubwayData.LINE_14);
                }
            } else {
                lstLineIds.add(lid);
            }
        }
        return lstLineIds;
    }

    /**
     * 地铁图中以临接表形式保存换乘信息，例如：0908(七里庄)，0913(郭公庄车)，09，6025
     *
     * @param fromStationId Head部，记录图中所有车站
     * @param toStationId   Item部，记录某一车站能够到达的所有车站
     * @param lineId        Head->Item所在线路ID
     * @param distance      Head->Item之间的距离
     */
    protected void addHeadAndItems(final String fromStationId, final String toStationId, final String lineId, final int distance) {
        Item item = new Item(toStationId, lineId, distance);
        for (Head head : mLstHeads) {
            if (head.sid.equals(fromStationId)) {
                head.lstItems.add(item);
                return;
            }
        }
        // 如果Head中不存在fromStationId，则创建并添加到mLstHeads中
        Head head = new Head(fromStationId);
        head.lstItems.add(item);
        mLstHeads.add(head);
    }

    /**
     * 删除Head->Item，例如：0908(七里庄)-0913(郭公庄车)
     *
     * @param fromStationId Head部
     * @param toStationId   Item部
     */
    protected void delHeadAndItems(final String fromStationId, final String toStationId) {
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
     * @param targetLineId    插入的车站线路ID
     * @param targetStationId 插入的车站ID
     */
    protected void insertStationToSubwayMap(final String targetLineId, final String targetStationId) {
        // 获取普通车站的临接车站，List长度为2，表示[from,to]
        List<String> lstAdjacentStationIds = mLineDao.getAdjacentStationIds(targetStationId);

        // 获取[from,to]车站ID的最小值，即地铁图中的车站ID
        final String fromMapStationId = mStationDao.getMapStationId(lstAdjacentStationIds.get(0));
        final String toMapStationId = mStationDao.getMapStationId(lstAdjacentStationIds.get(1));
        // 获取[from,to]的线路车站ID，即SubwayMap中的车站ID
        final String fromLineStationId = mStationDao.getLineStationId(targetLineId, fromMapStationId);
        final String toLineStationId = mStationDao.getLineStationId(targetLineId, toMapStationId);

        // 双向删除[from,to]
        delHeadAndItems(fromMapStationId, toMapStationId);
        delHeadAndItems(toMapStationId, fromMapStationId);

        // 获取[from,target]的站间距离
        final int fromTargetDistance = mLineDao.getIntervalDistance(fromLineStationId, targetStationId);
        // 双向添加[from,target]
        addHeadAndItems(fromMapStationId, targetStationId, targetLineId, fromTargetDistance);
        addHeadAndItems(targetStationId, fromMapStationId, targetLineId, fromTargetDistance);

        // 获取[target,to]的站间距离
        final int targetToDistance = mLineDao.getIntervalDistance(toLineStationId, targetStationId);
        // 双向添加[target,to]
        addHeadAndItems(targetStationId, toMapStationId, targetLineId, targetToDistance);
        addHeadAndItems(toMapStationId, targetStationId, targetLineId, targetToDistance);
    }

    /**
     * 向换乘区间加入新车站，例如：向[七里庄,郭公庄]插入[科怡路,丰台科技园]，则变为[七里庄,科怡路]，[科怡路,丰台科技园]，[丰台科技园,郭公庄]
     *
     * @param targetLineId     插入的车站线路ID
     * @param smallerStationId 插入的车站ID较小值
     * @param largerStationId  插入的车站ID较大值
     */
    protected void insertStationToSubwayMap(final String targetLineId, final String smallerStationId, final String largerStationId) {
        // 获取普通车站的临接车站，List长度为2，表示[from,to]
        List<String> lstAdjacentStationIds = mLineDao.getAdjacentStationIds(smallerStationId);

        // 获取[from,to]车站ID的最小值，即地铁图中的车站ID
        final String fromMapStationId = mStationDao.getMapStationId(lstAdjacentStationIds.get(0));
        final String toMapStationId = mStationDao.getMapStationId(lstAdjacentStationIds.get(1));
        // 获取[from,to]的线路车站ID，即SubwayMap中的车站ID
        final String fromLineStationId = mStationDao.getLineStationId(targetLineId, fromMapStationId);
        final String toLineStationId = mStationDao.getLineStationId(targetLineId, toMapStationId);

        // 双向删除[from,to]
        delHeadAndItems(fromMapStationId, toMapStationId);
        delHeadAndItems(toMapStationId, fromMapStationId);

        // 获取[from,smaller]的站间距离
        final int fromSmallerDistance = mLineDao.getIntervalDistance(fromLineStationId, smallerStationId);
        // 双向添加[from,smaller]
        addHeadAndItems(fromMapStationId, smallerStationId, targetLineId, fromSmallerDistance);
        addHeadAndItems(smallerStationId, fromMapStationId, targetLineId, fromSmallerDistance);

        // 获取[smaller,larger]的站间距离
        final int smallerLargerDistance = mLineDao.getIntervalDistance(smallerStationId, largerStationId);
        // 双向添加[smaller,larger]
        addHeadAndItems(smallerStationId, largerStationId, targetLineId, smallerLargerDistance);
        addHeadAndItems(largerStationId, smallerStationId, targetLineId, smallerLargerDistance);

        // 获取[larger,to]的站间距离
        final int largerToDistance = mLineDao.getIntervalDistance(largerStationId, toLineStationId);
        // 双向添加[larger,to]
        addHeadAndItems(largerStationId, toMapStationId, targetLineId, largerToDistance);
        addHeadAndItems(toMapStationId, largerStationId, targetLineId, largerToDistance);
    }

    /**
     * 从抽象图中搜索最短路径，车站ID为所有ID的最小值，例如：1号线军事博物馆站(0109)，9号线军事博物馆站(0904)，则传0109
     *
     * @param fromStationId 起点车站ID
     * @param toStationId   终点车站ID
     * @return From-To换乘路线
     */
    protected TransferRoute searchTransferRoute(final String fromStationId, final String toStationId) {
        final int size = mLstHeads.size();// SubwayMap中结点总数，即车站总数
        int[] distance = new int[size];// From车站到各车站的距离
        int[] previous = new int[size];// 各车站的前驱车站索引值
        boolean[] visited = new boolean[size];// From车站是否已遍历各车站

        // from车站在mLstHeads的索引值
        int fromStationIndex = 0;
        for (Head head : mLstHeads) {
            if (head.sid.equals(fromStationId)) {
                break;
            }
            fromStationIndex++;
        }
        // to车站在mLstHeads的索引值
        int toStationIndex = 0;
        for (Head head : mLstHeads) {
            if (head.sid.equals(toStationId)) {
                break;
            }
            toStationIndex++;
        }

        // 初始化From指各车站之间的距离，每一个车站的前驱车站下标，默认各车站均未访问
        for (int i = 0; i < size; i++) {
            distance[i] = getFromToDistanceByHeadIndex(fromStationIndex, i);
            previous[i] = fromStationIndex;
            visited[i] = false;
        }
        previous[fromStationIndex] = -1;
        visited[fromStationIndex] = true;

        // 迪杰斯特拉算法，14号分为AB段，图中任意两点可达
        int cur = 0, min, tmp;
        while (!visited[toStationIndex]) {
            // 寻找当前最小的路径
            min = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                if (!visited[i] && distance[i] < min) {
                    min = distance[i];
                    cur = i;
                }
            }
            // 标记已获取到最短路径
            visited[cur] = true;
            // 修正当前最短路径和前驱结点
            for (int i = 0; i < size; i++) {
                tmp = getFromToDistanceByHeadIndex(cur, i);
                if (tmp != Integer.MAX_VALUE) {
                    tmp += min;
                }
                if (!visited[i] && tmp < distance[i]) {
                    distance[i] = tmp;
                    previous[i] = cur;
                }
            }
        }
        // 例如：计算从A到D，中间隔着BC，路径为A-C-B-D，previous记录结点前驱下标，即D记录B的下标，B记录C的下标，C记录A的下标，
        // 想要得到从A到D的路径，先根据终点下标逆向遍历获得D-B-C-A，再逆向遍历获得A-C-B-D
        int[] path = new int[size];
        int pathSize = 0;
        int start = fromStationIndex;
        int end = toStationIndex;
        while (end != start) {
            path[pathSize++] = end;
            end = previous[end];
        }
        path[pathSize++] = fromStationIndex;

        if (LogUtil.DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (int i = pathSize - 1; i >= 0; i--) {
                sb.append(mLstHeads.get(path[i]).sid).append(",");
            }
            sb.append("size:").append(pathSize);
            LogUtil.d(sb.toString());
        }

        // 生成换乘路线并返回
        return generateTransferRoute(path, pathSize);
    }

    /**
     * 获取From-To站间距离，From，To均在mLstHeads列表中
     *
     * @param fromIndex From在mLstHeads列表的索引
     * @param toIndex   To在mLstHeads列表的索引
     * @return From-To站间距离，如果From-To不可达，则返回Integer.MAX_VALUE
     */
    protected int getFromToDistanceByHeadIndex(final int fromIndex, final int toIndex) {
        final String toStationId = mLstHeads.get(toIndex).sid;
        for (Item item : mLstHeads.get(fromIndex).lstItems) {
            if (toStationId.equals(item.sid)) {
                return item.distance;
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 获取From-To站所在线路ID
     *
     * @param fromIndex From在mLstHeads列表的索引
     * @param toIndex   To在mLstHeads列表的索引
     * @return From-To站所在线路ID
     */
    protected String getFromToLineIdByHeadIndex(final int fromIndex, final int toIndex) {
        final String toStationId = mLstHeads.get(toIndex).sid;
        for (Item item : mLstHeads.get(fromIndex).lstItems) {
            if (toStationId.equals(item.sid)) {
                return item.lid;
            }
        }
        return "";
    }

    /**
     * 生成换乘路线
     *
     * @param path A-D的遍历路径
     * @param size path数组的实际长度
     * @return 换乘路线
     */
    protected TransferRoute generateTransferRoute(final int[] path, final int size) {
        // path为D-B-C-A，逆向遍历获得A-C-B-D
        String curLineId = "";
        List<TransferSubRoute> lstTransferSubRoute = new ArrayList<>();
        for (int i = size - 1; i > 0; i--) {
            final String fromStationId = mLstHeads.get(path[i]).sid;// 车站A
            final String toStationId = mLstHeads.get(path[i - 1]).sid;// 车站C
            final String fromToLineId = getFromToLineIdByHeadIndex(path[i], path[i - 1]);// A-C所在线路
            final int fromToDistance = getFromToDistanceByHeadIndex(path[i], path[i - 1]);// A-C站间距离

            // 如果A-C站与C-B站在同一线路，则合并为A-B，并区分不同线路
            if (fromToLineId.equals(curLineId)) {
                TransferSubRoute curSubRoute = lstTransferSubRoute.get(lstTransferSubRoute.size() - 1);
                curSubRoute.toStationName = toStationId;
                curSubRoute.distance += fromToDistance;
                // 机场线为单向换乘，记录所有换乘路径
                if (fromToLineId.equals(SubwayData.LINE_99)) {
                    curSubRoute.lstStationNames.add(fromStationId);
                }
            } else {
                TransferSubRoute subRoute = new TransferSubRoute();
                subRoute.fromStationName = fromStationId;
                subRoute.toStationName = toStationId;
                subRoute.lineName = fromToLineId;
                subRoute.distance = fromToDistance;
                // 机场线为单向换乘，记录所有换乘路径
                if (fromToLineId.equals(SubwayData.LINE_99)) {
                    subRoute.lstStationNames = new ArrayList<>();
                }
                lstTransferSubRoute.add(subRoute);
                curLineId = fromToLineId;
            }
        }
        // 更新换乘信息，将ID值转换为实际值
        updateTransferSubRoutes(lstTransferSubRoute);

        // 生成换乘线路
        TransferRoute transferRoute = new TransferRoute();
        transferRoute.fromStationName = mFromStationName;
        transferRoute.toStationName = mToStationName;
        transferRoute.airportLineDistance = 0;
        transferRoute.otherLineDistance = 0;
        transferRoute.elapsedTime = 0;
        transferRoute.lstTransferSubRoute = lstTransferSubRoute;

        // 机场线速度与价格与普通线不一致
        for (TransferSubRoute subRoute : lstTransferSubRoute) {
            if (subRoute.lineName.equals(SubwayData.LINE_JICHANGXIAN)) {
                transferRoute.airportLineDistance += subRoute.distance;
            } else {
                transferRoute.otherLineDistance += subRoute.distance;
            }
        }
        transferRoute.elapsedTime = SubwayUtil.getTransferElapsedTime(transferRoute.airportLineDistance, transferRoute.otherLineDistance, transferRoute.lstTransferSubRoute.size() - 1);

        return transferRoute;
    }

    /**
     * generateTransferRoute方法中TransferSubRoute各字段存的都是ID值，转换为实际值，并计算换乘方向和换乘区间车站
     *
     * @param lstTransferSubRoutes 换乘子线路集合
     */
    protected void updateTransferSubRoutes(final List<TransferSubRoute> lstTransferSubRoutes) {
        for (TransferSubRoute subRoute : lstTransferSubRoutes) {
            final String lineId = subRoute.lineName;
            final String fromLineStationId = mStationDao.getLineStationId(lineId, subRoute.fromStationName);
            final String toLineStationId = mStationDao.getLineStationId(lineId, subRoute.toStationName);
            // 途径车站
            if (SubwayUtil.isCircularLine(lineId)) {
                List<String> lstStationNames1 = mStationDao.getIntervalStationNames(fromLineStationId, toLineStationId);
                List<String> lstStationNames2 = mStationDao.getIntervalStationNamesInCircularLine(fromLineStationId, toLineStationId);
                if (lstStationNames1.size() < lstStationNames2.size()) {
                    subRoute.lstStationNames = lstStationNames1;
                } else {
                    subRoute.lstStationNames = lstStationNames2;
                }
            } else if (lineId.equals(SubwayData.LINE_99)) {
                ArrayList<String> newStationNames = new ArrayList<>();
                for (String sid : subRoute.lstStationNames) {
                    newStationNames.add(mStationDao.getStationName(sid));
                }
                subRoute.lstStationNames.clear();
                subRoute.lstStationNames.addAll(newStationNames);
            } else {
                subRoute.lstStationNames = mStationDao.getIntervalStationNames(fromLineStationId, toLineStationId);
            }
            // 换乘方向
            if (SubwayUtil.isCircularLine(lineId)) {
                subRoute.direction = subRoute.lstStationNames.size() > 0 ?
                        subRoute.lstStationNames.get(0) : mStationDao.getStationName(toLineStationId);
            } else if (subRoute.lineName.equals(SubwayData.LINE_14)) {
                if (fromLineStationId.compareTo(toLineStationId) < 0
                        && toLineStationId.compareTo(SubwayData.LINE_14A_IDS[1]) <= 0) {
                    // 西局方向
                    subRoute.direction = SubwayData.STATION_XIJU;
                } else if (fromLineStationId.compareTo(toLineStationId) > 0
                        && toLineStationId.compareTo(SubwayData.LINE_14B_IDS[0]) >= 0) {
                    // 北京南站方向
                    subRoute.direction = SubwayData.STATION_BEIJINGNANZHAN;
                } else {
                    subRoute.direction = fromLineStationId.compareTo(toLineStationId) < 0 ?
                            mLineDao.getLineLastStationName(lineId) : mLineDao.getLineFirstStationName(lineId);
                }
            } else {
                // 起点车站<终点车站，取线路车站ID最大值的车站名，反之，取线路车站ID最小的车站名
                subRoute.direction = fromLineStationId.compareTo(toLineStationId) < 0 ?
                        mLineDao.getLineLastStationName(lineId) : mLineDao.getLineFirstStationName(lineId);
            }
            // 线路名
            subRoute.lineName = SubwayUtil.getLineName(lineId);
            // 子路线起点站
            subRoute.fromStationName = mStationDao.getStationName(fromLineStationId);
            // 子路线终点站
            subRoute.toStationName = mStationDao.getStationName(toLineStationId);
        }
    }

    /**
     * 将transferRoute添加到transferDetail中，并以运行时间升序排序
     *
     * @param transferDetail 换乘详情(包含多条换乘线路)
     * @param transferRoute  换乘线路
     */
    protected void updateTransferDetail(TransferDetail transferDetail, TransferRoute transferRoute) {
        int insertIndex = 0;
        boolean hasSameRoute = false;
        for (TransferRoute route : transferDetail.lstTransferRoute) {
            if (route.airportLineDistance == transferRoute.airportLineDistance
                    && route.otherLineDistance == transferRoute.otherLineDistance) {
                hasSameRoute = true;
                break;
            } else if (route.elapsedTime < transferRoute.elapsedTime) {
                insertIndex++;
            } else {
                break;
            }
        }
        if (!hasSameRoute) {
            transferDetail.lstTransferRoute.add(insertIndex, transferRoute);
        }
        if (transferDetail.lstTransferRoute.size() > 3) {
            transferDetail.lstTransferRoute.remove(transferDetail.lstTransferRoute.size() - 1);
        }
    }
}
