package org.iatoki.judgels.sandalphon.jid;

import judgels.persistence.hibernate.HibernateDaoData;
import org.iatoki.judgels.play.jid.AbstractJidCacheHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class JidCacheHibernateDao extends AbstractJidCacheHibernateDao<JidCacheModel> implements JidCacheDao {

    @Inject
    public JidCacheHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public JidCacheModel createJidCacheModel() {
        return new JidCacheModel();
    }
}
