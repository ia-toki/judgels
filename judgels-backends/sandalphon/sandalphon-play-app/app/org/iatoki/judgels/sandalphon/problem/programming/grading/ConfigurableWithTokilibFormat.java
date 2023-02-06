package org.iatoki.judgels.sandalphon.problem.programming.grading;

import java.util.List;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;

public interface ConfigurableWithTokilibFormat {

    GradingConfig updateConfigWithTokilibFormat(GradingConfig config, List<FileInfo> testDataFiles);
}
