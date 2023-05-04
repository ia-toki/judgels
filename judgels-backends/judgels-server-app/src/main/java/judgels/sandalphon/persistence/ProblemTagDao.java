package judgels.sandalphon.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.UnmodifiableDao;

public interface ProblemTagDao extends UnmodifiableDao<ProblemTagModel> {
    List<ProblemTagModel> selectAllByProblemJid(String problemJid);
    Map<String, Integer> selectTagCounts();
    Map<String, Integer> selectPublicTagCounts();
    List<ProblemTagModel> selectAllByTags(Set<String> tags);
    Optional<ProblemTagModel> selectByProblemJidAndTag(String problemJid, String tag);
}
