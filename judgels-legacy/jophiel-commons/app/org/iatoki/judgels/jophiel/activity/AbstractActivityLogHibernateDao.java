package org.iatoki.judgels.jophiel.activity;

import org.iatoki.judgels.play.model.AbstractHibernateDao;

public abstract class AbstractActivityLogHibernateDao<M extends AbstractActivityLogModel> extends AbstractHibernateDao<Long, M> implements BaseActivityLogDao<M> {

    protected AbstractActivityLogHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }
}
