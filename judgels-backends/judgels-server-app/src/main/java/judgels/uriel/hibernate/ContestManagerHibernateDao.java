package judgels.uriel.hibernate;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestManagerModel_;

public class ContestManagerHibernateDao extends HibernateDao<ContestManagerModel> implements ContestManagerDao {
    @Inject
    public ContestManagerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public QueryBuilder<ContestManagerModel> selectByContestJid(String contestJid) {
        return new HibernateQueryBuilder<>(currentSession(), ContestManagerModel.class)
                .where(columnEq(ContestManagerModel_.contestJid, contestJid));
    }

    @Override
    public Optional<ContestManagerModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestManagerModel_.userJid, userJid))
                .unique();
    }

    @Override
    public void dump(PrintWriter output, String contestJid) {
        List<ContestManagerModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_manager (contestJid, userJid, createdBy, createdAt, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestManagerModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.userJid),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }
}
