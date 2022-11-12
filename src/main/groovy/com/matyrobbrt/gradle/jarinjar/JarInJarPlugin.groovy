/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class JarInJarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.getExtensions().create('jij', JarInJarExtension.class, project)
    }
}
