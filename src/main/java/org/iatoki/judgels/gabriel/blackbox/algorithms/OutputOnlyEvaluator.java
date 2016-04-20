package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FilenameUtils;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.LocalFileSystemProvider;
import org.iatoki.judgels.gabriel.blackbox.EvaluationException;
import org.iatoki.judgels.gabriel.blackbox.EvaluationResult;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;

import java.io.File;
import java.io.IOException;

public final class OutputOnlyEvaluator implements Evaluator {

    private final File evaluationDir;

    public OutputOnlyEvaluator(File evaluationDir, File sourceFile) {
        this.evaluationDir = evaluationDir;

        FileSystemProvider fileSystemProvider = new LocalFileSystemProvider(evaluationDir);
        try {
            fileSystemProvider.uploadZippedFiles(ImmutableList.of(), sourceFile, false);
        } catch (IOException e) {

        }
    }

    @Override
    public EvaluationResult evaluate(File testCaseInputFile) throws EvaluationException {
        return EvaluationResult.plainResult(FilenameUtils.getBaseName(testCaseInputFile.getName()) + ".out");
    }

    @Override
    public String getEvaluationResultFilename(File testcaseInputFile) {
        return FilenameUtils.getBaseName(testcaseInputFile.getName()) + ".out";
    }
}
