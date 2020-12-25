package com.android.toolbox.skrfidbox.econst;

public final class EReturnResult {
    /**
     * 失败
     **/
    public static byte Failed = 0x00;
    /**
     * 成功
     **/
    public static byte Succeed = 0x01;
    /**
     * 指令不存在
     **/
    public static byte NoSuchCMD = 0x02;
    /**
     * CRC校验错误
     **/
    public static byte ErrorCRC = 0x03;
    /**
     * 魔数
     **/
    public static byte ErrorMagic = 0x04;
    /**
     * 长度错误
     **/
    public static byte ErrorLen = 0x05;
    /**
     * 设备编号错误
     **/
    public static byte ErrorDevNum = 0x06;
    /**
     * 空数据
     **/
    public static byte ErrorNullData = 0x07;
    /**
     * json错误
     **/
    public static byte ErrorJson = 0x08;
    /**
     * 超时
     **/
    public static byte TimeOut = 0x09;
    /**
     * 其他错误
     **/
    public static byte OthersError = 0x0A;

    /**
     * 通信失败，没有发送成功
     **/
    public static byte NonResponse = (byte) 0xFF;
}