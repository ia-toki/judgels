package judgels.sandalphon.hibernate;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.persistence.LessonModel;
import judgels.sandalphon.persistence.LessonModel_;
import judgels.sandalphon.persistence.LessonPartnerDao;
import judgels.sandalphon.persistence.LessonPartnerModel;
import judgels.sandalphon.persistence.LessonPartnerModel_;

public final class LessonPartnerHibernateDao extends HibernateDao<LessonPartnerModel> implements LessonPartnerDao {

    @Inject
    public LessonPartnerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<LessonPartnerModel> selectByLessonJidAndUserJid(String lessonJid, String userJid) {
        return selectByFilter(new FilterOptions.Builder<LessonPartnerModel>()
                .putColumnsEq(LessonPartnerModel_.lessonJid, lessonJid)
                .putColumnsEq(LessonPartnerModel_.userJid, userJid)
                .build());
    }

    @Override
    public List<LessonPartnerModel> selectAllByLessonJid(String lessonJid) {
        return selectAll(new FilterOptions.Builder<LessonPartnerModel>()
                .putColumnsEq(LessonPartnerModel_.lessonJid, lessonJid)
                .build());
    }

    static CustomPredicateFilter<LessonModel> hasPartner(String userJid) {
        return (cb, cq, root) -> {
            Subquery<LessonPartnerModel> sq = cq.subquery(LessonPartnerModel.class);
            Root<LessonPartnerModel> subRoot = sq.from(LessonPartnerModel.class);

            sq.where(
                    cb.equal(subRoot.get(LessonPartnerModel_.lessonJid), root.get(LessonModel_.jid)),
                    cb.equal(subRoot.get(LessonPartnerModel_.userJid), userJid));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }
}
