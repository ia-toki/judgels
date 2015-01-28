package org.iatoki.judgels.gabriel.blackbox;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class SourceFile {
    private final String name;
    private final byte[] content;

    private SourceFile(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public static SourceFile fromFile(File file) throws IOException {
        String name = file.getName();
        byte[] content = FileUtils.readFileToByteArray(file);
        return new SourceFile(name, content);
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}
