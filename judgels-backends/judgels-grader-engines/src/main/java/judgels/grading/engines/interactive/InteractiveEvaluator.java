package judgels.grading.engines.interactive;

import java.io.File;
import judgels.grading.api.EvaluationException;
import judgels.grading.api.EvaluationResult;
import judgels.grading.api.Evaluator;
import judgels.grading.helpers.communicator.Communicator;

public class InteractiveEvaluator implements Evaluator {
    private Communicator communicator;

    public void prepare(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public EvaluationResult evaluate(File input, File output) throws EvaluationException {
        return communicator.communicate(input);
    }
}
