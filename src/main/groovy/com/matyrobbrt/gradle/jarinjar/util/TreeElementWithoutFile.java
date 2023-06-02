/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.util;

import org.apache.commons.io.IOUtils;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.file.RelativePath;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TreeElementWithoutFile implements FileTreeElement {
    private final boolean isDirectory;
    private final String path;
    @Nullable
    private final InputStream stream;
    private final RelativePath relativePath;

    public TreeElementWithoutFile(boolean isDirectory, String path, @Nullable InputStream stream) {
        this.isDirectory = isDirectory;
        this.path = path;
        this.stream = stream;
        this.relativePath = RelativePath.parse(isDirectory, path);
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public long getLastModified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream open() {
        if (stream == null) {
            throw new UnsupportedOperationException();
        }
        return stream;
    }

    @Override
    public void copyTo(OutputStream outputStream) {
        try {
            IOUtils.copy(open(), outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean copyTo(File file) {
        if (stream == null) {
            throw new UnsupportedOperationException();
        }
        try (final OutputStream outputStream = new FileOutputStream(file)) {
            copyTo(outputStream);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return path;
    }

    @Override
    public String getPath() {
        return getRelativePath().getPathString();
    }

    @Override
    public RelativePath getRelativePath() {
        return relativePath;
    }

    @Override
    public int getMode() {
        return 0;
    }
}
