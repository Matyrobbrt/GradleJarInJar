/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.transform

import com.matyrobbrt.gradle.jarinjar.data.JiJDependency
import com.matyrobbrt.gradle.jarinjar.util.TreeElementWithoutFile
import groovy.transform.CompileStatic
import groovy.transform.MapConstructor
import org.apache.commons.io.IOUtils
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet

import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

@CompileStatic
class FilterContentsTransformer implements ArtifactTransformer, PatternFilterable {
    @Delegate(includeTypes = PatternFilterable)
    final PatternSet pattern

    FilterContentsTransformer(final PatternSet pattern) {
        this.pattern = pattern
    }

    FilterContentsTransformer(@DelegatesTo(PatternFilterable) Closure configure) {
        this.pattern = new PatternSet()
        configure.setResolveStrategy(Closure.DELEGATE_FIRST)
        configure.setDelegate(pattern)
        configure.call(pattern)
    }

    @Override
    byte[] transform(JiJDependency dependency, byte[] input) {
        final spec = pattern.asSpec
        final bytes = new ByteArrayOutputStream()
        try (final inputOs = new JarInputStream(new ByteArrayInputStream(input))
             final depOs = new JarOutputStream(bytes, inputOs.manifest)) {

            ZipEntry entry
            while ((entry = inputOs.nextEntry) !== null) {
                if (spec.isSatisfiedBy(new TreeElementWithoutFile(
                        entry.isDirectory(), entry.name, entry.directory ? null : inputOs
                ))) {
                    depOs.putNextEntry(makeNewEntry(entry))
                    if (!entry.directory) {
                        IOUtils.copy(inputOs, depOs)
                    }
                    depOs.closeEntry()
                }
            }
        }
        return bytes.toByteArray()
    }

    static ZipEntry makeNewEntry(ZipEntry oldEntry) {
        ZipEntry newEntry = new ZipEntry(oldEntry.name)
        if (oldEntry.getComment() !== null) newEntry.setComment(oldEntry.getComment())
        return newEntry
    }

    @Override
    String hash() {
        return null
    }

    @Override
    Object addToAntBuilder(Object node, String childNodeName) {
        return pattern.addToAntBuilder(node, childNodeName)
    }
}
