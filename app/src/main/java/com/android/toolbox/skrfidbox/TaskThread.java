package com.android.toolbox.skrfidbox;

import com.alibaba.fastjson.JSON;
import com.android.toolbox.skrfidbox.callback.ILockStatusCallback;
import com.android.toolbox.skrfidbox.callback.IRfidReadCallback;
import com.android.toolbox.skrfidbox.econst.EByteBase;
import com.android.toolbox.skrfidbox.econst.ECmdType;
import com.android.toolbox.skrfidbox.econst.ELock;
import com.android.toolbox.skrfidbox.econst.ERfid;
import com.android.toolbox.skrfidbox.entity.MsgObjBase;
import com.android.toolbox.skrfidbox.entity.MsgObj_HeartBeat;
import com.android.toolbox.skrfidbox.entity.Tags;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

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

    public void run() {
        InputStream inputStream = null;
        DataInputStream dis = null;
        if (!checkAvailable()) {
            return;
        }
        try {
            //必须设置线程超时时长，否则会导致cup过高或者连接通道无法关闭等问题
            socket.setSoTimeout(this.readTimeout);
            // 获取输入流，并读取客户端信息
            inputStream = socket.getInputStream();
            dis = new DataInputStream(inputStream);
            int cacheSize = 1024 * 10;
            byte[] data = new byte[cacheSize];
            while (dis.read(data) > 0) {
                final MsgObjBase msgObjBase = new MsgObjBase(data);
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
                                tags = (Tags) JSON.parseObject(jsonStr, Tags.class);
                                sendGetAllTagsCmd();
                                if (this.rfidReadCallback != null) {
                                    this.rfidReadCallback.OnNotifyReadData(tags);
                                }
                                break;
                            case GetAllTags:
                                String jsonStr2 = DataConverts.Bytes_To_ASCII(msgObjBase.getCmdData());
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
                data = new byte[cacheSize];
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
}