package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import org.iatoki.judgels.play.EntityNotFoundException;

public final class BundleSubmissionNotFoundException extends EntityNotFoundException {
    @Override
    public String getEntityName() {
        return "Submission";
    }
}
