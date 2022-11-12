/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.data

import com.matyrobbrt.gradle.jarinjar.util.PredicatedConsumers
import groovy.transform.CompileStatic
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.util.internal.ConfigureUtil

import java.util.function.BiConsumer
import java.util.function.Predicate
import java.util.regex.Pattern

@CompileStatic
class JiJConfigurationData {
    private final PredicatedConsumers<ResolvedDependency, JiJDependencyData> dependencyData = new PredicatedConsumers<>()
    final Map<Dependency, JiJDependencyData> byDependencyData = [:]

    void configureDependencies(Predicate<ResolvedDependency> predicate, BiConsumer<ResolvedDependency, JiJDependencyData> creator) {
        dependencyData.add(predicate, creator)
    }

    void eachMatching(String pattern, @DelegatesTo(JiJDependencyData) Closure closure) {
        eachMatching(Pattern.compile(pattern), closure)
    }

    void eachMatching(Pattern pattern, @DelegatesTo(JiJDependencyData) Closure closure) {
        final pred = pattern.asPredicate()
        configureDependencies({
            pred.test(it.moduleGroup + ':' + it.name)
        }) { dep, data ->
            ConfigureUtil.configure(closure, data)
        }
    }

    JiJDependencyData applyData(ResolvedDependency dependency, JiJDependencyData data) {
        dependencyData.apply(dependency).accept(data)
        return data
    }
}
