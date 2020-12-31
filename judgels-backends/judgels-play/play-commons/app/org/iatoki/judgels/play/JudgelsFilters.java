package org.iatoki.judgels.play;

import play.filters.csrf.CSRFFilter;
import play.filters.gzip.GzipFilter;
import play.http.DefaultHttpFilters;

import javax.inject.Inject;

public final class JudgelsFilters extends DefaultHttpFilters {
    @Inject
    public JudgelsFilters(CSRFFilter csrfFilter, GzipFilter gzipFilter) {
        super(csrfFilter, gzipFilter);
    }
}
