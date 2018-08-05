package com.rockbb.mocha.db;

import java.util.ArrayList;
import java.util.Arrays;

public class ClauseGen {
    public static final int ASC = 0;
    public static final int DESC = 1;

    private StringBuilder sb = new StringBuilder();
    private StringBuilder sb2 = new StringBuilder();
    private ArrayList<Object> params = new ArrayList<>();
    private boolean open = false;

    public ClauseGen where() {
        sb.append("\nWHERE");
        open = true;
        return this;
    }

    public ClauseGen set() {
        sb.append("\nSET");
        open = true;
        return this;
    }

    public ClauseGen and(String clause, Object ... params) {
        if (open) {
            sb.append('\n').append(clause);
        } else {
            sb.append("\nAND ").append(clause);
        }
        if (params != null && params.length > 0) {
            this.params.addAll(Arrays.asList(params));
        }
        this.open = false;
        return this;
    }

    public ClauseGen or(String clause, Object ... params) {
        if (open) {
            sb.append('\n').append(clause);
        } else {
            sb.append("\nOR ").append(clause);
        }
        if (params != null && params.length > 0) {
            this.params.addAll(Arrays.asList(params));
        }
        this.open = false;
        return this;
    }

    public ClauseGen comma(String clause, Object ... params) {
        if (open) {
            sb.append('\n').append(clause);
        } else {
            sb.append(",\n").append(clause);
        }
        if (params != null && params.length > 0) {
            this.params.addAll(Arrays.asList(params));
        }
        this.open = false;
        return this;
    }

    public ClauseGen append(String str1, String str2, Object param) {
        sb.append(str1);
        sb2.append(str2);
        if (param != null) {
            this.params.add(param);
        }
        return this;
    }

    public ClauseGen orderby(String sort, String order) {
        sb.append('\n').append("ORDER BY ").append(sort).append(' ').append(order);
        return this;
    }

    public ClauseGen limt(int offset, int limit) {
        sb.append('\n').append("LIMIT ").append(offset).append(',').append(limit);
        return this;
    }

    public String getSql() {
        return sb.append(sb2).toString();
    }

    public Object[] getParams() {
        return params.toArray();
    }
}
