package org.iatoki.judgels.jerahmeel.course;

import com.google.common.collect.ImmutableList;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class CourseHibernateDao extends JudgelsHibernateDao<CourseModel> implements CourseDao {

    @Inject
    public CourseHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    protected List<SingularAttribute<CourseModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(CourseModel_.name, CourseModel_.description);
    }
}
