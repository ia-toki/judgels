package org.iatoki.judgels.sandalphon.jid;

import org.iatoki.judgels.play.jid.AbstractBaseJidCacheServiceImpl;

public final class JidCacheServiceImpl extends AbstractBaseJidCacheServiceImpl<JidCacheModel> implements JidCacheService {

    private static JidCacheServiceImpl INSTANCE;

    private JidCacheServiceImpl(JidCacheDao jidCacheDao) {
        super(jidCacheDao);
    }

    public static synchronized void buildInstance(JidCacheDao jidCacheDao) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("JidCacheService instance has already been built");
        }
        INSTANCE = new JidCacheServiceImpl(jidCacheDao);
    }

    public static JidCacheServiceImpl getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("JidCacheService instance has not been built");
        }
        return INSTANCE;
    }
}
