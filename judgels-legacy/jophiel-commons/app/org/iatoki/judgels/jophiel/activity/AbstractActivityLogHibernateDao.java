package org.iatoki.judgels.jophiel.activity;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public abstract class AbstractActivityLogHibernateDao<M extends AbstractActivityLogModel> extends HibernateDao<M> implements BaseActivityLogDao<M> {
    public AbstractActivityLogHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
