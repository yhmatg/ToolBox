package com.android.toolbox.core.bean.assist;

import java.util.List;

public class AssetsListPage {

    /**
     * endRow : 3
     * hasNextPage : false
     * hasPreviousPage : false
     * isFirstPage : true
     * isLastPage : true
     * list : [{"create_date":1591168172000,"creator":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"creator_id":"d3cc4af6a6604ccca12f746e1b029412","id":"54455af0cbca4074b0fdb0ce2da90843","maintain_finish_date":1591168194000,"maintain_price":0,"odr_code":"WX20200603000004","odr_date":1591168008000,"odr_remark":"we I oh","odr_status":"已完成","odr_type":"管理员报修","repUser":{"id":"27bb7a684beb4e6bb07f4e5634cdfed4","tenant_isdefault":1,"tenantid":"tenantid5013","user_email":"","user_emp_code":"0001","user_mobile":"18721243175","user_real_name":"gyanyu","user_source":0},"rep_user_id":"27bb7a684beb4e6bb07f4e5634cdfed4","tra_user_id":"d3cc4af6a6604ccca12f746e1b029412","transactor":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"update_date":1591168172000,"updater":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"updater_id":"d3cc4af6a6604ccca12f746e1b029412"},{"create_date":1591163642000,"creator":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"creator_id":"d3cc4af6a6604ccca12f746e1b029412","id":"da381483c875448296bdac28ed591e04","maintain_finish_date":1591163665000,"maintain_price":0,"odr_code":"WX20200603000003","odr_date":1591163600000,"odr_remark":"a","odr_status":"已完成","odr_type":"管理员报修","repUser":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"rep_user_id":"d3cc4af6a6604ccca12f746e1b029412","tra_user_id":"d3cc4af6a6604ccca12f746e1b029412","transactor":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"update_date":1591163642000,"updater":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"updater_id":"d3cc4af6a6604ccca12f746e1b029412"},{"create_date":1591163531000,"creator":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"creator_id":"d3cc4af6a6604ccca12f746e1b029412","id":"d0a1001a0cf0432689038cb60ca1f882","maintain_finish_date":1591163578000,"maintain_price":0,"odr_code":"WX20200603000001","odr_date":1591163474000,"odr_remark":",","odr_status":"已完成","odr_type":"管理员报修","repUser":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"rep_user_id":"d3cc4af6a6604ccca12f746e1b029412","tra_user_id":"d3cc4af6a6604ccca12f746e1b029412","transactor":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"update_date":1591163531000,"updater":{"id":"d3cc4af6a6604ccca12f746e1b029412","tenant_isdefault":1,"tenantid":"tenantid5013","user_age":"","user_email":"","user_emp_code":"123","user_mobile":"18408002925","user_name":"test001","user_real_name":"测试123"},"updater_id":"d3cc4af6a6604ccca12f746e1b029412"}]
     * navigateFirstPage : 1
     * navigateLastPage : 1
     * navigatePages : 8
     * navigatepageNums : [1]
     * nextPage : 0
     * pageNum : 1
     * pageSize : 20
     * pages : 1
     * prePage : 0
     * size : 3
     * startRow : 1
     * total : 3
     */

    private int endRow;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private boolean isFirstPage;
    private boolean isLastPage;
    private int navigateFirstPage;
    private int navigateLastPage;
    private int navigatePages;
    private int nextPage;
    private int pageNum;
    private int pageSize;
    private int pages;
    private int prePage;
    private int size;
    private int startRow;
    private int total;
    private int astCount;
    private double totalMoney;
    private List<AssetsListItemInfo> list;
    private List<Integer> navigatepageNums;
    private boolean isLocal;

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean isIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public int getNavigateFirstPage() {
        return navigateFirstPage;
    }

    public void setNavigateFirstPage(int navigateFirstPage) {
        this.navigateFirstPage = navigateFirstPage;
    }

    public int getNavigateLastPage() {
        return navigateLastPage;
    }

    public void setNavigateLastPage(int navigateLastPage) {
        this.navigateLastPage = navigateLastPage;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public List<AssetsListItemInfo> getList() {
        return list;
    }

    public void setList(List<AssetsListItemInfo> list) {
        this.list = list;
    }

    public List<Integer> getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(List<Integer> navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public int getAstCount() {
        return astCount;
    }

    public void setAstCount(int astCount) {
        this.astCount = astCount;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }
}
