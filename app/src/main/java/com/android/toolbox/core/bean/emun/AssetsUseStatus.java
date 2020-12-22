package com.android.toolbox.core.bean.emun;

/**
 * @Auther: lijiahang
 * @Date: 2019/2/14 16:36
 * @Description:
 */
public enum AssetsUseStatus {
    IN_IDEL("闲置",0),
    IN_USED("在用", 1),
    IN_REPAIR("维修中", 2),
    IN_Transfer("调拨中",3),
    WAIT_DISTRIBUTE("待派发",4),
    ALREADY_DISTRIBUTE("已派发",5),
    IN_BORROW("借用",6),
    IN_BORROW_APPROVE("借用审批中",7),
    IN_RETURN_APPROVE("归还审批中",8),
    IN_REPAIR_APPROVE("维修审批中",9),
    IN_SCRAP("报废",10),
    IN_TRANSFER_APPROVE("调拨审批中",11),
    IN_SCRAP_APPROVE("报废审批中",12),
    IN_RECIVE_APPROVE("领用审批中",13),
    IN_WITHDRAWAL_APPROVE("退库审批中",14);


    // 成员变量
    private String name;
    private int index;

    private AssetsUseStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index) {
        for (AssetsUseStatus c : AssetsUseStatus.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
//    public static AssetsStatus getByName(String name) {
//        for (AssetsStatus c : AssetsStatus.values()) {
//            if (c.getName().equals(name)) {
//                return c;
//            }
//        }
//        return null;
//    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
