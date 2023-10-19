package judgels.gabriel.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.gabriel.api.EvaluationException;
import judgels.gabriel.api.EvaluationResult;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.ScoringResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class OutputOnlyEvaluator implements Evaluator {
    private static final String EVALUATION_OUTPUT_SUFFIX = "_evaluation.out";

    private Scorer scorer;

    private FileSystem fs;
    private File evaluationDir;

    public void prepare(Scorer scorer, File evaluationDir, File sourceFile) throws PreparationException {
        this.scorer = scorer;
        this.fs = new LocalFileSystem(evaluationDir.toPath());
        this.evaluationDir = evaluationDir;

        try {
            fs.uploadZippedFiles(Paths.get(""), new FileInputStream(sourceFile));
        } catch (RuntimeException | FileNotFoundException e) {
            throw new PreparationException(e);
        }

        for (File file : FileUtils.listFiles(evaluationDir, null, false)) {
            if (!FilenameUtils.getExtension(file.getName()).equals("out")) {
                continue;
            }
            String evalFilename = FilenameUtils.removeExtension(file.getAbsolutePath()) + EVALUATION_OUTPUT_SUFFIX;
            try {
                FileUtils.moveFile(file, new File(evalFilename));
            } catch (IOException e) {
                throw new PreparationException(e);
            }
        }
    }

    @Override
    public EvaluationResult evaluate(File input, File output) throws EvaluationException {
        if (!getEvaluationOutput(input).exists()) {
            return EvaluationResult.skippedResult();
        }

        ScoringResult result = score(input, output);
        return new EvaluationResult.Builder()
                .verdict(result.getVerdict())
                .build();
    }

    @Override
    public ScoringResult score(File input, File output) throws ScoringException {
        return scorer.score(input, output, getEvaluationOutput(input));
    }

    private File getEvaluationOutput(File input) {
        String evaluationOutputFilename = FilenameUtils.getBaseName(input.getName()) + EVALUATION_OUTPUT_SUFFIX;
        return new File(evaluationDir, evaluationOutputFilename);
    }
}
