/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.transform

import com.matyrobbrt.gradle.jarinjar.data.JiJDependency
import groovy.transform.CompileStatic

@CompileStatic
interface ArtifactTransformer {
    byte[] transform(JiJDependency dependency, byte[] dependencyBytes)

    String hash()
}