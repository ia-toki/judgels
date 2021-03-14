package org.iatoki.judgels.sandalphon.problem.base.tag;

import static org.iatoki.judgels.sandalphon.StatementLanguageStatus.DISABLED;
import static org.iatoki.judgels.sandalphon.StatementLanguageStatus.ENABLED;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.gabriel.api.GradingConfig;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import org.iatoki.judgels.sandalphon.problem.base.ProblemDao;
import org.iatoki.judgels.sandalphon.problem.base.ProblemModel;
import org.iatoki.judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProblemTagStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemTagStore.class);

    private final ProblemDao problemDao;
    private final ProblemTagDao problemTagDao;
    private final ProblemStore problemStore;
    private final ProgrammingProblemStore programmingProblemStore;

    @Inject
    public ProblemTagStore(
            ProblemDao problemDao,
            ProblemTagDao problemTagDao,
            ProblemStore problemStore,
            ProgrammingProblemStore programmingProblemStore) {

        this.problemDao = problemDao;
        this.problemTagDao = problemTagDao;
        this.problemStore = problemStore;
        this.programmingProblemStore = programmingProblemStore;
    }

    public void refreshDerivedTags(String problemJid) {
        Set<String> curTags = problemTagDao.selectAllByProblemJid(problemJid)
                .stream()
                .map(m -> m.tag)
                .collect(Collectors.toSet());
        Set<String> tagsToAdd = Sets.newHashSet();
        Set<String> tagsToRemove = Sets.newHashSet();

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

    public Set<String> filterProblemJidsByTags(Set<String> initialProblemJids, Set<String> tags) {
        Set<String> problemJids = initialProblemJids;

        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("visibility-private", "visibility-public"));
        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("statement-en"));
        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("editorial-no", "editorial-yes", "editorial-en"));
        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("engine-batch", "engine-interactive", "engine-output-only", "engine-functional"));
        problemJids = filterProblemJidsByTags(problemJids, tags, ImmutableSet.of("scoring-partial", "scoring-subtasks", "scoring-absolute"));

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

    public long refreshProblemDerivedTags(long lastProblemId, long limit) {
        List<ProblemModel> models = problemDao.selectAll(new FilterOptions.Builder<ProblemModel>()
                .lastId(lastProblemId)
                .build(), new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderDir(OrderDir.ASC)
                .pageSize((int) limit)
                .build());

        long lastId = lastProblemId;
        for (ProblemModel model : models) {
            try {
                refreshDerivedTags(model.jid);
            } catch (Exception e) {
                LOGGER.error("Failed to refresh problem derived tags for " + model.jid, e);
            }
            lastId = model.id;
        }
        return lastId;
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
}
