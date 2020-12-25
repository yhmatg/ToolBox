package com.android.toolbox.skrfidbox;
import com.android.toolbox.skrfidbox.callback.ILockStatusCallback;
import com.android.toolbox.skrfidbox.callback.IRfidReadCallback;
import com.android.toolbox.skrfidbox.econst.ELock;
import com.android.toolbox.skrfidbox.entity.MsgObjBase;
import com.android.toolbox.skrfidbox.entity.Tags;

public class RfidBoxDemoApplication {
    public static void main(String[] args) {
        ServerThread serverThread = new ServerThread(5460, 3 * 100000);
        serverThread.start();
        try {
            Thread.sleep(10 * 1000);
            //盘点次数，每次1秒，默认最少5秒，因为思科认为只有连续盘点至少5次才能盘点全
            int invTimes = 5;
            while (true) {
                serverThread.getTaskThread().sendStartReadTagsCmd(invTimes, new IRfidReadCallback() {

                    @Override
                    public void OnReceiveData(MsgObjBase msgObj) {

                    }

                    @Override
                    public void OnNotifyReadData(Tags tags) {
                        Logger.info("OnNotifyReadData");
                        Logger.info(tags);
                    }

                    @Override
                    public void OnGetAllTags(Tags tags) {
                        Logger.info("OnGetAllTags");
                        Logger.info(tags);
                    }
                });
                serverThread.getTaskThread().sendLockCmd(ELock.OpenLock, new ILockStatusCallback() {
                    @Override
                    public void OnOpenLock() {
                        Logger.info("OnOpenLock");
                    }

                    @Override
                    public void OnCloseLock() {
                        Logger.info("OnCloseLock");
                    }
                });
                Thread.sleep(50 * 1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
