/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.util.mimicking;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedModuleVersion;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Objects;

@NonNullApi
public final class ResolvedArtifactImpl implements ResolvedArtifact {
    private final File file;
    private final ResolvedModuleVersion moduleVersion;
    private final String name;
    private final String type;
    private final String extension;
    private final String classifier;
    private final ComponentArtifactIdentifier identifier;

    public ResolvedArtifactImpl(
            File file, ResolvedModuleVersion moduleVersion,
            String name, String type, String extension, String classifier,
            ComponentArtifactIdentifier identifier
    ) {
        this.file = file;
        this.moduleVersion = moduleVersion;
        this.name = name;
        this.type = type;
        this.extension = extension;
        this.classifier = classifier;
        this.identifier = identifier;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public ResolvedModuleVersion getModuleVersion() {
        return moduleVersion;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Nullable
    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public ComponentArtifactIdentifier getId() {
        return identifier;
    }

    public File file() {
        return file;
    }

    public ResolvedModuleVersion moduleVersion() {
        return moduleVersion;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public String extension() {
        return extension;
    }

    public String classifier() {
        return classifier;
    }

    public ComponentArtifactIdentifier identifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ResolvedArtifactImpl that = (ResolvedArtifactImpl) obj;
        return Objects.equals(this.file, that.file) &&
                Objects.equals(this.moduleVersion, that.moduleVersion) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.extension, that.extension) &&
                Objects.equals(this.classifier, that.classifier) &&
                Objects.equals(this.identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, moduleVersion, name, type, extension, classifier, identifier);
    }

    @Override
    public String toString() {
        return "ResolvedArtifactImpl[" +
                "file=" + file + ", " +
                "moduleVersion=" + moduleVersion + ", " +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "extension=" + extension + ", " +
                "classifier=" + classifier + ", " +
                "identifier=" + identifier + ']';
    }

}
