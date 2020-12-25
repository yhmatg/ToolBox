package com.android.toolbox.skrfidbox.entity;

import com.android.toolbox.skrfidbox.econst.ECmdType;
import com.android.toolbox.skrfidbox.econst.EHartBeat;

/**
 * 心跳包消息体
 */
public class MsgObj_HeartBeat extends MsgObjBase {
    public MsgObj_HeartBeat() {
        super.setCmdType(ECmdType.HartBeat);
        super.setCmdTag(EHartBeat.SoftHartBeat);
    }
}