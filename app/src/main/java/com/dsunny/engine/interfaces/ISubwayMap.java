package com.dsunny.engine.interfaces;

import com.dsunny.activity.bean.TransferDetail;

/**
 * 地铁图抽象接口
 */
public interface ISubwayMap {
    TransferDetail search(final String fromStationName, final String toStationName);
}
