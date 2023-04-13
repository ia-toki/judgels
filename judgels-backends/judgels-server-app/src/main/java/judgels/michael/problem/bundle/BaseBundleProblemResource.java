package judgels.michael.problem.bundle;

import javax.inject.Inject;
import judgels.michael.problem.BaseProblemResource;
import judgels.sandalphon.problem.bundle.BundleProblemStore;
import judgels.sandalphon.problem.bundle.item.BundleItemStore;

public abstract class BaseBundleProblemResource extends BaseProblemResource {
    @Inject protected BundleProblemStore bundleProblemStore;
    @Inject protected BundleItemStore itemStore;
}
