package org.iatoki.judgels.jerahmeel.archive;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class ArchiveHibernateDao extends AbstractJudgelsHibernateDao<ArchiveModel> implements ArchiveDao {

    public ArchiveHibernateDao() {
        super(ArchiveModel.class);
    }
}
