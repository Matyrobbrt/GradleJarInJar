/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.data;

import com.matyrobbrt.gradle.jarinjar.transform.ArtifactTransformer;
import com.matyrobbrt.gradle.jarinjar.util.DependencyUtils;
import com.matyrobbrt.gradle.jarinjar.JarInJarExtension;
import com.matyrobbrt.gradle.jarinjar.util.mimicking.ComponentArtifactIdentifierImpl;
import com.matyrobbrt.gradle.jarinjar.util.mimicking.ResolvedArtifactImpl;
import com.matyrobbrt.gradle.jarinjar.util.mimicking.ResolvedModuleVersionImpl;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.jvm.tasks.Jar;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class JiJDependency {
    private final String group;
    private final String artifact;
    private final String version;
    private final String range;
    private final boolean includeMetadata;
    private final boolean isObfuscated;
    private final String jarPath;
    private final File file;
    private final List<ArtifactTransformer> transformers;

    public JiJDependency(
            String group, String artifact, String version, String range,
            boolean includeMetadata, boolean isObfuscated, String jarPath,
            File file, List<ArtifactTransformer> transformers
    ) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
        this.range = range;
        this.includeMetadata = includeMetadata;
        this.isObfuscated = isObfuscated;
        this.jarPath = jarPath;
        this.file = file;
        this.transformers = transformers;
    }

    public static class Provider {
        private List<JiJDependency> cached;
        @Nonnull
        protected Supplier<List<JiJDependency>> supplier;

        public Provider(Supplier<List<JiJDependency>> supplier) {
            this.supplier = supplier;
        }

        public final List<JiJDependency> resolve() {
            if (cached != null) return cached;
            return cached = supplier.get();
        }
    }

    public static final class ConfigurationDependencyProvider extends Provider {
        private final JarInJarExtension extension;

        @SuppressWarnings("PatternVariableCanBeUsed")
        public ConfigurationDependencyProvider(Project project, List<Configuration> configurations) {
            super(null);
            this.extension = project.getExtensions().getByType(JarInJarExtension.class);
            this.supplier = () -> {
                final List<JiJDependency> dependencies = new ArrayList<>();
                for (final Configuration configuration : configurations) {
                    for (final Dependency dep : configuration.getAllDependencies()) {
                        if (dep instanceof ModuleDependency) {
                            final ModuleDependency moduleDependency = (ModuleDependency) dep;
                            for (final ResolvedArtifact artifact : DependencyUtils.getResolvedDependency(project, moduleDependency).getAllModuleArtifacts()) {
                                dependencies.add(fromArtifact(configuration, moduleDependency, artifact));
                            }
                        }
                    }
                }
                return dependencies;
            };
        }

        private JiJDependency fromArtifact(Configuration configuration, ModuleDependency dependency, ResolvedArtifact artifact) {
            return extension.resolve(dependency, configuration)
                    .build(artifact, dependency);
        }
    }

    public static Provider fromJar(Supplier<Jar> jarTask, JiJDependencyData dependencyData) {
        return new Provider(() -> Collections.singletonList(dependencyData.build(
                artifactFromJar(jarTask.get()), null
        )));
    }

    private static ResolvedArtifact artifactFromJar(Jar jar) {
        return new ResolvedArtifactImpl(
                jar.getArchiveFile().get().getAsFile(),
                new ResolvedModuleVersionImpl(
                        jar.getProject().getGroup().toString(),
                        jar.getArchiveBaseName().get(),
                        jar.getArchiveVersion().get()
                ),
                jar.getArchiveBaseName().get(),
                "jar", jar.getArchiveExtension().get(),
                jar.getArchiveClassifier().get(),
                new ComponentArtifactIdentifierImpl(jar.getArchiveFileName().get())
        );
    }

    public String group() {
        return group;
    }

    public String artifact() {
        return artifact;
    }

    public String version() {
        return version;
    }

    public String range() {
        return range;
    }

    public boolean includeMetadata() {
        return includeMetadata;
    }

    public boolean isObfuscated() {
        return isObfuscated;
    }

    public String jarPath() {
        return jarPath;
    }

    public File file() {
        return file;
    }

    public List<ArtifactTransformer> transformers() {
        return transformers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        JiJDependency that = (JiJDependency) obj;
        return Objects.equals(this.group, that.group) &&
                Objects.equals(this.artifact, that.artifact) &&
                Objects.equals(this.version, that.version) &&
                Objects.equals(this.range, that.range) &&
                this.includeMetadata == that.includeMetadata &&
                this.isObfuscated == that.isObfuscated &&
                Objects.equals(this.jarPath, that.jarPath) &&
                Objects.equals(this.file, that.file) &&
                Objects.equals(this.transformers, that.transformers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, artifact, version, range, includeMetadata, isObfuscated, jarPath, file, transformers);
    }

    @Override
    public String toString() {
        return "JiJDependency[" +
                "group=" + group + ", " +
                "artifact=" + artifact + ", " +
                "version=" + version + ", " +
                "range=" + range + ", " +
                "includeMetadata=" + includeMetadata + ", " +
                "isObfuscated=" + isObfuscated + ", " +
                "jarPath=" + jarPath + ", " +
                "file=" + file + ", " +
                "transformers=" + transformers + ']';
    }

}
