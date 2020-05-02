package org.iatoki.judgels.sandalphon;

import judgels.sandalphon.SandalphonConfiguration;
import org.iatoki.judgels.jophiel.JophielClientControllerUtils;
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
    public SandalphonSingletonsBuilder(JidCacheDao jidCacheDao, SandalphonConfiguration config) {
        JidCacheServiceImpl.buildInstance(jidCacheDao);

        JophielClientControllerUtils.buildInstance(config.getRaphaelBaseUrl(), config.getJophielConfig().getBaseUrl());
        SandalphonControllerUtils.buildInstance();
    }
}
