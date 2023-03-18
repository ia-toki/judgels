package judgels.michael.problem.programming;

import javax.inject.Inject;
import judgels.michael.problem.base.BaseProblemResource;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;

public abstract class BaseProgrammingProblemResource extends BaseProblemResource {
    @Inject protected ProgrammingProblemStore programmingProblemStore;
}
