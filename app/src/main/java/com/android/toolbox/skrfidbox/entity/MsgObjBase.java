package com.android.toolbox.skrfidbox.entity;

import com.android.toolbox.skrfidbox.DataConverts;
import com.android.toolbox.skrfidbox.econst.EByteBase;
import com.android.toolbox.skrfidbox.econst.ECmdType;
import com.android.toolbox.skrfidbox.econst.EHFCard;
import com.android.toolbox.skrfidbox.econst.EHartBeat;
import com.android.toolbox.skrfidbox.econst.ELed;
import com.android.toolbox.skrfidbox.econst.ELock;
import com.android.toolbox.skrfidbox.econst.EReturnResult;
import com.android.toolbox.skrfidbox.econst.ERfid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 消息体基类
 */
public class MsgObjBase {
    private byte[] headFlag;//头标志 2字节
    private byte addressNum;//地址 1字节
    private byte[] serialNum;//序列号 4字节
    private ECmdType cmdType; //命令类型
    private EByteBase cmdTag;    //命令tag
    private byte ereturn;//返回值

    public byte[] getHeadFlag() {
        return headFlag;
    }

    public byte getAddressNum() {
        return addressNum;
    }

    public byte[] getSerialNum() {
        return serialNum;
    }

    public ECmdType getCmdType() {
        return cmdType;
    }

    public EByteBase getCmdTag() {
        return cmdTag;
    }

    public byte getEreturn() {
        return ereturn;
    }

    public byte[] getCmdLenbyte() {
        return cmdLenbyte;
    }

    public byte[] getCmdData() {
        return cmdData;
    }

    public byte[] getCrc() {
        return crc;
    }

    public byte[] getFrameData() {
        return frameData;
    }

    private byte[] cmdLenbyte;//命令长度
    private byte[] cmdData;//命令数据
    private byte[] crc;//crc
    private byte[] frameData;//帧数据

    public void setHeadFlag(byte[] headFlag) {
        this.headFlag = headFlag;
    }

    public void setAddressNum(byte addressNum) {
        this.addressNum = addressNum;
    }

    public void setSerialNum(byte[] serialNum) {
        this.serialNum = serialNum;
    }

    public void setCmdType(ECmdType cmdType) {
        this.cmdType = cmdType;
    }

    public void setCmdTag(EByteBase cmdTag) {
        this.cmdTag = cmdTag;
    }

    public void setEreturn(byte ereturn) {
        this.ereturn = ereturn;
    }

    public void setCmdLenbyte(byte[] cmdLenbyte) {
        this.cmdLenbyte = cmdLenbyte;
    }

    public void setCmdData(byte[] cmdData) {
        this.cmdData = cmdData;
    }

    public void setCrc(byte[] crc) {
        this.crc = crc;
    }

    public void setFrameData(byte[] frameData) {
        this.frameData = frameData;
    }

    public MsgObjBase()  //消息基础
    {
        headFlag = new byte[]{0x17, (byte) 0x99};//头部
        addressNum = 0x00;//地址
        cmdData = null;
        ereturn = EReturnResult.NonResponse;
        serialNum = new byte[]{0, 0, 0, 0};//序列号
    }

    public static EByteBase getCmdTag(ECmdType cmdType, byte code) {
        EByteBase result = ERfid.StartReadTags;
        switch (cmdType) {
            case RFID:
                result = ERfid.getCmdTag(code);
                break;
            case HartBeat:
                result = EHartBeat.getCmdTag(code);
                break;
            case LED:
                result = ELed.getCmdTag(code);
                break;
            case Lock:
                result = ELock.getCmdTag(code);
                break;
            case HFCard:
                result = EHFCard.getCmdTag(code);
                break;
        }
        return result;
    }

    public int flag = 0;;
    public MsgObjBase(byte[] receivedData) //接收数据
    {
        frameData = receivedData;//帧数据
        headFlag = new byte[]{receivedData[flag], receivedData[flag + 1]};
        flag += 2;
        addressNum = receivedData[flag++];
        serialNum = new byte[]{receivedData[flag], receivedData[flag + 1], receivedData[flag + 2], receivedData[flag + 3]};
        flag += 4;
        cmdType = ECmdType.getCmdType(receivedData[flag++]);
        cmdTag = getCmdTag(cmdType, receivedData[flag++]);
        ereturn = receivedData[flag++];
        cmdLenbyte = new byte[]{receivedData[flag], receivedData[flag + 1]};
        flag += 2;
        short len = DataConverts.bytes_To_short(cmdLenbyte);
        if (len > 0) {
            cmdData = new byte[len];
            for (int i = 0; i < len; i++) {
                cmdData[i] = receivedData[flag + i];
            }
        } else
            cmdData = null;
        flag += len;
        crc = new byte[]{receivedData[flag], receivedData[flag + 1]};
    }

    /**
     * * 设置属性
     **/
    protected void SendPacked() {
    }

    @Override
    public String toString() {
        return "com.esim.rfidclient.sdk.box.entity.MsgObjBase{" +
                "headFlag=" + Arrays.toString(headFlag) +
                ", addressNum=" + addressNum +
                ", serialNum=" + Arrays.toString(serialNum) +
                ", cmdType=" + cmdType +
                ", cmdTag=" + cmdTag +
                ", ereturn=" + ereturn +
                ", cmdLenbyte=" + Arrays.toString(cmdLenbyte) +
                ", cmdData=" + Arrays.toString(cmdData) +
                ", crc=" + Arrays.toString(crc) +
                ", frameData=" + Arrays.toString(frameData) +
                '}';
    }

    /**
     * * 把属性打包成数组
     **/
    public byte[] CmdTobytes() {
        SendPacked();
        List<Byte> tempCmd = new ArrayList<Byte>();
        //消息头
        for (int i = 0; i < headFlag.length; i++) {
            tempCmd.add(headFlag[i]);
        }
        //地址号
        tempCmd.add(addressNum);
        //序列号
        for (int i = 0; i < serialNum.length; i++) {
            tempCmd.add(serialNum[i]);
        }
        //命令
        tempCmd.add(cmdType.getCode());
        //
        tempCmd.add(cmdTag.getCode());
        //据此 9位
        if (cmdData == null) {
            tempCmd.add((byte) 0x00);
            tempCmd.add((byte) 0x00);
        } else {
            byte[] bytes = DataConverts.Short_To_bytes((short) cmdData.length);
            for (int i = 0; i < bytes.length; i++) {
                tempCmd.add(bytes[i]);
            }
            for (int i = 0; i < cmdData.length; i++) {
                tempCmd.add(cmdData[i]);
            }
        }
        byte[] tempbytes = new byte[tempCmd.size()];
        frameData = new byte[tempCmd.size() + 2];
        for (int i = 0; i < tempCmd.size(); i++) {
            final Byte objByte = tempCmd.get(i);
            tempbytes[i] = frameData[i] = objByte;
        }
        byte[] tempCrc = DataConverts.Make_CRC16(tempbytes);
        frameData[frameData.length - 2] = tempCrc[0];
        frameData[frameData.length - 1] = tempCrc[1];
        return frameData;
    }

}

