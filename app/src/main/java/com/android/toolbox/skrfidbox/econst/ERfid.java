package com.android.toolbox.skrfidbox.econst;

public enum ERfid implements EByteBase {
    /**
     * * 盘点数据上报
     **/
    NotifyReadData(0x01),
    /**
     * * 启动盘点
     **/
    StartReadTags(0x02),
    /**
     * * 结束盘点
     **/
    StopReadTags(0x03),
    /****
     ** 设置盘点模式
     **/
    SetReadMode(0x04),
    /****
     ** 异常数据上报
     **/
    NotifyAbnormalData(0x05),
    /****
     ** 设置功率
     **/
    SetPower(0x06),
    /****
     ** 查询功率
     **/
    GetPower(0x07),
    /**
     * * 获取所有标签
     **/
    GetAllTags(0x08),
    /**
     * * 清空标签缓存
     **/
    ClearTempTags(0x09);
    private byte code;

    ERfid(byte _code) {
        this.code = _code;
    }

    ERfid(int _code) {
        this.code = (byte) _code;
    }

    public byte getCode() {
        return code;
    }

    @Override
    public EByteBase parse(Byte val) {
        return getCmdTag(val);
    }

    public static ERfid getCmdTag(Byte val) {
        for (ERfid prot : values()) {
            if (prot.getCode() == val) {
                return prot;
            }
        }
        return null;
    }
}
