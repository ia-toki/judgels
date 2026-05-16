package judgels.michael.problem.programming;

import jakarta.inject.Inject;
import judgels.michael.problem.BaseProblemResource;
import judgels.problem.programming.ProgrammingProblemStore;

public abstract class BaseProgrammingProblemResource extends BaseProblemResource {
    @Inject protected ProgrammingProblemStore programmingProblemStore;
}
