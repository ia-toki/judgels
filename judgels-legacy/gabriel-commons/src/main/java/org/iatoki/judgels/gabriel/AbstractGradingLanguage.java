package org.iatoki.judgels.gabriel;

import com.google.common.base.Joiner;

import java.util.Set;

public abstract class AbstractGradingLanguage implements GradingLanguage {
    public final String verifyFile(String filename, byte[] content) {
        String byExtension = verifyFileExtension(filename);
        String byContent = verifyFileContent(content);

        if (byExtension != null) {
            return byExtension;
        } else if (byContent != null) {
            return byContent;
        } else {
            return null;
        }
    }

    protected abstract Set<String> getAllowedExtensions();

    protected String verifyFileContent(byte[] content) {
        return null;
    }

    private String verifyFileExtension(String filename) {
        int dotPos = filename.lastIndexOf('.');
        if (dotPos == -1) {
            return "Filename must have one of this extensions: " + Joiner.on(", ").join(getAllowedExtensions());
        }

        String extension = filename.substring(dotPos + 1);
        if (getAllowedExtensions().contains(extension)) {
            return null;
        } else {
            return "Filename must have one of this extensions: " + Joiner.on(", ").join(getAllowedExtensions());
        }
    }
}
