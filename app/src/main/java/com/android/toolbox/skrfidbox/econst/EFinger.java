package com.android.toolbox.skrfidbox.econst;

public enum EFinger  implements EByteBase{
    /**
    ** 指静脉模板数据采集
    **/
    GatherTemplate(0x01),
    /**
    ** 1：N验证结果上报
    **/
    Verify_1_N(0x02),
    /**
    ** 指静脉模板写入设备
    **/
    WriteTemplateToDevice(0x03),
    /**
    ** 指静脉采集上报
    **/
    NotifyGatherTemplate(0x04),
    /**
    ** 指静脉模板写入设备确认回复
    **/
    ReplyYes_WriteTemplateToDevice(0x05),
    /**
    ** 删除指静脉模板
    **/
    DeleteSingleTemplate(0x06),
    /**
    ** 服务端应答收到采集信息
    **/
    GatherTemplateACK(0x07),
    /**
    ** 删除手指
    **/
    DeleteAllTemplate(0x08),
    /**
    ** 请移开手指
    **/
    PlaceMoveFinger(0x09),
    /**
    ** 取消注册
    **/
    CancelGatherTemplate(0x0A);
    private byte code;

    EFinger(byte _code) {
        this.code = _code;
    }

    EFinger(int _code) {
        this.code = (byte) _code;
    }

    public byte getCode() {
        return code;
    }

    @Override
    public EByteBase parse(Byte val) {
        return getCmdTag(val);
    }

    public static EFinger getCmdTag(Byte val) {
        for (EFinger prot : values()) {
            if (prot.getCode() == val) {
                return prot;
            }
        }
        return null;
    }
}
