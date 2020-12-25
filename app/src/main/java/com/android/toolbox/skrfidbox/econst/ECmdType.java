package com.android.toolbox.skrfidbox.econst;

public enum ECmdType implements EByteBase {

    /**
     * 锁
     **/
    Lock(0x01),

    /**
     * 灯
     **/
    LED(0x02),

    /**
     * 指静脉
     **/
    Finger(0x03),

    /**
     * 高频
     **/
    HFCard(0x04),
    /**
     * 二维码
     **/
    QRCode(0x05),
    /**
     * 超高频
     **/
    RFID(0x06),
    /**
     * 故障报警数据
     **/
    Warnning(0x07),
    /**
     * 心跳
     **/
    HartBeat(0x08),
    /**
     * 设备上线
     **/
    Online(0x09),
    /**
     * 温湿度
     **/
    Humiture(0x0A),
    /**
     * 设置柜号
     **/
    Address(0x0B);

    private byte code;

    ECmdType(byte _code) {
        this.code = _code;
    }

    ECmdType(int _code) {
        this.code = (byte) _code;
    }

    public byte getCode() {
        return code;
    }

    @Override
    public EByteBase parse(Byte val) {
        return getCmdType(val);
    }

    public static ECmdType getCmdType(Byte val) {
        for (ECmdType prot : values()) {
            if (prot.getCode() == val) {
                return prot;
            }
        }
        return null;
    }
}
