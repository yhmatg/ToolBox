package com.android.toolbox.skrfidbox.econst;

public enum ELock implements EByteBase {
    /**
     * 开锁
     **/
    OpenLock(0x01),
    /**
     * * 状态反馈
     **/
    NotifyStatus(0x02),
    /**
     * * 关锁
     **/
    CloseLock(0x03),
    /**
     * * 锁一直开着
     **/
    LockKeepOpen(0x04);
    private byte code;

    ELock(byte _code) {
        this.code = _code;
    }

    ELock(int _code) {
        this.code = (byte) _code;
    }

    public byte getCode() {
        return code;
    }

    @Override
    public EByteBase parse(Byte val) {
        return getCmdTag(val);
    }

    public static ELock getCmdTag(Byte val) {
        for (ELock prot : values()) {
            if (prot.getCode() == val) {
                return prot;
            }
        }
        return null;
    }
}
