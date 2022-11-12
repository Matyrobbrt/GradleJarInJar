/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.transform


import com.matyrobbrt.gradle.jarinjar.util.HashFunction
import com.matyrobbrt.gradle.jarinjar.data.JiJDependency
import groovy.transform.CompileStatic
import groovy.transform.MapConstructor

import java.util.jar.JarFile
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.ZipEntry

@CompileStatic
@MapConstructor
class ForgeManifestFixerTransformer implements ArtifactTransformer {
    String modType
    String modulePrefix

    @Override
    byte[] transform(JiJDependency dependency, byte[] input) {
        final bytes = new ByteArrayOutputStream()
        try (final depOs = new JarOutputStream(bytes)
            final inputOs = new JarInputStream(new ByteArrayInputStream(input))) {

            final manifest = new Manifest(inputOs.manifest)
            manifest.mainAttributes.putValue('FMLModType', modType)
            if (!manifest.mainAttributes.getValue('Automatic-Module-Name')) {
                manifest.mainAttributes.putValue('Automatic-Module-Name', (modulePrefix + '.' + dependency.group() + '.' + dependency.artifact()).replace('-', '.'))
            }
            depOs.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME))
            manifest.write(depOs)
            depOs.closeEntry()

            ZipEntry entry
            while ((entry = inputOs.nextEntry) !== null) {
                if (entry.name.endsWith('module-info.class')) continue
                if (entry.name == 'META-INF/mods.toml') { // Skip mods
                    return input
                }
                depOs.putNextEntry(makeNewEntry(entry))
                depOs.write(inputOs.readAllBytes())
                depOs.closeEntry()
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
        return HashFunction.SHA1.hash('forgemanifestfix:' + modType + ';' + modulePrefix)
    }
}
