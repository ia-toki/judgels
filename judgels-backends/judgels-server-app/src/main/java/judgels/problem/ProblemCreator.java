package judgels.problem;

import jakarta.inject.Inject;
import judgels.api.problem.Problem;
import judgels.api.problem.ProblemType;
import judgels.problem.base.ProblemStore;
import judgels.problem.base.statement.ProblemStatementStore;
import judgels.problem.base.tag.ProblemTagStore;
import judgels.problem.bundle.BundleProblemStore;
import judgels.problem.programming.ProgrammingProblemStore;

public class ProblemCreator {
    private final ProblemStore problemStore;
    private final ProblemStatementStore statementStore;
    private final BundleProblemStore bundleProblemStore;
    private final ProgrammingProblemStore programmingProblemStore;
    private final ProblemTagStore tagStore;

    @Inject
    public ProblemCreator(
            ProblemStore problemStore,
            ProblemStatementStore statementStore,
            BundleProblemStore bundleProblemStore,
            ProgrammingProblemStore programmingProblemStore,
            ProblemTagStore tagStore) {

        this.problemStore = problemStore;
        this.statementStore = statementStore;
        this.bundleProblemStore = bundleProblemStore;
        this.programmingProblemStore = programmingProblemStore;
        this.tagStore = tagStore;
    }

    public Problem createProblem(String actorJid, String slug, String gradingEngine, String additionalNote, String initialLanguage) {
        ProblemType type = gradingEngine.equals("Bundle") ? ProblemType.BUNDLE : ProblemType.PROGRAMMING;
        Problem problem = problemStore.createProblem(type, slug, additionalNote);

        statementStore.initStatements(problem.getJid(), type, initialLanguage);

        if (type == ProblemType.BUNDLE) {
            bundleProblemStore.initBundleProblem(problem.getJid());
        } else {
            programmingProblemStore.initProgrammingProblem(problem.getJid(), gradingEngine);
            tagStore.refreshDerivedTags(problem.getJid());
        }

        problemStore.initRepository(actorJid, problem.getJid());

        return problem;
    }
}
