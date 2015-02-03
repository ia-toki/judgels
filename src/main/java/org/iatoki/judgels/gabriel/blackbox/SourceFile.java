package org.iatoki.judgels.gabriel.blackbox;

public final class SourceFile {
    private final String name;
    private final String content;

    public SourceFile(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
