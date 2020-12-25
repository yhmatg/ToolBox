package com.android.toolbox.skrfidbox;

import java.io.UnsupportedEncodingException;

public class DataConverts {
    /**
    ** 校验：校验(不包含帧头和帧尾校验位)并获得校验数组
    **/
    public static byte[] Make_CRC16(byte[] puchMsg) {
        int wCRCin = 0x0000; //初值
        int wCPoly = 0x1021; //多项式
        int wChar = 0;
        int k = 0;
        int usDataLen = puchMsg.length;
        while (usDataLen-- > 0) {
            wChar = puchMsg[k++] & 0xff;
            wCRCin ^= wChar << 8;
            for (int i = 0; i < 8; i++) {
                if ((wCRCin & 0x8000) > 0)
                    wCRCin = (Integer) ((wCRCin << 1) ^ wCPoly);
                else
                    wCRCin = (Integer) (wCRCin << 1);
            }
        }
        byte[] bytes = Int_To_bytes(wCRCin);
        return new byte[]{bytes[2], bytes[3]};
    }


    /**
    **    转换21：   数组16   -   Ushort     如：{00，0A}  = 10（仅用于2个元素的数组转换，其他出错）
    **/
    public static short bytes_To_short(byte[] hexByes) {
        short sRet = 0;
        sRet += (hexByes[0] & 0xFF) << 8;
        sRet += hexByes[1] & 0xFF;
        return sRet;
    }

    public static byte[] reverse(byte[] hexByes) {
        byte[] result = new byte[hexByes.length];
        for (int i = hexByes.length; i > 0; i--) {
            result[hexByes.length - i] = hexByes[i - 1];
        }
        return result;
    }

    /**
    ** 转换2：   int/ushort10   ->   数组10    如10 = {0A ，00}
    **/
    public static byte[] Int_To_bytes(int i) {
        byte[] _temp = new byte[]
                {
                        (byte) (0xff & i),
                        (byte) ((0xff00 & i) >> 8),
                        (byte) ((0xff0000 & i) >> 16),
                        (byte) ((0xff000000 & i) >> 24)
                };
        return reverse(_temp);
    }


    /**
    ** 转换2：   int/ushort10   ->   数组10    如10 = {00 ，0A}
    **/
    public static byte[] Short_To_bytes(short i) {
        byte[] _temp = new byte[]
                {
                        (byte) (0xff & i),
                        (byte) ((0xff00 & i) >> 8),
                };
        return reverse(_temp);
    }


    /**
    **    转换20：   Copy  Some bytes
    **/
    public static byte[] Readbytes(byte[] respose, int index, int length) {
        byte[] revs = new byte[length];
        for (int i = 0; i < length; i++) {
            revs[i] = respose[index + i];
        }
        return revs;
    }

    /**
    ** 十六进制 ASCII   转字符串
    **/
    public static String Bytes_To_ASCII(byte[] bytes) {
        int offset = 0;
        int dateLen = bytes.length;
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }

    public static String Ints_To_HexStr(int[] bytes) {
        int off = 0;
        int len = bytes.length;
        String str = "";
        if (bytes == null) {
            return str;
        }
        if ((off + len) > bytes.length) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = off; i < (off + len); i++) {
            //去除負數
            stringBuilder.append(String.format("%02x", (int) (bytes[i] & 0xff)));
        }
        return stringBuilder.toString();
    }
}
