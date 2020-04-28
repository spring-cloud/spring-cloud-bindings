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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CnbBinding {

    private final Map<String, String> metadata;
    private final Map<String, String> secret;

    private String name;
    private File bindingDir;

    public CnbBinding(Map<String, String> metadata, Map<String, String> secret) {
        this.metadata = metadata;
        this.secret = secret;
    }

    public CnbBinding(File bindingDir) {
        this.bindingDir = bindingDir;
        this.metadata = subDirToMap("metadata");
        this.secret = subDirToMap("secret");
        this.name = bindingDir.getName();
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getSecret() {
        return this.secret;
    }

    public Map<String, String> getAllMetadata() {
        return this.metadata;
    }

    public String getKind() {
        return this.metadata.get("kind");
    }

    public String getProvider() {
        return this.metadata.get("provider");
    }

    public String[] getTags() {
        String tagsVal = this.metadata.get("tags");
        String[] tags = tagsVal.split(",");
        return tags;
    }

    private Map<String, String> subDirToMap(String subdir) {
        Path subDirPath = Paths.get(this.bindingDir.getPath(), subdir);
        File subDir = subDirPath.toFile().getAbsoluteFile();
        if (!subDir.isDirectory()) {
            throw new IllegalBindingException(subDir + "is not a directory");
        }
        Map<String, String> secret = new HashMap<String, String>();
        for (File file : subDir.listFiles()) {
            if (file.isDirectory()) {
                continue;
            }
            secret.put(file.getName(), readFile(file));
        }
        return secret;
    }

    private String readFile(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath())).trim();
        } catch (IOException e) {
            throw new IllegalBindingException(e);
        }
    }
}
