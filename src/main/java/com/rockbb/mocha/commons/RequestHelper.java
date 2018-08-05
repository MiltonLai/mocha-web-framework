package com.rockbb.mocha.commons;

import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestHelper {
    private HttpServletRequest request;
    private String ip;
    private String userAgent;
    private String redirect;

    public RequestHelper(
            HttpServletRequest request) {
        this.request = request;
        this.ip = InetAddressUtil.getAddressFromRequest(request);
        this.userAgent = request.getHeader("User-Agent");

        redirect = request.getParameter("redirect");
        if (redirect == null || redirect.equals("")) {
            redirect = request.getHeader("Referer");
        }
        // If the redirect link is a middle page, set it to empty
        if (redirect != null && redirect.matches(".*\\/(message|login|logout)\\.\\w{1,4}")) {
            redirect = null;
        }
    }

    public HttpServletRequest getRequest() { return request; }
    public String getIp() { return ip; }
    public String getUserAgent() { return userAgent; }
    public String getRedirect() {return redirect;}
    public String getPath() {return request.getContextPath() + request.getServletPath();}

    public Pager getPager() {
        return new Pager(getInt("offset"), getInt("limit", 20), getInt("sort"), getInt("order"), getInt("sort2"), getInt("order2"));
    }

	/* HTTP request parameters handlers */

    public String getParameter(String key) {
        return request.getParameter(key);
    }

    public String getHeader(String key) {
        return request.getHeader(key);
    }

    public Object get(String key, Class clazz) {
        if (clazz.equals(String.class)) {
            return get(key);
        } else if (clazz.equals(BigDecimal.class)) {
            return getBigDecimal(key);
        } else if (clazz.equals(Date.class)) {
            return getDate(key);
        } else if (clazz.equals(int.class)) {
            return getInt(key);
        } else if (clazz.equals(long.class)) {
            return getLong(key);
        } else if (clazz.equals(char.class)) {
            return getChar(key, (char)0);
        } else if (clazz.equals(boolean.class)) {
            return getBoolean(key, null);
        } else if (clazz.equals(byte.class)) {
            return (byte)0;
        } else if (clazz.equals(short.class)) {
            return (short)getInt(key);
        } else if (clazz.equals(float.class)) {
            return getFloat(key, 0f);
        } else if (clazz.equals(double.class)) {
            return getDouble(key, 0d);
        } else {
            return null;
        }
    }

    /**
     * 获取request中的字符串数组参数
     *
     * @param key 参数名
     * @param default_array 值为空的情况下的默认数组
     * @return 数组
     */
    public String[] getArray(String key, String[] default_array) {
        String[] array = request.getParameterValues(key);
        if (array == null || array.length == 0) {
            array = default_array;
        }
        return array;
    }

    /**
     * 获取request中的字符串数组参数
     *
     * @param key 参数名
     * @param default_array 值为空的情况下的默认数组
     * @param candidates 取值范围, 不在这个范围内的会被过滤掉, 不能为空
     * @return 数组
     */
    public String[] getArray(String key, String[] default_array, String[] candidates) {
        String[] array = request.getParameterValues(key);
        if (array == null || array.length == 0) {
            array = default_array;
        }
        List<String> arrayList = Arrays.asList(array);
        Arrays.sort(candidates); // 首先对数组排序
        for (int i = 0; i < arrayList.size();) {
            if (Arrays.binarySearch(candidates, arrayList.get(i)) < 0)
                arrayList.remove(i);
            else
                i++;
        }
        return arrayList.toArray(new String[]{});
    }

    /**
     * 获取request中的整数数组参数
     *
     * @param key 参数名
     * @param default_array 值为空的情况下的默认数组
     * @param candidates 取值范围, 不在这个候选集合内的会被过滤掉, 为空则不限制
     * @return 数组
     */
    public int[] getIntArray(String key, int[] default_array, Integer[] candidates) {
        String[] array = request.getParameterValues(key);
        if (array == null || array.length == 0) { return default_array; }

        int[] ints = new int[array.length];
        int pos = 0;
        if (candidates != null) {
            Arrays.sort(candidates);
            for (int i = 0; i < array.length; i++) {
                int value = 0;
                try {
                    value = Integer.parseInt(array[i]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (Arrays.binarySearch(candidates, value) >= 0) {
                    ints[pos] = value;
                    pos++;
                }
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                int value = 0;
                try {
                    value = Integer.parseInt(array[i]);
                } catch (NumberFormatException e) {
                    continue;
                }

                ints[pos] = value;
                pos++;
            }
        }

        return ArrayUtils.subarray(ints, 0, pos);
    }

    /**
     * 获取request中的整数数组参数
     *
     * @param key 参数名
     * @param default_array 值为空的情况下的默认数组
     * @param min	取值范围的下限(含), minimum value, included
     * @param max	取值范围的上限(含), maximum value, included
     * @return 数组
     */
    public int[] getIntArray(String key, int[] default_array, int min, int max) {
        String[] array = request.getParameterValues(key);
        if (array == null || array.length == 0) { return default_array; }
        int[] ints = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            int value = 0;
            try {
                value = Integer.parseInt(array[i]);
            } catch (NumberFormatException e) {
                continue;
            }

            if (value < min)
                ints[i] = min;
            else if (value > max)
                ints[i] = max;
            else
                ints[i] = value;
        }
        return ints;
    }

    /**
     * 根据输入的时间格式, 自动选择格式字符串并转换为Date类型数据
     */
    public Date getDate(String key) {
        String str = get(key);
        if (str == null || str.length() == 0) return null;
        return TimeUtil.getDate(str);
    }

    /**
     * 读取 2016-01-01 12:30:30 格式的时间
     */
    public Date getDateYmdhms(String key) {
        return TimeUtil.getDate(get(key), TimeUtil.FORMAT_YMD_HMS);
    }

    /**
     * 读取 2016-01-01 格式的日期
     */
    public Date getDateYmd(String key) {
        return TimeUtil.getDate(get(key), TimeUtil.FORMAT_YMD);
    }

    /**
     * 读取 2016-01-01 格式的日期
     * @param boundary -1:左边界, 0:不变, 1:右边界
     */
    public Date getDateYmd(String key, int boundary) {
        Date date = TimeUtil.getDate(get(key), TimeUtil.FORMAT_YMD);
        if (date != null) {
            if (boundary == -1) {
                return TimeUtil.getDayStart(date);
            } else if (boundary == 1) {
                return TimeUtil.getDayEnd(date);
            }
        }
        return date;
    }

    /**
     * 读取自定义格式的时间
     */
    public Date getDate(String key, String format) {
        return TimeUtil.getDate(get(key), format);
    }

    /**
     * 读取request中的BigDecimal参数, 值为空或出现异常情况下返回0
     *
     * @param key 参数名
     * @return BigDecimal
     */
    public BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, BigDecimal.ZERO, -1);
    }

    public BigDecimal getBigDecimal(String key, int scale) {
        return getBigDecimal(key, BigDecimal.ZERO, scale);
    }

    /**
     * 读取request中的BigDecimal参数
     *
     * @param key 参数名
     * @param defaultValue 值为空或出现异常情况下的默认值
     * @param scale 精度限制, -1为不限制
     * @return BigDecimal
     */
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue, int scale) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            DecimalFormat df = new DecimalFormat();
            df.setParseBigDecimal(true);
            BigDecimal value = (BigDecimal)df.parse(str);
            if (scale > -1 && value.scale() > scale) {
                value = value.setScale(scale, BigDecimal.ROUND_HALF_UP);
            }
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的浮点数参数
     *
     * @param key 参数名
     * @param defaultValue 为空时的默认值
     * @return 浮点值
     */
    public double getDouble(String key, double defaultValue) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            return Double.parseDouble(str.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的浮点数参数, 值必须在candidates中, 否则使用默认值
     *
     * @param key 参数名
     * @param defaultValue 参数为空时的默认值
     * @param candidates 取值范围, 不在这个候选集合内的会被过滤掉, 为空则不限制
     * @return 浮点值
     */
    public double getDouble(String key, double defaultValue, double[] candidates) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            double value = Double.parseDouble(str.trim());
            Arrays.sort(candidates);
            if (Arrays.binarySearch(candidates, value) < 0) {
                return defaultValue;
            }
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的浮点数参数, 值必须在(min, max)区间中, 否则使用默认值
     *
     * @param key 参数名
     * @param defaultValue 参数为空的情况下的默认值
     * @param min	取值范围的下限(含), minimum value, included
     * @param max	取值范围的上限(含), maximum value, included
     * @return 浮点值
     */
    public double getDouble(String key, double defaultValue, double min, double max) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            double value = Double.parseDouble(str.trim());
            if (value < min || value > max) {
                value = defaultValue;
            }
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的浮点数参数
     *
     * @param key 参数名
     * @param defaultValue 为空时的默认值
     * @return 浮点值
     */
    public Float getFloat(String key, float defaultValue) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            return Float.parseFloat(str.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的浮点数参数, 值必须在candidates中, 否则使用默认值
     *
     * @param key 参数名
     * @param defaultValue 参数为空时的默认值
     * @param candidates 取值范围, 不在这个候选集合内的会被过滤掉, 为空则不限制
     * @return 浮点值
     */
    public Float getFloat(String key, float defaultValue, float[] candidates) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            float value = Float.parseFloat(str.trim());
            Arrays.sort(candidates);
            if (Arrays.binarySearch(candidates, value) < 0) {
                return defaultValue;
            }
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的浮点数参数, 值必须在(min, max)区间中, 否则使用默认值
     *
     * @param key 参数名
     * @param defaultValue 参数为空的情况下的默认值
     * @param min	取值范围的下限(含), minimum value, included
     * @param max	取值范围的上限(含), maximum value, included
     * @return 浮点值
     */
    public Float getFloat(String key, float defaultValue, float min, float max) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            float value = Float.parseFloat(str.trim());
            if (value < min || value > max) {
                return defaultValue;
            }
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的整数参数, 默认为0
     *
     * @param key 参数名
     * @return 整数
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 获取request中的整数参数
     *
     * @param key 参数名
     * @param defaultValue 参数为空时的默认值
     * @return 整数
     */
    public int getInt(String key, int defaultValue) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的整数参数, 取值必须是candidates中的某一项
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @param candidates 候选值, 不在候选里的会被滤掉. 如果为空则不限制
     * @return 整数
     */
    public int getInt(String key, int defaultValue, int[] candidates) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            int value = Integer.parseInt(str.trim());
            Arrays.sort(candidates);
            if (Arrays.binarySearch(candidates, value) < 0) {
                return defaultValue;
            }
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的整数参数, 取值必须在min和max之间
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @param min 最小值(含)
     * @param max 最大值(含)
     * @return 整数
     */
    public int getInt(String key, int defaultValue, int min, int max) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            int value = Integer.parseInt(str.trim());
            if (value < min) {
                value = min;
            } else if (value > max) {
                value = max;
            }
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的长整数参数, 如果为空则使用0L
     *
     * @param key 参数名
     * @return 整数
     */
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * 获取request中的长整数参数, 如果为空则使用默认值
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @return 长整数
     */
    public long getLong(String key, long defaultValue) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) return defaultValue;
        try {
            return Long.parseLong(str.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取request中的长整数数组, 如果为空则返回默认数组
     *
     * @param key 参数名
     * @param default_array 默认数组
     * @param candidates 候选值, 如果为空则无限制, can be null if there is no limit
     * @return 长整数数组
     */
    public long[] getLongArray(String key, long[] default_array, long[] candidates) {
        String[] array = request.getParameterValues(key);
        if (array == null || array.length == 0) { return default_array; }

        long[] longs = new long[array.length];
        int pos = 0;
        if (candidates != null) {
            Arrays.sort(candidates);
            for (int i = 0; i < array.length; i++) {
                long value = 0L;
                try {
                    value = Long.parseLong(array[i]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (Arrays.binarySearch(candidates, value) >= 0) {
                    longs[pos] = value;
                    pos++;
                }
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                long value = 0;
                try {
                    value = Long.parseLong(array[i]);
                } catch (NumberFormatException e) {
                    continue;
                }
                longs[pos] = value;
                pos++;
            }
        }

        return ArrayUtils.subarray(longs, 0, pos);
    }

    /**
     * 获取request中的字符串参数, 自动转换为指定的编码, 默认为空字符串
     *
     * @param key 参数名
     * @param encoding 参数编码
     * @param serverEncoding WEB容器编码
     * @return 字符串
     */
    public String getUnicode(String key, String encoding, String serverEncoding) {
        return getUnicode(key, encoding, serverEncoding, "");
    }

    /**
     * 获取request中的字符串参数, 自动转换为指定的编码
     *
     * @param key 参数名
     * @param encoding 参数编码
     * @param serverEncoding WEB容器编码
     * @param defaultValue 默认值
     * @return 字符串
     */
    public String getUnicode(String key, String encoding, String serverEncoding, String defaultValue) {
        if (serverEncoding.equals(encoding)) { return get(key, defaultValue); }

        try {
            byte[] tmp = get(key).getBytes(serverEncoding);
            return new String(tmp, encoding);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, Boolean defaultValue) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) {
            return defaultValue;
        } else {
            str = str.toLowerCase();
            if (str.equals("true")) {
                return true;
            } else if (str.equals("false")) {
                return false;
            } else {
                return defaultValue;
            }
        }
    }

    public char getChar(String key, char defaultValue) {
        String str = request.getParameter(key);
        if (str == null || str.length() == 0) {
            return defaultValue;
        } else {
            return str.charAt(0);
        }
    }

    /**
     * 获取request中的字符串参数, 默认为空字符串
     *
     * @param key 参数名
     * @return 字符串
     */
    public String get(String key) {
        return get(key, "");
    }

    /**
     * 获取request中的字符串参数
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @return 字符串
     */
    public String get(String key, String defaultValue) {
        String value = request.getParameter(key);
        if (value == null) {
            return defaultValue;
        }
        return value.trim();
    }

    /**
     * 获取request中的字符串参数, 如果不在预设的candidates里面, 则返回默认值
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @param candidates 候选值
     * @return 字符串
     */
    public String get(String key, String defaultValue, String[] candidates) {
        String value = request.getParameter(key);
        if (value == null) {
            return defaultValue;
        }
        if (!Arrays.asList(candidates).contains(value)) {
            return defaultValue;
        }
        return value.trim();
    }
}