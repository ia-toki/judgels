package org.iatoki.judgels.sandalphon.problem.base.tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

@Singleton
public class ProblemTagHibernateDao extends UnmodifiableHibernateDao<ProblemTagModel> implements ProblemTagDao {
    @Inject
    public ProblemTagHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ProblemTagModel> selectAllByProblemJid(String problemJid) {
        return selectAll(new FilterOptions.Builder<ProblemTagModel>()
                .putColumnsEq(ProblemTagModel_.problemJid, problemJid)
                .build(), new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderDir(OrderDir.ASC)
                .build());
    }

    @Override
    public List<ProblemTagModel> selectAllByTags(Set<String> tags) {
        return selectAll(new FilterOptions.Builder<ProblemTagModel>()
                .putColumnsIn(ProblemTagModel_.tag, tags)
                .build());
    }

    @Override
    public Optional<ProblemTagModel> selectByProblemJidAndTag(String problemJid, String tag) {
        return selectByFilter(new FilterOptions.Builder<ProblemTagModel>()
                .putColumnsEq(ProblemTagModel_.problemJid, problemJid)
                .putColumnsEq(ProblemTagModel_.tag, tag)
                .build());
    }
}
