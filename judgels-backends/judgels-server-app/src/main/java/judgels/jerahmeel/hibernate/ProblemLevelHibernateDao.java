package judgels.jerahmeel.hibernate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.Tuple;
import judgels.jerahmeel.persistence.ProblemLevelDao;
import judgels.jerahmeel.persistence.ProblemLevelModel;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import org.hibernate.query.Query;

public class ProblemLevelHibernateDao extends HibernateDao<ProblemLevelModel>
        implements ProblemLevelDao {

    @Inject
    public ProblemLevelHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Map<String, Integer> selectAllAverageByProblemJids(Collection<String> problemJids) {
        if (problemJids.isEmpty()) {
            return Collections.emptyMap();
        }

        String q = ""
                + "SELECT problemJid, AVG(level) "
                + "FROM jerahmeel_problem_level "
                + "WHERE problemJid IN :problemJids "
                + "GROUP BY problemJid";

        Query<Tuple> query = currentSession().createQuery(q, Tuple.class);
        query.setParameterList("problemJids", problemJids);

        List<Tuple> data = query.getResultList();

        Map<String, Integer> res = new HashMap<>();
        for (Tuple t : data) {
            String problemJid = t.get(0, String.class);
            int level = (int) (double) t.get(1, Double.class);
            level = (level + 99) / 100 * 100;
            res.put(problemJid, level);
        }
        return Map.copyOf(res);
    }
}
