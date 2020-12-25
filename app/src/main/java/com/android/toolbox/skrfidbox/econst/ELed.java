package com.android.toolbox.skrfidbox.econst;

public enum ELed  implements EByteBase{
    /**
    ** 打开照明灯
    **/
    OpenLightLED(0x01),
    /**
    ** 关闭照明灯
    **/
    CloseLightLED(0x02),
    /**
    ** 打开报警灯
    **/
    OpenAlarmLED(0x03),
    /**
    ** 关闭报警灯
    **/
    CloseALarmLED(0x04),
    /**
    ** 设置照明灯工作模式
    **/
    SetLightLedMode(0x05);
    private byte code;

    ELed(byte _code) {
        this.code = _code;
    }

    ELed(int _code) {
        this.code = (byte) _code;
    }

    public byte getCode() {
        return code;
    }

    @Override
    public EByteBase parse(Byte val) {
        return getCmdTag(val);
    }

    public static ELed getCmdTag(Byte val) {
        for (ELed prot : values()) {
            if (prot.getCode() == val) {
                return prot;
            }
        }
        return null;
    }
}
