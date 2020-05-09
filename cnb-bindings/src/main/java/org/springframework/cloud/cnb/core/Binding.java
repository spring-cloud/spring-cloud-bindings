/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.cnb.core;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A representation of a binding as defined by the
 * <a href="https://github.com/buildpacks/spec/blob/master/extensions/bindings.md">Cloud Native Buildpacks Specification</a>.
 */
public final class Binding {

    private final String name;

    private final Path path;

    private final Map<String, String> metadata;

    private final Map<String, String> secret;

    /**
     * Creates a new {@code Binding} instance using the specified file system root.
     */
    public Binding(@NotNull Path path) {
        this.name = path.getFileName().toString();
        this.path = path;
        this.metadata = createFilePerEntryMap(path.resolve("metadata"));
        this.secret = createFilePerEntryMap(path.resolve("secret"));
    }

    /**
     * Creates a new {@code Binding} instance using the specified content.
     *
     * @param name     the name of the {@code Binding}.
     * @param path     the path to the {@code Binding}.
     * @param metadata the metadata of the {@code Binding}.
     * @param secret   the secret of the {@code Binding}.
     */
    public Binding(@NotNull String name, @NotNull Path path, @NotNull Map<String, String> metadata,
                   @NotNull Map<String, String> secret) {

        this.name = name;
        this.path = path;
        this.metadata = metadata;
        this.secret = secret;
    }

    /**
     * Returns the name of the binding.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Returns the path of the binding.
     */
    public @NotNull Path getPath() {
        return path;
    }

    /**
     * Returns the metadata of the binding.
     */
    public @NotNull Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Returns the secret of the binding.
     */
    public @NotNull Map<String, String> getSecret() {
        return secret;
    }

    /**
     * Returns the kind of the binding.  Equivalent to {@code getMetadata().get("kind")}.
     */
    public @NotNull String getKind() {
        return metadata.get("kind");
    }

    /**
     * Returns the provider of the binding.  Equivalent to {@code getMetadata().get("provider")}.
     */
    public @NotNull String getProvider() {
        return metadata.get("provider");
    }

    /**
     * Returns the {@link Path} to a metadata file on disk.
     *
     * @param name the name of the metadata key.
     */
    public @NotNull Path getMetadataFilePath(@NotNull String name) {
        return this.path.resolve("metadata").resolve(name);
    }

    /**
     * Returns the {@link Path} to a secret file on disk.
     *
     * @param name the name of the secret key.
     */
    public @NotNull Path getSecretFilePath(@NotNull String name) {
        return this.path.resolve("secret").resolve(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Binding binding = (Binding) o;
        return name.equals(binding.name) &&
                path.equals(binding.path) &&
                metadata.equals(binding.metadata) &&
                secret.equals(binding.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, metadata, secret);
    }

    private @NotNull Map<String, String> createFilePerEntryMap(@NotNull Path path) {
        try {
            return Files.list(path)
                    .collect(Collectors.toMap(
                            p -> p.getFileName().toString(),
                            p -> {
                                try {
                                    return new String(Files.readAllBytes(p), StandardCharsets.UTF_8).trim();
                                } catch (IOException e) {
                                    throw new IllegalStateException(String.format("unable to read file '%s'", p), e);
                                }
                            }
                    ));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("unable to list children of '%s'", path), e);
        }
    }

}
