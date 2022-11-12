/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.task

import com.matyrobbrt.gradle.jarinjar.data.JiJDependency
import groovy.transform.CompileStatic
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import java.util.stream.Stream

@CompileStatic
abstract class WithMetadataJarInJarTask<Z extends Serializable> extends AbstractJarInJarTask {
    protected abstract Z createMetadata(Stream<JiJDependency> dependencies)
    @Internal
    protected abstract FileCollection getMetadataFiles()

    @Override
    @TaskAction
    protected void copy() {
        jarJarCopySpec.from(metadataFiles)
        super.copy()
    }

    protected Z metadata
    @Input
    Z getMetadata() {
        if (this.@metadata === null) {
            this.@metadata = createMetadata(providers.stream().flatMap { it.resolve().stream() }
                    .filter { it.includeMetadata() }
                    .filter(distinct()))
        }
        return this.@metadata
    }
}
