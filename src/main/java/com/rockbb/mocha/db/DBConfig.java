package com.rockbb.mocha.db;

import java.util.ResourceBundle;

public class DBConfig {
    private static ResourceBundle rb = ResourceBundle.getBundle("db");

    public static String getValue(String key) {
        return rb.getString(key);
    }

    public static int getIntValue(String key) {
        int value = 0;
        try {
            value = Integer.parseInt(getValue(key));
        } catch (Exception e) {
            // nothing to do
        }
        return value;
    }

}
