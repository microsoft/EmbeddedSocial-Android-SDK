/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.test.embeddedsocial.util;

import com.microsoft.test.embeddedsocial.TestConstants;

import java.util.Random;

public class StringUtils {

    private static final Random RNG = new Random();

    private StringUtils() { }

    public static String generateName() {
        return TestConstants.TEST_ACCOUNT_PREFIX + generateString(10);
    }

    public static String generateString(int maxLength) {
        final int MIN_LENGTH = 4;

        int stringLength = generateInRange(MIN_LENGTH, maxLength);
        StringBuilder builder = new StringBuilder(stringLength);
        builder.append(Character.toUpperCase(generateCharacter()));
        for (int i = 1; i < stringLength; ++i) {
            char nextChar = generateCharacter();
            builder.append(nextChar);
        }
        return builder.toString();
    }

    public static char generateCharacter() {
        return (char)generateInRange('a', 'z');
    }

    public static int generateInRange(int start, int end) {

        if (start > end || start < 0 || end < 1) {
            throw new IllegalArgumentException();
        }
        int range = end - start + 1;
        return RNG.nextInt(range) + start;
    }
}
