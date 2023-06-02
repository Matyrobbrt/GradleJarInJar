/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.util;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static byte[] readAllBytes(InputStream is) throws IOException {
        return org.apache.commons.io.IOUtils.toByteArray(is);
    }
}
