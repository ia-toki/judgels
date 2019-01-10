package org.iatoki.judgels.sandalphon.jid;

import org.iatoki.judgels.play.jid.AbstractJidCacheHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class JidCacheHibernateDao extends AbstractJidCacheHibernateDao<JidCacheModel> implements JidCacheDao {

    public JidCacheHibernateDao() {
        super(JidCacheModel.class);
    }

    @Override
    public JidCacheModel createJidCacheModel() {
        return new JidCacheModel();
    }
}
