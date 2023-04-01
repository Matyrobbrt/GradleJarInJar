/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.util.mimicking;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedModuleVersion;

import java.util.Objects;

@NonNullApi
public final class ResolvedModuleVersionImpl implements ResolvedModuleVersion {
    private final ModuleVersionIdentifier id;

    public ResolvedModuleVersionImpl(
            ModuleVersionIdentifier id
    ) {
        this.id = id;
    }

    public ResolvedModuleVersionImpl(String group, String name, String version, ModuleIdentifier module) {
        this(new ModuleVersionIdentifier() {
            @Override
            public String getVersion() {
                return version;
            }

            @Override
            public String getGroup() {
                return group;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ModuleIdentifier getModule() {
                return module;
            }
        });
    }

    public ResolvedModuleVersionImpl(String group, String name, String version) {
        this(group, name, version, new ModuleIdentifier() {
            @Override
            public String getGroup() {
                return group;
            }

            @Override
            public String getName() {
                return name;
            }
        });
    }

    @Override
    public ModuleVersionIdentifier getId() {
        return id;
    }

    public ModuleVersionIdentifier id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ResolvedModuleVersionImpl that = (ResolvedModuleVersionImpl) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ResolvedModuleVersionImpl[" +
                "id=" + id + ']';
    }

}
