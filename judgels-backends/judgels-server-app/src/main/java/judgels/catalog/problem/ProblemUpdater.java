package judgels.catalog.problem;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.api.catalog.problem.ProblemSetterRole;
import judgels.catalog.problem.tag.ProblemTagStore;

public class ProblemUpdater {
    private final ProblemStore problemStore;
    private final ProblemTagStore tagStore;

    @Inject
    public ProblemUpdater(ProblemStore problemStore, ProblemTagStore tagStore) {
        this.problemStore = problemStore;
        this.tagStore = tagStore;
    }

    public void updateProblem(
            String problemJid,
            String slug,
            String additionalNote,
            Map<ProblemSetterRole, List<String>> setterJidsMap,
            Set<String> topicTags) {

        problemStore.updateProblem(problemJid, slug, additionalNote);

        Map<ProblemSetterRole, List<String>> curSetterJidsMap = problemStore.getProblemSetters(problemJid);
        for (ProblemSetterRole role : ProblemSetterRole.values()) {
            List<String> setterJids = setterJidsMap.getOrDefault(role, List.of());
            if (!setterJids.equals(curSetterJidsMap.getOrDefault(role, List.of()))) {
                problemStore.updateProblemSetters(problemJid, role, setterJids);
            }
        }

        tagStore.updateTopicTags(problemJid, topicTags);
    }
}
