package org.iatoki.judgels.gabriel;

import java.util.Map;

public final class SubmissionSource {

    private final Map<String, SourceFile> submissionFiles;

    public SubmissionSource(Map<String, SourceFile> submissionFiles) {
        this.submissionFiles = submissionFiles;
    }

    public Map<String, SourceFile> getSubmissionFiles() {
        return submissionFiles;
    }
}
