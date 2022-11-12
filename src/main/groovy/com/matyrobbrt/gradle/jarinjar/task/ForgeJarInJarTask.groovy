/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.task

import com.matyrobbrt.gradle.jarinjar.data.JiJDependency
import com.matyrobbrt.gradle.jarinjar.util.JiJUtils
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import org.gradle.api.file.ConfigurableFileCollection

import java.nio.file.Files
import java.util.function.Function
import java.util.stream.Stream

@CompileStatic
abstract class ForgeJarInJarTask extends WithMetadataJarInJarTask<HashMap> {
    ForgeJarInJarTask() {
        super()
        jarDestinationDirectory.convention('META-INF/jarjar')
    }

    @Override
    protected HashMap createMetadata(Stream<JiJDependency> dependencies) {
        final jars = dependencies
                .filter(distinct())
                .sorted(Comparator.<JiJDependency, String> comparing({ JiJDependency dep -> dep.group() + ':' + dep.version() } as Function<JiJDependency, String>))
                .map {
                    JiJUtils.makeForgeJarJsonMap(
                            it.group(), it.artifact(), it.version(), it.jarPath(), it.range(), it.isObfuscated()
                    )
                }.toList()
        return ['jars': jars]
    }

    @Override
    protected ConfigurableFileCollection getMetadataFiles() {
        return getProject().files({
            project.files(writeMetadata(new File(project.buildDir, "$name/metadata.json")))
        })
    }

    private File writeMetadata(File file) {
        final path = file.toPath()
        if (!Files.exists(path)) {
            Files.createDirectories(path.parent)
        }
        Files.writeString(path, JsonOutput.prettyPrint(new JsonBuilder(getMetadata()).toString()))
        return file
    }
}
