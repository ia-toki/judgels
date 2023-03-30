package judgels.michael.problem.bundle;

import javax.inject.Inject;
import judgels.michael.problem.BaseProblemResource;
import judgels.sandalphon.problem.bundle.BundleProblemStore;

public abstract class BaseBundleProblemResource extends BaseProblemResource {
    @Inject protected BundleProblemStore bundleProblemStore;
}
