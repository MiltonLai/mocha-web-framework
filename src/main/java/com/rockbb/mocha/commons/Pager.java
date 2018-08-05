package com.rockbb.mocha.commons;

import java.io.Serializable;

public class Pager implements Serializable {
    public static final String[] ORDERS = {"DESC", "ASC"};
    public static final int DESC = 0;
    public static final int ASC = 1;

    private int offset;
    private int limit;
    private int sort;
    private String order;
    private int sort2;
    private String order2;

    public Pager() {
        this(0, 20, 0, 0);
    }

    public Pager(int limit) {
        this(0, limit, 0, 0);
    }

    public Pager(int offset, int limit) {
        this(offset, limit, 0, 0);
    }

    public Pager(int offset, int limit, int sort, int order) {
        this.offset = (offset < 0)? 0 : offset;
        this.limit = (limit <= 0)? 20 : limit;
        this.sort = sort;
        this.order = (order == DESC)? ORDERS[0] : ORDERS[1];
    }

    public Pager(int offset, int limit, int sort, int order, int sort2, int order2) {
        this.offset = (offset < 0)? 0 : offset;
        this.limit = (limit <= 0)? 20 : limit;
        this.sort = sort;
        this.order = (order == DESC)? ORDERS[0] : ORDERS[1];
        this.sort2 = sort2;
        this.order2 = (order2 == DESC)? ORDERS[0] : ORDERS[1];
    }

    public Pager max() {
        this.limit = Integer.MAX_VALUE - 1;
        return this;
    }

    public int getOffset() { return offset; }
    public int getLimit() { return limit; }
    public int getSort() { return sort; }
    public int getSort2() { return sort2; }
    public String getOrder() { return order; }
    public String getOrder2() { return order2; }
}