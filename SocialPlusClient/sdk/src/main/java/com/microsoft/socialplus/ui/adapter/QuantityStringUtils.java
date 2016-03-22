package com.microsoft.socialplus.ui.adapter;

public class QuantityStringUtils {
    private QuantityStringUtils() {}

    /**
     * Converts a non-negative long value to an int
     * @return int value of this long or INTEGER.MAX_VALUE if int type is too small
     */
    public static int convertLongToInt(long value) {
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)value;
    }
}
