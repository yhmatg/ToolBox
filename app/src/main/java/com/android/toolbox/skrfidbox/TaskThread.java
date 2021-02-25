package com.android.toolbox.skrfidbox;

import android.widget.Adapter;

import com.alibaba.fastjson.JSON;
import com.android.toolbox.skrfidbox.callback.ILockStatusCallback;
import com.android.toolbox.skrfidbox.callback.IRfidReadCallback;
import com.android.toolbox.skrfidbox.econst.EByteBase;
import com.android.toolbox.skrfidbox.econst.ECmdType;
import com.android.toolbox.skrfidbox.econst.ELed;
import com.android.toolbox.skrfidbox.econst.ELock;
import com.android.toolbox.skrfidbox.econst.ERfid;
import com.android.toolbox.skrfidbox.entity.MsgObjBase;
import com.android.toolbox.skrfidbox.entity.MsgObj_HeartBeat;
import com.android.toolbox.skrfidbox.entity.Tags;
import com.xuexiang.xlog.XLog;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class TaskThread extends Thread {
    private Socket socket;
    private List<MsgObjBase> cmdList = new LinkedList<MsgObjBase>();
    private int readTimeout = 0;
    IRfidReadCallback rfidReadCallback = null;
    ILockStatusCallback lockStatusCallback = null;

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * 构造函数
     */
    public TaskThread(ServerSocket serverSocket) {
        try {
            this.socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 线程执行的操作，响应客户端的请求
    private byte addressNum;//地址 1字节
    private byte[] serialNum;//序列号 4字节

    /**
     * 发送盘点命令
     *
     * @param times 盘点耗时
     */
    public void sendStartReadTagsCmd(int times, IRfidReadCallback rfidReadCallback) {
        this.rfidReadCallback = rfidReadCallback;
        sendCmd(ECmdType.RFID, ERfid.StartReadTags, new byte[]{(byte) times});
    }

    /**
     * 发送开锁、关锁命令
     *
     * @param lockType
     * @param lockStatusCallback
     */
    public void sendLockCmd(ELock lockType, ILockStatusCallback lockStatusCallback) {
        this.lockStatusCallback = lockStatusCallback;
        sendCmd(ECmdType.Lock, lockType, new byte[]{(byte) 1});
    }

    /**
     * 发送获取所有标签命令
     */
    public void sendGetAllTagsCmd() {
        sendCmd(ECmdType.RFID, ERfid.GetAllTags, new byte[]{1});
    }

    /**
     * 发送打开照明指令
     */
    public void sendLightLedOpen() {
        sendCmd(ECmdType.LED, ELed.OpenLightLED, new byte[]{1});
    }

    /**
     * 发送关闭照明指令
     */
    public void sendLightLedClose() {
        sendCmd(ECmdType.LED, ELed.CloseLightLED, new byte[]{1});
    }

    /**
     * 发送打开报警指令
     */
    public void sendAlarmLedOpen() {
        sendCmd(ECmdType.LED, ELed.OpenAlarmLED, new byte[]{1});
    }

    /**
     * 发送关闭报警指令
     */
    public void sendAlarmLedClose() {
        sendCmd(ECmdType.LED, ELed.CloseALarmLED, new byte[]{1});
    }


    /**
     * 发送一个指令
     *
     * @param cmdType 指令类型
     * @param erfid   RFID 命令参数
     */
    public void sendCmd(ECmdType cmdType, EByteBase erfid) {
        sendCmd(cmdType, erfid, new byte[]{0});
    }

    /**
     * 发送一个指令
     *
     * @param cmdType 指令类型
     * @param erfid   RFID 命令参数
     * @param cmdData 指令数据包
     */
    public void sendCmd(ECmdType cmdType, EByteBase erfid, byte[] cmdData) {
        MsgObjBase msgObjBase = new MsgObjBase();
        msgObjBase.setCmdType(cmdType);
        msgObjBase.setCmdTag(erfid);
        msgObjBase.setCmdData(cmdData);
        msgObjBase.setAddressNum(addressNum);
        msgObjBase.setSerialNum(serialNum);
        sendCmd(msgObjBase);
    }

    /**
     * 发送一个指令
     *
     * @param msgObjBase 指令消息
     */
    private void sendCmd(MsgObjBase msgObjBase) {
        msgObjBase.setAddressNum(addressNum);
        msgObjBase.setSerialNum(serialNum == null ? new byte[4] : serialNum);
        byte[] bytes = msgObjBase.CmdTobytes();
        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkAvailable() {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            return false;
        }
        return true;
    }

    private List<Byte> pollDataList = new ArrayList<Byte>();
    public void run() {
        final int readTimeout = this.readTimeout;
        final List<Byte> pollDataList = this.pollDataList;
        //线程负责写数据到pollDataList 中
        Thread readThread = new Thread(new Runnable() {
            public void run() {
                InputStream inputStream = null;
                DataInputStream dis = null;
                if (!checkAvailable()) {
                    return;
                }
                try {
                    //必须设置线程超时时长，否则会导致cup过高或者连接通道无法关闭等问题
                    socket.setSoTimeout(readTimeout);
                    // 获取输入流，并读取客户端信息
                    inputStream = socket.getInputStream();
                    dis = new DataInputStream(inputStream);
                    int cacheSize = 1024 * 1024 * 20;
                    byte[] data = new byte[cacheSize];
                    int totalSize = -1;
                    while ((totalSize = dis.read(data)) > 0) {
                        synchronized (TaskThread.class) {
                            //todo 错误待修改
                            byte[] dataBytes = Arrays.copyOf(data, totalSize);
                            final Collection bytes = Collections.singleton(dataBytes);
                            pollDataList.addAll(bytes);
                            data = new byte[cacheSize];
                        }
                    }
                    socket.shutdownInput();// 关闭输入流
                    // 获取输出流，响应客户端的请求
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // 关闭资源
                    try {
                        if (inputStream != null)
                            inputStream.close();
                        if (socket != null)
                            socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        readThread.start();
        while (!readThread.isInterrupted()) {
            synchronized (TaskThread.class) {
                if (this.pollDataList.size() > 0) {
                    Byte[] data = new Byte[this.pollDataList.size()];
                    this.pollDataList.toArray(data);
                    final MsgObjBase msgObjBase = new MsgObjBase(toPrimitives(data));
                    int dataLength = msgObjBase.flag + 1;
                    if (dataLength > 0 && dataLength <= this.pollDataList.size()) {
                        processMsgObj(msgObjBase);
                        this.pollDataList = this.pollDataList.subList(dataLength, this.pollDataList.size());
                    }
                }
            }
        }
    }

    private void processMsgObj(MsgObjBase msgObjBase) {
        if (this.rfidReadCallback != null) {
            this.rfidReadCallback.OnReceiveData(msgObjBase);
        }
        if (msgObjBase.getCmdType() == ECmdType.Online) {
            serialNum = msgObjBase.getSerialNum();
            addressNum = msgObjBase.getAddressNum();
        }
        //回复心跳包
        if (msgObjBase.getCmdType() == ECmdType.HartBeat) {
            MsgObj_HeartBeat msgObj_heartBeat = new MsgObj_HeartBeat();
            sendCmd(msgObj_heartBeat);
        }
        if (msgObjBase.getCmdType() == ECmdType.RFID) {
            if (msgObjBase.getCmdData() != null) {
                Tags tags = null;
                switch ((ERfid) msgObjBase.getCmdTag()) {
                    case NotifyReadData:
                        String jsonStr = DataConverts.Bytes_To_ASCII(msgObjBase.getCmdData());
                        XLog.get().e("NotifyReadData111====" + new String(msgObjBase.getCmdData()));
                        XLog.get().e("NotifyReadData222====" + jsonStr);
                        tags = (Tags) JSON.parseObject(jsonStr, Tags.class);
                        sendGetAllTagsCmd();
                        if (this.rfidReadCallback != null) {
                            this.rfidReadCallback.OnNotifyReadData(tags);
                        }
                        break;
                    case GetAllTags:
                        String jsonStr2 = DataConverts.Bytes_To_ASCII(msgObjBase.getCmdData());
                        XLog.get().e("GetAllTags111====" + new String(msgObjBase.getCmdData()));
                        XLog.get().e("GetAllTags222====" + jsonStr2);
                        tags = (Tags) JSON.parseObject(jsonStr2, Tags.class);
                        if (this.rfidReadCallback != null) {
                            this.rfidReadCallback.OnGetAllTags(tags);
                        }
//                                sendCmd(new MsgObj_RFID_ClearTempTags());
                        break;
                }
            }
        }
        Logger.info("状态码=======" + msgObjBase.getCmdType().name());
        if (msgObjBase.getCmdType() == ECmdType.Lock) {
            if (this.lockStatusCallback != null) {
                if (msgObjBase.getCmdTag() == ELock.OpenLock) {
                    this.lockStatusCallback.OnOpenLock();
                }
                if (msgObjBase.getCmdTag() == ELock.CloseLock) {
                    this.lockStatusCallback.OnCloseLock();
                }
            }
        }
        if (cmdList.size() > 0) {
            for (int i = 0; i < cmdList.size(); i++) {
                Logger.info("执行一条命令");
                Logger.info(cmdList.get(i));
                sendCmd(cmdList.get(i));
            }
            cmdList.clear();
        }
    }

    private byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];

        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }
}