/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.util.mimicking;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.component.ComponentIdentifier;

@NonNullApi
public record ComponentArtifactIdentifierImpl(
        ComponentIdentifier componentIdentifier, String displayName
) implements ComponentArtifactIdentifier {
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
}
