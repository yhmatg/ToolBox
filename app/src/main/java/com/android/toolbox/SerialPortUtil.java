package com.android.toolbox;

import android.util.Log;

import com.gg.reader.api.utils.HexUtils;
import com.gxwl.device.reader.dal.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SerialPortUtil {
    private static String TAG = "SerialPortUtil";
    private static SerialPortUtil mSerialPortUtil;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private Thread receiveThread = null;
    //开锁动作后开始轮询锁状态
    private boolean isStart;
    private String serialData;
    private List<String> openData = new ArrayList<>();
    private List<String> closeData = new ArrayList<>();
    private boolean isLock = true;
    HashSet<String> openLocks = new HashSet<>();
    HashSet<String> closeLocks = new HashSet<>();

    /**
     * 打开串口的方法
     */
    private SerialPortUtil() {
        //5A0801010001530D
        openData.add("5A0801030001510D");
        closeData.add("5A0801030001500D");
    }

    public static SerialPortUtil getInstance() {
        if (mSerialPortUtil == null) {
            mSerialPortUtil = new SerialPortUtil();
        }
        return mSerialPortUtil;
    }

    public void openSrialPort(String pathName, int baudRate) {
        Log.i(TAG, "打开串口");
        try {
            SerialPort serialPort = new SerialPort(new File(pathName), baudRate, 0);
            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭串口的方法
     * 关闭串口中的输入输出流
     * 然后将flag的值设为flag，终止接收数据线程
     */
    public void closeSerialPort() {
        Log.i(TAG, "关闭串口");
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送串口数据的方法
     *
     * @param data 要发送的数据
     */
    public void sendSerialPort(String data) {
        Log.i(TAG, "发送串口数据");
        try {
            //byte[] sendData = data.getBytes();
            byte[] sendData = HexUtils.hexString2Bytes(data.trim());
            outputStream.write(sendData);
            outputStream.flush();
            Log.i(TAG, "串口数据发送成功");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "串口数据发送失败");
        }
    }

    /**
     * 接收串口数据的方法
     */
    public void receiveSerialPort() {
        Log.i(TAG, "接收串口数据");
        isStart = true;
        openLocks.clear();
        closeLocks.clear();
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (isStart) {
                    try {
                        byte[] readData = new byte[1024];
                        if (inputStream == null) {
                            return;
                        }
                        int size = inputStream.read(readData);
                        if (size > 0 && isStart) {
                            serialData = HexUtils.bytes2HexString(readData, 0, size);
                            Log.i(TAG, "接收到串口数据:" + HexUtils.bytes2HexString(readData, 0, size));
                            if (lockListener != null) {
                                if (closeData.contains(serialData)) {
                                    closeLocks.add(serialData);
                                    if (!isLock && isAllOpen(closeLocks)) {
                                        lockListener.onCloseLock();
                                        isLock = true;
                                        isStart = false;
                                    }
                                } else if (openData.contains(serialData)) {
                                    openLocks.add(serialData);
                                    if (isLock && isAllClose(openLocks)) {
                                        lockListener.onOpenLock();
                                        isLock = false;
                                    }
                                }
                            }
                            Thread.sleep(500);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //启动接收线程
        receiveThread.start();
    }

    private OnLockStatusChangeListener lockListener;

    public interface OnLockStatusChangeListener {
        void onCloseLock();

        void onOpenLock();
    }

    public void setLockListener(OnLockStatusChangeListener lockListener) {
        this.lockListener = lockListener;
    }

    private boolean isAllClose(HashSet<String> closeLocks) {
        for (String openDatum : closeData) {
            if (!closeLocks.contains(openDatum)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllOpen(HashSet<String> openLocks) {
        for (String openDatum : openData) {
            if (!openLocks.contains(openDatum)) {
                return false;
            }
        }
        return true;
    }
}
