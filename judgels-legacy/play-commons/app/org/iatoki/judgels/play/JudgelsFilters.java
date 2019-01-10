package org.iatoki.judgels.play;

import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.http.HttpFilters;

import javax.inject.Inject;

public final class JudgelsFilters implements HttpFilters {

    private final GzipFilter gzipFilter;

    @Inject
    public JudgelsFilters(GzipFilter gzipFilter) {
        this.gzipFilter = gzipFilter;
    }

    @Override
    public EssentialFilter[] filters() {
        return new EssentialFilter[] {gzipFilter};
    }
}
