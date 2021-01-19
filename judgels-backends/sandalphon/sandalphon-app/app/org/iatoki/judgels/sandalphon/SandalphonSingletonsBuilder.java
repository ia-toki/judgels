package org.iatoki.judgels.sandalphon;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @deprecated Temporary class. Will be restructured when new module system has been finalized.
 */
@Singleton
@Deprecated
public final class SandalphonSingletonsBuilder {

    @Inject
    public SandalphonSingletonsBuilder() {
        SandalphonControllerUtils.buildInstance();
    }
}
