package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import java.io.IOException;

public interface BundleProblemGrader {

    BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer answer) throws IOException;
}
