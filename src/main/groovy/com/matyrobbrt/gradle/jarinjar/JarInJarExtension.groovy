/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar

import com.matyrobbrt.gradle.jarinjar.util.DependencyUtils
import com.matyrobbrt.gradle.jarinjar.data.JiJConfigurationData
import com.matyrobbrt.gradle.jarinjar.data.JiJDependencyData
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.util.internal.ConfigureUtil

@CompileStatic
abstract class JarInJarExtension {
    @PackageScope final Project project

    JarInJarExtension(Project project) {
        this.project = project
    }

    private final Map<String, JiJConfigurationData> configurationData = [:]
    private final Map<Dependency, JiJDependencyData> dependencyConfigurationData = [:]

    JiJConfigurationData getData(String configurationName) {
        return configurationData.computeIfAbsent(configurationName, { new JiJConfigurationData() })
    }

    JiJDependencyData resolve(ModuleDependency dependency, Configuration depConfiguration) {
        final resolved = DependencyUtils.getResolvedDependency(project, dependency)
        final conf = getData(depConfiguration.name)
        JiJDependencyData configuration = dependencyConfigurationData[dependency] ?: conf.byDependencyData[dependency]
        return conf.applyData(resolved, configuration ?: new JiJDependencyData())
    }

    @CompileDynamic
    void onConfiguration(String name, @DelegatesTo(
            value = JiJConfigurationData, strategy = Closure.DELEGATE_FIRST
    ) Closure closure) {
        final data = configurationData.computeIfAbsent(name, { new JiJConfigurationData() })
        ConfigureUtil.configure(closure, data)
    }

    void onConfiguration(Configuration configuration, @DelegatesTo(
            value = JiJConfigurationData, strategy = Closure.DELEGATE_FIRST
    ) Closure closure) {
        onConfiguration(configuration.name, closure)
    }

    void addDependencyData(Dependency dependency, JiJDependencyData data) {
        dependencyConfigurationData[dependency] = data
    }

    @CompileDynamic
    void onDependency(Dependency dependency, @DelegatesTo(
            value = JiJDependencyData, strategy = Closure.DELEGATE_FIRST
    ) Closure closure) {
        final data = new JiJDependencyData()
        ConfigureUtil.configure(closure, data)
        addDependencyData(dependency, data)
    }

    @CompileDynamic
    void dependencies(@DelegatesTo(
            value = JiJDependencyHandler, strategy = Closure.DELEGATE_FIRST
    ) Closure closure) {
        ConfigureUtil.configure(closure, new JiJDependencyHandler(this))
    }
}