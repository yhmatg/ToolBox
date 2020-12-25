package com.android.toolbox.skrfidbox.callback;

import com.android.toolbox.skrfidbox.entity.MsgObjBase;
import com.android.toolbox.skrfidbox.entity.Tags;

/**
 * 回调接口
 */
public interface IRfidReadCallback {

    /**
     * 接收数据时触发
     *
     * @param msgObj 接收到的消息
     */
    void OnReceiveData(MsgObjBase msgObj);

    /**
     * 盘点命令结束后，获取到初步数据时触发
     *
     * @return
     */
    void OnNotifyReadData(Tags tags);

    /**
     * 盘点命令结束后，获取到完整的盘点记录时触发
     *
     * @return
     */
    void OnGetAllTags(Tags tags);

}
