/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.util.mimicking;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.component.ComponentIdentifier;

import java.util.Objects;

@NonNullApi
public final class ComponentArtifactIdentifierImpl implements ComponentArtifactIdentifier {
    private final ComponentIdentifier componentIdentifier;
    private final String displayName;

    public ComponentArtifactIdentifierImpl(
            ComponentIdentifier componentIdentifier, String displayName
    ) {
        this.componentIdentifier = componentIdentifier;
        this.displayName = displayName;
    }

    public ComponentArtifactIdentifierImpl(String displayName) {
        this(() -> displayName, displayName);
    }

    @Override
    public ComponentIdentifier getComponentIdentifier() {
        return componentIdentifier;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public ComponentIdentifier componentIdentifier() {
        return componentIdentifier;
    }

    public String displayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ComponentArtifactIdentifierImpl that = (ComponentArtifactIdentifierImpl) obj;
        return Objects.equals(this.componentIdentifier, that.componentIdentifier) &&
                Objects.equals(this.displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentIdentifier, displayName);
    }

    @Override
    public String toString() {
        return "ComponentArtifactIdentifierImpl[" +
                "componentIdentifier=" + componentIdentifier + ", " +
                "displayName=" + displayName + ']';
    }

}
