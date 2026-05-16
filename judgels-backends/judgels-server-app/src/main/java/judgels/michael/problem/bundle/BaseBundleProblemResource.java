package judgels.michael.problem.bundle;

import jakarta.inject.Inject;
import judgels.michael.problem.BaseProblemResource;
import judgels.problem.bundle.BundleProblemStore;
import judgels.problem.bundle.item.BundleItemStore;

public abstract class BaseBundleProblemResource extends BaseProblemResource {
    @Inject protected BundleProblemStore bundleProblemStore;
    @Inject protected BundleItemStore itemStore;
}
