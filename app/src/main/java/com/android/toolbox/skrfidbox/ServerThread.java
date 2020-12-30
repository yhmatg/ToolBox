package com.android.toolbox.skrfidbox;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerThread extends Thread {

    private int port = 5460;
    private ServerSocket serverSocket = null;
    private int readTimeout = 3 * 100000;
    /**
     * 子线程，负责读取数据
     */
    private TaskThread taskThread = null;

    public TaskThread getTaskThread() {
        return taskThread;
    }

    public ServerThread(int port, int readTimeout) {
        this.port = port;
        this.readTimeout = readTimeout;
        try {
            if (null == serverSocket) {
                this.serverSocket = new ServerSocket(port);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动服务
     */
    public void run() {
        while (taskThread == null || !taskThread.isAlive() || !taskThread.isInterrupted() || !taskThread.checkAvailable()) {
            taskThread = new TaskThread(serverSocket);
            taskThread.setReadTimeout(readTimeout);
            taskThread.start();
            //10秒检测一次设备是否还在线，如果不在线重新创建一个线程
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}