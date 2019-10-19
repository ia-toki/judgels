package org.iatoki.judgels.jerahmeel.chapter;

import com.google.common.collect.ImmutableList;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.jerahmeel.persistence.ChapterModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class ChapterHibernateDao extends JudgelsHibernateDao<ChapterModel> implements ChapterDao {

    @Inject
    public ChapterHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    protected List<SingularAttribute<ChapterModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(ChapterModel_.name, ChapterModel_.description);
    }
}
