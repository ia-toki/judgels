package org.iatoki.judgels.sandalphon.problem.base.tag;

import com.google.inject.ImplementedBy;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.UnmodifiableDao;

@ImplementedBy(ProblemTagHibernateDao.class)
public interface ProblemTagDao extends UnmodifiableDao<ProblemTagModel> {
    List<ProblemTagModel> selectAllByProblemJid(String problemJid);
    List<ProblemTagModel> selectAllByTags(Set<String> tags);
    Optional<ProblemTagModel> selectByProblemJidAndTag(String problemJid, String tag);
}
