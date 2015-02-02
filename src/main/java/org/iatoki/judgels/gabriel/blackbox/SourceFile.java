package org.iatoki.judgels.gabriel.blackbox;

public final class SourceFile {
    private final String name;
    private final byte[] content;

    public SourceFile(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}
