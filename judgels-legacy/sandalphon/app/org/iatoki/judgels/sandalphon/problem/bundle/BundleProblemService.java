package org.iatoki.judgels.sandalphon.problem.bundle;

import com.google.inject.ImplementedBy;

import java.io.IOException;

@ImplementedBy(BundleProblemServiceImpl.class)
public interface BundleProblemService {

    void initBundleProblem(String problemJid) throws IOException;
}
