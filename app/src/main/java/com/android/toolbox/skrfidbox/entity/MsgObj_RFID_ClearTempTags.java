package com.android.toolbox.skrfidbox.entity;
import com.android.toolbox.skrfidbox.econst.ECmdType;
import com.android.toolbox.skrfidbox.econst.ERfid;

public class MsgObj_RFID_ClearTempTags extends MsgObjBase {
    /**
     * 清空盘点
     * killer:
     * 比如上一次盘点了200张标签。下一次盘点的时候会以上一次的标签为基准，计算哪些是新增的，哪些是减少的。     *
     * killer:
     * 清空就是删除上一次的盘点结果。
     */
    public MsgObj_RFID_ClearTempTags() {
        super.setCmdType(ECmdType.RFID);
        super.setCmdTag(ERfid.ClearTempTags);
    }
}
