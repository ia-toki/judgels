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
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.persistence.ContestSupervisorModel_;

public class ContestSupervisorHibernateDao extends HibernateDao<ContestSupervisorModel> implements ContestSupervisorDao {
    @Inject
    public ContestSupervisorHibernateDao(HibernateDaoData data) {
        super(data);
    }


    @Override
    public QueryBuilder<ContestSupervisorModel> selectByContestJid(String contestJid) {
        return new HibernateQueryBuilder<>(currentSession(), ContestSupervisorModel.class)
                .where(columnEq(ContestSupervisorModel_.contestJid, contestJid));
    }

    @Override
    public Optional<ContestSupervisorModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByContestJid(contestJid)
                .where(columnEq(ContestSupervisorModel_.userJid, userJid))
                .unique();
    }

    @Override
    public void dump(PrintWriter output, String contestJid) {
        List<ContestSupervisorModel> results = selectByContestJid(contestJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_supervisor (contestJid, userJid, permission, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestSupervisorModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s)",
                    escape(m.contestJid),
                    escape(m.userJid),
                    escape(m.permission),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }
}
