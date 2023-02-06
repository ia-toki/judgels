package judgels.sandalphon.problem.base.tag;

import static judgels.sandalphon.resource.StatementLanguageStatus.DISABLED;
import static judgels.sandalphon.resource.StatementLanguageStatus.ENABLED;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.gabriel.api.GradingConfig;
import judgels.sandalphon.persistence.ProblemTagDao;
import judgels.sandalphon.persistence.ProblemTagModel;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;

public class ProblemTagStore {
    private final ProblemTagDao problemTagDao;
    private final ProblemStore problemStore;
    private final ProgrammingProblemStore programmingProblemStore;

    @Inject
    public ProblemTagStore(
            ProblemTagDao problemTagDao,
            ProblemStore problemStore,
            ProgrammingProblemStore programmingProblemStore) {

        this.problemTagDao = problemTagDao;
        this.problemStore = problemStore;
        this.programmingProblemStore = programmingProblemStore;
    }

    public Map<String, Integer> getTagCounts(boolean isAdmin) {
        if (!isAdmin) {
            return ImmutableMap.of();
        }
        return problemTagDao.selectTagCounts();
    }

    public Map<String, Integer> getPublicTagCounts() {
        return problemTagDao.selectPublicTagCounts();
    }

    public Set<String> findTopicTags(String problemJid) {
        return problemTagDao.selectAllByProblemJid(problemJid)
                .stream()
                .map(m -> m.tag)
                .filter(tag -> tag.startsWith("topic-"))
                .collect(Collectors.toSet());
    }

    public void updateTopicTags(String problemJid, Set<String> topicTags) {
        Set<String> curTags = findTopicTags(problemJid);

        for (String tag : Sets.difference(topicTags, curTags)) {
            ProblemTagModel m = new ProblemTagModel();
            m.problemJid = problemJid;
            m.tag = tag;
            problemTagDao.insert(m);
        }
        for (String tag : Sets.difference(curTags, topicTags)) {
            problemTagDao.selectByProblemJidAndTag(problemJid, tag).ifPresent(problemTagDao::delete);
        }
    }

    public void updateVisibilityTag(String problemJid, boolean isPublic) {
        Set<String> curTags = getTags(problemJid);
        Set<String> tagsToAdd = new HashSet<>();
        Set<String> tagsToRemove = new HashSet<>();

        removeTag(curTags, tagsToAdd, tagsToRemove, "visibility-private");
        removeTag(curTags, tagsToAdd, tagsToRemove, "visibility-public");

        if (isPublic) {
            upsertTag(curTags, tagsToAdd, tagsToRemove, "visibility-public");
        } else {
            upsertTag(curTags, tagsToAdd, tagsToRemove, "visibility-private");
        }

        applyTagUpdates(problemJid, tagsToAdd, tagsToRemove);
    }

    public void refreshDerivedTags(String problemJid) {
        Set<String> curTags = getTags(problemJid);
        Set<String> tagsToAdd = new HashSet<>();
        Set<String> tagsToRemove = new HashSet<>();

        removeTag(curTags, tagsToAdd, tagsToRemove, "visibility-private");
        removeTag(curTags, tagsToAdd, tagsToRemove, "visibility-public");

        if (curTags.contains("visibility-public")) {
            upsertTag(curTags, tagsToAdd, tagsToRemove, "visibility-public");
        } else {
            upsertTag(curTags, tagsToAdd, tagsToRemove, "visibility-private");
        }

        if (problemStore.getStatementAvailableLanguages(null, problemJid).getOrDefault("en-US", DISABLED) == ENABLED) {
            upsertTag(curTags, tagsToAdd, tagsToRemove, "statement-en");
        } else {
            removeTag(curTags, tagsToAdd, tagsToRemove, "statement-en");
        }

        removeTag(curTags, tagsToAdd, tagsToRemove, "editorial-yes");
        removeTag(curTags, tagsToAdd, tagsToRemove, "editorial-no");

        if (problemStore.hasEditorial(null, problemJid)) {
            upsertTag(curTags, tagsToAdd, tagsToRemove, "editorial-yes");

            if (problemStore.getEditorialAvailableLanguages(null, problemJid).getOrDefault("en-US", DISABLED) == ENABLED) {
                upsertTag(curTags, tagsToAdd, tagsToRemove, "editorial-en");
            } else {
                removeTag(curTags, tagsToAdd, tagsToRemove, "editorial-en");
            }
        } else {
            upsertTag(curTags, tagsToAdd, tagsToRemove, "editorial-no");
        }

        if (problemJid.startsWith("JIDPROG")) {
            removeTag(curTags, tagsToAdd, tagsToRemove, "engine-batch");
            removeTag(curTags, tagsToAdd, tagsToRemove, "engine-interactive");
            removeTag(curTags, tagsToAdd, tagsToRemove, "engine-output-only");
            removeTag(curTags, tagsToAdd, tagsToRemove, "engine-functional");

            String gradingEngine = programmingProblemStore.getGradingEngine(null, problemJid);
            if (gradingEngine.startsWith("Batch")) {
                upsertTag(curTags, tagsToAdd, tagsToRemove, "engine-batch");
            } else if (gradingEngine.startsWith("Interactive")) {
                upsertTag(curTags, tagsToAdd, tagsToRemove, "engine-interactive");
            } else if (gradingEngine.startsWith("OutputOnly")) {
                upsertTag(curTags, tagsToAdd, tagsToRemove, "engine-output-only");
            } else if (gradingEngine.startsWith("Functional")) {
                upsertTag(curTags, tagsToAdd, tagsToRemove, "engine-functional");
            }

            removeTag(curTags, tagsToAdd, tagsToRemove, "scoring-partial");
            removeTag(curTags, tagsToAdd, tagsToRemove, "scoring-subtasks");
            removeTag(curTags, tagsToAdd, tagsToRemove, "scoring-absolute");

            GradingConfig gradingConfig = programmingProblemStore.getGradingConfig(null, problemJid);

            if (gradingEngine.endsWith("WithSubtasks")) {
                if (gradingConfig.getSubtasks().size() == 1) {
                    upsertTag(curTags, tagsToAdd, tagsToRemove, "scoring-absolute");
                } else {
                    upsertTag(curTags, tagsToAdd, tagsToRemove, "scoring-subtasks");
                }
            } else {
                if (gradingConfig.getTestData().size() == 2 && gradingConfig.getTestData().get(1).getTestCases().size() == 1) {
                    upsertTag(curTags, tagsToAdd, tagsToRemove, "scoring-absolute");
                } else {
                    upsertTag(curTags, tagsToAdd, tagsToRemove, "scoring-partial");
                }
            }
        }

        applyTagUpdates(problemJid, tagsToAdd, tagsToRemove);
    }

    public Set<String> filterProblemJidsByTags(Set<String> initialProblemJids, Set<String> tags) {
        Set<String> problemJids = initialProblemJids;

        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("visibility-private", "visibility-public"));
        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("statement-en"));
        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("editorial-no", "editorial-yes", "editorial-en"));
        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("engine-batch", "engine-interactive", "engine-output-only", "engine-functional"));
        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("scoring-partial", "scoring-subtasks", "scoring-absolute"));
        problemJids = filterProblemJidsByTopicTags(problemJids, tags);

        return problemJids;
    }

    public Set<String> filterProblemJidsByTags(Set<String> problemJids, Set<String> tags, Set<String> groupTags) {
        Set<String> intersectionTags = Sets.intersection(tags, groupTags);
        if (intersectionTags.isEmpty()) {
            return problemJids;
        }

        Set<String> allowedProblemJids = problemTagDao.selectAllByTags(intersectionTags).stream()
                .map(m -> m.problemJid)
                .collect(Collectors.toSet());

        if (problemJids == null) {
            return allowedProblemJids;
        }
        return Sets.intersection(problemJids, allowedProblemJids);
    }

    public Set<String> filterProblemJidsByTopicTags(Set<String> problemJids, Set<String> tags) {
        Set<String> topicTags = tags.stream()
                .filter(tag -> tag.startsWith("topic-"))
                .filter(tag -> tags.stream().noneMatch(t -> isTagChild(tag, t)))
                .collect(Collectors.toSet());

        if (topicTags.isEmpty()) {
            return problemJids;
        }

        Set<String> allowedProblemJids = problemTagDao.selectAllByTags(topicTags).stream()
                .map(m -> m.problemJid)
                .collect(Collectors.toSet());

        if (problemJids == null) {
            return allowedProblemJids;
        }
        return Sets.intersection(problemJids, allowedProblemJids);
    }

    private Set<String> getTags(String problemJid) {
        return problemTagDao.selectAllByProblemJid(problemJid)
                .stream()
                .map(m -> m.tag)
                .collect(Collectors.toSet());
    }

    private void upsertTag(Set<String> curTags, Set<String> tagsToAdd, Set<String> tagsToRemove, String tag) {
        if (tagsToRemove.contains(tag)) {
            tagsToRemove.remove(tag);
        } else if (!curTags.contains(tag)) {
            tagsToAdd.add(tag);
        }
    }

    private void removeTag(Set<String> curTags, Set<String> tagsToAdd, Set<String> tagsToRemove, String tag) {
        if (tagsToAdd.contains(tag)) {
            tagsToAdd.remove(tag);
        } else if (curTags.contains(tag)) {
            tagsToRemove.add(tag);
        }
    }

    private void applyTagUpdates(String problemJid, Set<String> tagsToAdd, Set<String> tagsToRemove) {
        for (String tag : tagsToAdd) {
            ProblemTagModel m = new ProblemTagModel();
            m.problemJid = problemJid;
            m.tag = tag;
            problemTagDao.insert(m);
        }
        for (String tag : tagsToRemove) {
            problemTagDao.selectByProblemJidAndTag(problemJid, tag).ifPresent(problemTagDao::delete);
        }
    }

    private static boolean isTagChild(String tag, String tagChild) {
        return !tag.equals(tagChild) && tagChild.startsWith(tag);
    }
}
