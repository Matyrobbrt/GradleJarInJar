/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.transform;

import com.matyrobbrt.gradle.jarinjar.data.JiJDependency;
import com.matyrobbrt.gradle.jarinjar.util.HashFunction;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public interface TransformerManager {
    String getName(JiJDependency dependency) throws IOException;

    static TransformerManager forFile(File file) {
        return new ForFile(file.toPath());
    }

    final class ForFile implements TransformerManager {
        private final Path file;
        public ForFile(Path file) {
            this.file = file;
        }

        private HashMap<TransformerData, String> names;

        @Override
        public String getName(JiJDependency dependency) throws IOException {
            final HashSet<String> transformerHashes = dependency.transformers().stream()
                    .map(ArtifactTransformer::hash)
                    .collect(Collectors.toCollection(HashSet::new));

            if (!transformerHashes.isEmpty() && transformerHashes.contains(null)) {
                return appendExtension(UUID.randomUUID().toString(), dependency.file().getName()); // If a transformer can't be hashed don't allow the jar to be cached
            }

            final TransformerData data = new TransformerData(
                    transformerHashes,
                    dependency.group(), dependency.artifact(), dependency.version(),
                    HashFunction.MD5.hash(dependency.file())
            );

            if (names == null) read();

            final String oldName = names.get(data); if (oldName != null) return oldName;

            final String newName = appendExtension(UUID.randomUUID().toString(), dependency.file().getName());
            names.put(data, newName); save();

            return newName;
        }

        private String appendExtension(String stringIn, String fileName) {
            final int dotIndexOf = fileName.lastIndexOf('.');
            if (dotIndexOf == -1) return stringIn;
            return stringIn + "." + fileName.substring(dotIndexOf + 1);
        }

        private void save() throws IOException {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
            }
            try (final ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(names);
            }
        }

        @SuppressWarnings("unchecked")
        private void read() throws IOException {
            if (!Files.exists(file)) {
                names = new HashMap<>();
            } else {
                try (final ObjectInputStream is = new ObjectInputStream(Files.newInputStream(file))) {
                    names = (HashMap<TransformerData, String>) is.readObject();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private static final class TransformerData implements Serializable {
            private static final long serialVersionUID = 8067653141683777986L;
            private final HashSet<String> transformerHashes;
            private final String group;
            private final String artifact;
            private final String version;
            private final String fileHash;

            private TransformerData(HashSet<String> transformerHashes, String group, String artifact, String version, String fileHash) {
                this.transformerHashes = transformerHashes;
                this.group = group;
                this.artifact = artifact;
                this.version = version;
                this.fileHash = fileHash;
            }

            public HashSet<String> transformerHashes() {
                return transformerHashes;
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

            public String fileHash() {
                return fileHash;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) return true;
                if (obj == null || obj.getClass() != this.getClass()) return false;
                final TransformerData that = (TransformerData) obj;
                return Objects.equals(this.transformerHashes, that.transformerHashes) &&
                        Objects.equals(this.group, that.group) &&
                        Objects.equals(this.artifact, that.artifact) &&
                        Objects.equals(this.version, that.version) &&
                        Objects.equals(this.fileHash, that.fileHash);
            }

            @Override
            public int hashCode() {
                return Objects.hash(transformerHashes, group, artifact, version, fileHash);
            }

            @Override
            public String toString() {
                return "TransformerData[" +
                        "transformerHashes=" + transformerHashes + ", " +
                        "group=" + group + ", " +
                        "artifact=" + artifact + ", " +
                        "version=" + version + ", " +
                        "fileHash=" + fileHash + ']';
            }
        }
    }
}
