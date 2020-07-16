/*
 * Copyright 2020 the original author or authors.
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
package org.springframework.cloud.bindings;

import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A representation of a binding as defined by the
 * <a href="https://github.com/k8s-service-bindings/spec#application-projection">Kubernetes Service Binding Specification</a>.
 */
public final class Binding {

    /**
     * The key for the kind of a binding.
     */
    public static final String KIND = "kind";

    /**
     * The key for the provider of a binding.
     */
    public static final String PROVIDER = "provider";

    /**
     * The key for the type of a binding.
     */
    public static final String TYPE = "type";

    private final String name;

    private final Path path;

    private final String provider;

    private final Map<String, String> secret;

    private final String type;

    /**
     * Creates a new {@code Binding} instance using the specified file system root.
     */
    public Binding(Path path) {
        this(path.getFileName().toString(), path, createSecretMap(path));
    }

    /**
     * Creates a new {@code Binding} instance using the specified content.
     *
     * @param name   the name of the {@code Binding}.
     * @param path   the path to the {@code Binding}.
     * @param secret the secret of the {@code Binding}.
     */
    public Binding(String name, Path path, Map<String, String> secret) {
        this.name = name;
        this.path = path;
        this.secret = new HashMap<>();

        String type = null;
        String provider = null;
        for (Map.Entry<String, String> entry : secret.entrySet()) {
            switch (entry.getKey()) {
                case TYPE:
                case KIND: // TODO: Remove as CNB_BINDINGS ages out
                    type = entry.getValue();
                    break;
                case PROVIDER:
                    provider = entry.getValue();
                    break;
                default:
                    this.secret.put(entry.getKey(), entry.getValue());
            }
        }

        if (type == null) {
            throw new IllegalArgumentException(String.format("%s has no type and is not a valid binding", path));
        }

        this.type = type;
        this.provider = provider;
    }

    private static Map<String, String> createSecretMap(Path path) {
        Map<String, String> secret = createFilePerEntryMap(path);

        // TODO: Remove as CNB_BINDINGS ages out
        Arrays.asList("metadata", "secret")
                .forEach(d -> secret.putAll(createFilePerEntryMap(path.resolve(d))));

        return secret;
    }

    private static Map<String, String> createFilePerEntryMap(Path path) {
        if (!Files.exists(path)) {
            return Collections.emptyMap();
        }

        try {
            return Files.list(path)
                    .filter(p -> {
                        try {
                            return !Files.isHidden(p);
                        } catch (IOException e) {
                            throw new IllegalStateException(String.format("unable to determin id file '%s' is hidden", p), e);
                        }
                    })
                    .filter(p -> !Files.isDirectory(p))
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

    /**
     * Returns the name of the binding.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path of the binding.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Returns the secret of the binding.
     */
    public Map<String, String> getSecret() {
        return Collections.unmodifiableMap(secret);
    }

    /**
     * Returns the type of the binding.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the provider of the binding.
     */
    @Nullable
    public String getProvider() {
        return provider;
    }

    /**
     * Returns the {@link Path} to a secret file on disk.
     *
     * @param name the name of the secret key.
     */
    public Path getSecretFilePath(String name) {
        for (String d : Arrays.asList("metadata", "secret")) {
            Path file = path.resolve(d).resolve(name);
            if (Files.exists(file)) {
                return file;
            }
        }

        return this.path.resolve(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Binding binding = (Binding) o;
        return name.equals(binding.name) &&
                path.equals(binding.path) &&
                Objects.equals(provider, binding.provider) &&
                secret.equals(binding.secret) &&
                Objects.equals(type, binding.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, provider, secret, type);
    }

    @Override
    public String toString() {
        return "Binding{" +
                "name='" + name + '\'' +
                ", path=" + path +
                ", provider='" + provider + '\'' +
                ", secret=" + new TreeSet<>(secret.keySet()) +
                ", type='" + type + '\'' +
                '}';
    }

}
