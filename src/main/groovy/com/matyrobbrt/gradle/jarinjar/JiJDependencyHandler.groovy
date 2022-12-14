/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar


import com.matyrobbrt.gradle.jarinjar.data.JiJDependencyData
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.gradle.api.artifacts.Dependency

@CompileStatic
class JiJDependencyHandler {
    private final JarInJarExtension extension
    JiJDependencyHandler(JarInJarExtension extension) {
        this.extension = extension
    }

    Dependency add(String configurationName, Object dependencyNotation, @DelegatesTo(
            value = JiJDependencyData, strategy = Closure.DELEGATE_FIRST
    ) @ClosureParams(
        value = SimpleType, options = ['com.matyrobbrt.gradle.jarinjar.data.JiJDependencyData', 'org.gradle.api.artifacts.Dependency']
    ) Closure configuration = null) {
        Dependency baseDependency = extension.project.getDependencies().create(dependencyNotation)
        final JiJDependencyData data = new JiJDependencyData()

        if (configuration !== null) {
            configuration.resolveStrategy = Closure.DELEGATE_FIRST
            configuration.delegate = data
            configuration(data, baseDependency)
        }

        extension.addDependencyData(baseDependency, data)

        extension.project.configurations.getByName(configurationName).getDependencies().add(baseDependency)
        return baseDependency
    }
}
