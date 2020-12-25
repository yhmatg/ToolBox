package com.android.toolbox.skrfidbox.econst;

public enum EHartBeat implements EByteBase {
    /**
     * * 客户端心跳上报
     **/
    DeviceHartBeat(0x01),
    /**
     * * 服务端心跳应答
     **/
    SoftHartBeat(0x02);
    private byte code;

    EHartBeat(byte _code) {
        this.code = _code;
    }

    EHartBeat(int _code) {
        this.code = (byte) _code;
    }

    public byte getCode() {
        return code;
    }

    @Override
    public EByteBase parse(Byte val) {
        return getCmdTag(val);
    }

    public static EHartBeat getCmdTag(Byte val) {
        for (EHartBeat prot : values()) {
            if (prot.getCode() == val) {
                return prot;
            }
        }
        return null;
    }
}
