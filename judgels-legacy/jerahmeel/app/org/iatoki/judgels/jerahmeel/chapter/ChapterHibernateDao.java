package org.iatoki.judgels.jerahmeel.chapter;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;

import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class ChapterHibernateDao extends AbstractJudgelsHibernateDao<ChapterModel> implements ChapterDao {

    public ChapterHibernateDao() {
        super(ChapterModel.class);
    }

    @Override
    protected List<SingularAttribute<ChapterModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(ChapterModel_.name, ChapterModel_.description);
    }
}
