/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.task

import com.matyrobbrt.gradle.jarinjar.util.PrivateJavaCalls
import groovy.transform.CompileStatic
import com.matyrobbrt.gradle.jarinjar.data.JiJDependencyData
import com.matyrobbrt.gradle.jarinjar.data.JiJDependency
import com.matyrobbrt.gradle.jarinjar.data.JiJDependency.Provider
import com.matyrobbrt.gradle.jarinjar.transform.TransformerManager
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.util.internal.ConfigureUtil

import java.nio.file.Files
import java.util.concurrent.Callable
import java.util.function.Predicate

@CompileStatic
abstract class AbstractJarInJarTask extends Jar {
    public final CopySpec jarJarCopySpec
    protected final TransformerManager transformerManager = TransformerManager.forFile(new File(project.buildDir, "$name/transformers.out"))

    protected final List<Provider> providers = []

    AbstractJarInJarTask() {
        super()
        this.jarJarCopySpec = createJiJCopySpec()
    }

    @Input
    @Optional
    abstract Property<String> getJarDestinationDirectory()
    protected CopySpec createJiJCopySpec() {
        return mainSpec.addChild().into(new Callable<String>() {
            @Override
            String call() throws Exception {
                return jarDestinationDirectory.get()
            }
        })
    }

    @Override
    @TaskAction
    protected void copy() {
        PrivateJavaCalls.renameFromMap(jarJarCopySpec, fileNames)
        jarJarCopySpec.from(includedFiles)
        super.copy()
    }

    void fromConfiguration(Configuration... configurations) {
        provider(new JiJDependency.ConfigurationDependencyProvider(project, List.of(configurations)))
    }

    void fromJar(Jar jar, @DelegatesTo(JiJDependencyData) Closure configuration = null) {
        provider(JiJDependency.fromJar(() -> jar, ConfigureUtil.configure(configuration, new JiJDependencyData())))
        dependsOn(jar)
    }

    void fromJar(TaskProvider<? extends Task> jar, @DelegatesTo(JiJDependencyData) Closure configuration = null) {
        provider(JiJDependency.fromJar(() -> (Jar)jar.get(), ConfigureUtil.configure(configuration, new JiJDependencyData())))
        dependsOn(jar)
    }

    <T extends Provider> T provider(T provider) {
        providers.add(provider)
        return provider
    }

    protected FileCollection includedFiles
    @InputFiles
    FileCollection getIncludedFiles() {
        if (this.@includedFiles === null) {
            return this.@includedFiles = project.files(providers.stream()
                    .flatMap { it.resolve().stream() }
                    .filter(distinct())
                    .map { getFileAndTransform(it) }
                    .sorted()
                    .toArray())
        }
        return this.@includedFiles
    }

    protected File getFileAndTransform(JiJDependency dependency) {
        if (dependency.transformers().isEmpty()) {
            fileNames[dependency.file().name] = dependency.jarPath()
            return dependency.file()
        }

        final newFilePath = new File(project.buildDir, "$name/transformed/${getNewName(dependency)}")
        if (!newFilePath.exists()) {
            final newFilePathPath = newFilePath.toPath()
            Files.createDirectories(newFilePathPath.parent)
            try (final stream = dependency.file().newInputStream()) {
                Files.write(newFilePathPath, applyTransformers(stream, dependency))
            }
        }
        return newFilePath
    }

    protected final Map<String, String> fileNames = [:]
    protected String getNewName(JiJDependency dependency) {
        final fileName = transformerManager.getName(dependency)
        fileNames[fileName] = dependency.jarPath()
        return fileName
    }

    private static byte[] applyTransformers(InputStream input, JiJDependency dependency) {
        byte[] bytes = input.readAllBytes()
        dependency.transformers().each {
            bytes = it.transform(dependency, bytes)
        }
        return bytes
    }

    protected static Predicate<JiJDependency> distinct() {
        Set<File> files = new HashSet<>()
        Map<String, String> versions = new HashMap<>()
        return { JiJDependency dep ->
            if (!files.add(dep.file())) {
                println 'Removing duplicate: ' + dep
                return false
            }
            String oldVersion = versions.put(dep.group() + ':' + dep.artifact(), dep.version())
            if (oldVersion) {
                if (new DefaultArtifactVersion(oldVersion) < new DefaultArtifactVersion(dep.version())) {
                    return true
                }
                // Keep the old version in case it is greater than the current
                versions.put(dep.group() + ':' + dep.artifact(), oldVersion)
                return false
            }
            return true
        } as Predicate<JiJDependency>
    }
}