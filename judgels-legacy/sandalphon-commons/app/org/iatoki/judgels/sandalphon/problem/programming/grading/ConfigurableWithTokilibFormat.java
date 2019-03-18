package org.iatoki.judgels.sandalphon.problem.programming.grading;

import judgels.gabriel.api.GradingConfig;
import org.iatoki.judgels.FileInfo;

import java.util.List;

public interface ConfigurableWithTokilibFormat {

    GradingConfig updateConfigWithTokilibFormat(GradingConfig config, List<FileInfo> testDataFiles);
}
