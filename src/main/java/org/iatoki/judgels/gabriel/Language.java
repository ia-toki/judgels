package org.iatoki.judgels.gabriel;

import com.google.common.base.Joiner;

import java.util.List;
import java.util.Set;

public abstract class Language {

    public abstract String getName();

    public abstract List<String> getCompilationCommand(String sourceFilename);

    public abstract String getExecutableFilename(String sourceFilename);

    public abstract List<String> getExecutionCommand(String sourceFilename);

    public final String verifyFile(String filename, String content) {
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

    protected String verifyFileContent(String content) {
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
