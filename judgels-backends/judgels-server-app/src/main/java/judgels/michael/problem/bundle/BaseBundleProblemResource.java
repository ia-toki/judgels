package judgels.michael.problem.bundle;

import jakarta.inject.Inject;
import judgels.catalog.problem.bundle.BundleProblemStore;
import judgels.catalog.problem.bundle.item.BundleItemStore;
import judgels.michael.problem.BaseProblemResource;

public abstract class BaseBundleProblemResource extends BaseProblemResource {
    @Inject protected BundleProblemStore bundleProblemStore;
    @Inject protected BundleItemStore itemStore;
}
