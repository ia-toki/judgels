package org.iatoki.judgels.jophiel.activity;

import judgels.persistence.Dao;

public interface BaseActivityLogDao<M extends AbstractActivityLogModel> extends Dao<M> {

    M createActivityLogModel();
}
