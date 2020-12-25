package com.android.toolbox.skrfidbox.econst;

public enum EHFCard  implements EByteBase{
    /**
    ** 上报ID号
    **/
    NotifyIdCardNum(0x01),
    /**
    ** 上报用户区数据
    **/
    NotifyUserData(0x02),
    /**
    ** 读取用户区数据
    **/
    ReadUserData(0x03),
    /**
    ** 配置读取模式
    **/
    SetReadMode(0x04),
    /**
    ** 获取ID号
    **/
    GetIdCardNum(0x05);
    private byte code;

    EHFCard(byte _code) {
        this.code = _code;
    }

    EHFCard(int _code) {
        this.code = (byte) _code;
    }

    public byte getCode() {
        return code;
    }

    @Override
    public EByteBase parse(Byte val) {
        return getCmdTag(val);
    }

    public static EHFCard getCmdTag(Byte val) {
        for (EHFCard prot : values()) {
            if (prot.getCode() == val) {
                return prot;
            }
        }
        return null;
    }
}
