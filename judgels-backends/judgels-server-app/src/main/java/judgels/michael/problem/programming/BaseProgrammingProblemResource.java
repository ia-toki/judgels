package judgels.michael.problem.programming;

import jakarta.inject.Inject;
import judgels.catalog.problem.programming.ProgrammingProblemStore;
import judgels.michael.problem.BaseProblemResource;

public abstract class BaseProgrammingProblemResource extends BaseProblemResource {
    @Inject protected ProgrammingProblemStore programmingProblemStore;
}
