/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.transform

import com.matyrobbrt.gradle.jarinjar.data.JiJDependency
import groovy.transform.CompileStatic

import javax.annotation.Nullable

@CompileStatic
interface ArtifactTransformer {
    byte[] transform(JiJDependency dependency, byte[] dependencyBytes)

    /**
     * Return {@code null} when this transformer should not be cached.
     */
    @Nullable
    String hash()
}