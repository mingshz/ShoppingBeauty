package com.ming.shopping.beauty.service.utils;

import java.math.BigDecimal;

/**
 * 常量定义
 * Created by helloztt on 2017/12/21.
 */
public class Constant {
    public static final String UTF8_ENCODIND = "UTF-8";

    public static final String DATE_COLUMN_DEFINITION = "timestamp";
    public static final int FLOAT_COLUMN_SCALE = 2;
    public static final int FLOAT_COLUMN_PRECISION = 12;
    /**
     * 银行家舍入发
     */
    public static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
}
