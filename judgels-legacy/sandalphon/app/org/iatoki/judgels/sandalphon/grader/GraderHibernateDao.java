package org.iatoki.judgels.sandalphon.grader;

import com.google.common.collect.ImmutableList;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class GraderHibernateDao extends JudgelsHibernateDao<GraderModel> implements GraderDao {

    @Inject
    public GraderHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    protected List<SingularAttribute<GraderModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(GraderModel_.name);
    }
}
