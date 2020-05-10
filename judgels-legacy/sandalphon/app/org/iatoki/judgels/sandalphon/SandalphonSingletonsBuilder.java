package org.iatoki.judgels.sandalphon;

import org.iatoki.judgels.sandalphon.jid.JidCacheDao;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @deprecated Temporary class. Will be restructured when new module system has been finalized.
 */
@Singleton
@Deprecated
public final class SandalphonSingletonsBuilder {

    @Inject
    public SandalphonSingletonsBuilder(JidCacheDao jidCacheDao) {
        JidCacheServiceImpl.buildInstance(jidCacheDao);

        SandalphonControllerUtils.buildInstance();
    }
}
