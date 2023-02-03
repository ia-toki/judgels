package judgels.uriel.contest.scoreboard.bundle;

import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardContent;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardIncrementalContent;
import judgels.uriel.contest.scoreboard.ScoreboardProcessResult;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class BundleScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public BundleScoreboard parse(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, BundleScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BundleScoreboard create(ScoreboardState state, List<? extends ScoreboardEntry> entries) {
        return new BundleScoreboard.Builder()
                .state(state)
                .content(new BundleScoreboardContent.Builder()
                        .entries(Lists.transform(entries, e -> (BundleScoreboardEntry) e))
                        .build())
                .build();
    }

    @Override
    public boolean requiresGradingDetails(StyleModuleConfig styleModuleConfig) {
        return false;
    }

    @Override
    public ScoreboardProcessResult process(
            Contest contest,
            ScoreboardState scoreboardState,
            Optional<ScoreboardIncrementalContent> incrementalContent,
            StyleModuleConfig styleModuleConfig,
            Set<ContestContestant> contestants,
            Map<String, Profile> profilesMap,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime) {

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> contestantJids = contestants.stream().map(ContestContestant::getUserJid).collect(toSet());

        Map<String, List<ItemSubmission>> submissionsByUserJid = bundleItemSubmissions.stream()
                .collect(Collectors.groupingBy(ItemSubmission::getUserJid));

        List<BundleScoreboardEntry> entries = contestantJids.stream()
                .map(contestantJid -> {
                    List<Integer> contestantAnsweredItems = problemJids.stream()
                            .map(problemJid -> submissionsByUserJid
                                    .getOrDefault(contestantJid, Collections.emptyList())
                                    .stream()
                                    .filter(submission -> submission.getProblemJid().equals(problemJid))
                                    .count()
                            )
                            .map(c -> c.intValue())
                            .collect(Collectors.toList());

                    Optional<Instant> lastAnsweredTime = submissionsByUserJid
                            .getOrDefault(contestantJid, Collections.emptyList())
                            .stream()
                            .map(ItemSubmission::getTime)
                            .max(Instant::compareTo);

                    return new BundleScoreboardEntry.Builder()
                            .rank(0)
                            .contestantJid(contestantJid)
                            .answeredItems(contestantAnsweredItems)
                            .totalAnsweredItems(contestantAnsweredItems.stream().mapToInt(Integer::intValue).sum())
                            .lastAnsweredTime(lastAnsweredTime)
                            .build();
                })
                .collect(Collectors.toList());

        entries = sortEntriesAndAssignRanks(new UsingTotalAnsweredItemsBundleScoreboardEntryComparator(), entries);
        return new ScoreboardProcessResult.Builder()
                .entries(entries)
                .incrementalContent(new BundleScoreboardIncrementalContent())
                .build();
    }

    @Override
    public BundleScoreboardEntry clearEntryRank(ScoreboardEntry entry) {
        return new BundleScoreboardEntry.Builder()
                .from((BundleScoreboardEntry) entry)
                .rank(-1)
                .build();
    }

    private static List<BundleScoreboardEntry> sortEntriesAndAssignRanks(
            UsingTotalAnsweredItemsBundleScoreboardEntryComparator comparator,
            List<BundleScoreboardEntry> entries) {

        entries.sort(comparator);

        ImmutableList.Builder<BundleScoreboardEntry> newEntries = ImmutableList.builder();

        int previousRank = 0;
        for (int i = 0; i < entries.size(); i++) {
            int assignedRank;
            if (i == 0 || comparator.compareWithoutTieBreakerForEqualRanks(entries.get(i), entries.get(i - 1)) != 0) {
                assignedRank = i + 1;
            } else {
                assignedRank = previousRank;
            }
            previousRank = assignedRank;

            newEntries.add(new BundleScoreboardEntry.Builder().from(entries.get(i)).rank(assignedRank).build());
        }

        return newEntries.build();
    }
}
