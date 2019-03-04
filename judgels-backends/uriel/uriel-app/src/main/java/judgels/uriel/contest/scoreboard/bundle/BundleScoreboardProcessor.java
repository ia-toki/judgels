package judgels.uriel.contest.scoreboard.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.BundleStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardContent;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class BundleScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public Scoreboard parseFromString(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, BundleScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String computeToString(
            ObjectMapper mapper,
            ScoreboardState scoreboardState,
            Contest contest,
            StyleModuleConfig styleModuleConfig,
            Map<String, Optional<Instant>> contestantStartTimesMap,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime) {

        BundleStyleModuleConfig bundleStyleModuleConfig = (BundleStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        Set<String> contestantJids = scoreboardState.getContestantJids();

        List<ItemSubmission> filteredSubmissions = bundleItemSubmissions.stream()
                .filter(s -> contestantJids.contains(s.getUserJid()))
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .collect(Collectors.toList());

        Map<String, List<ItemSubmission>> submissionsByUserJid = filteredSubmissions.stream()
                .collect(Collectors.groupingBy(ItemSubmission::getUserJid));

        List<BundleScoreboardEntry> entries = contestantJids.stream()
                .map(contestantJid -> {
                    List<Integer> contestantAnsweredItems = problemJids.stream()
                            .map(problemJid -> submissionsByUserJid
                                    .getOrDefault(contestantJid, Collections.emptyList())
                                    .stream()
                                    .filter(submission -> submission.getProblemJid().equals(problemJid))
                                    .filter(submission -> !submission.getAnswer().isEmpty())
                                    .distinct()
                                    .count()
                            )
                            .map(Long::intValue)
                            .collect(Collectors.toList());

                    Optional<Instant> lastAnsweredTime = submissionsByUserJid
                            .getOrDefault(contestantJid, Collections.emptyList())
                            .stream()
                            .filter(submission -> !submission.getAnswer().isEmpty())
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

        entries = sortEntriesAndAssignRanks(
                new UsingTotalAnsweredItemsBundleScoreboardEntryComparator(),
                entries);

        try {
            return mapper.writeValueAsString(new BundleScoreboard.Builder()
                    .state(scoreboardState)
                    .content(new BundleScoreboardContent.Builder()
                        .entries(entries)
                        .build())
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getTotalEntries(Scoreboard scoreboard) {
        return ((BundleScoreboard) scoreboard).getContent().getEntries().size();
    }

    @Override
    public List<BundleScoreboardEntry> getEntries(Scoreboard scoreboard) {
        return ((BundleScoreboard) scoreboard).getContent().getEntries();
    }

    @Override
    public Scoreboard replaceEntries(Scoreboard scoreboard, List<?> entries) {
        BundleScoreboard bundleScoreboard = (BundleScoreboard) scoreboard;
        return new BundleScoreboard.Builder()
                .state(bundleScoreboard.getState())
                .content(new BundleScoreboardContent.Builder()
                        .entries((List<? extends BundleScoreboardEntry>) entries)
                        .build())
                .build();
    }

    @Override
    public Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids) {
        BundleScoreboard bundleScoreboard = (BundleScoreboard) scoreboard;

        Set<String> filteredContestantJids = bundleScoreboard.getState().getContestantJids()
                .stream()
                .filter(contestantJids::contains)
                .collect(Collectors.toSet());

        ScoreboardState filteredState = new ScoreboardState.Builder()
                .from(bundleScoreboard.getState())
                .contestantJids(filteredContestantJids)
                .build();

        List<BundleScoreboardEntry> filteredEntries = bundleScoreboard.getContent().getEntries()
                .stream()
                .filter(entry -> contestantJids.contains(entry.getContestantJid()))
                .map(entry -> new BundleScoreboardEntry.Builder()
                        .from(entry)
                        .rank(-1)
                        .build())
                .collect(Collectors.toList());

        return new BundleScoreboard.Builder()
                .state(filteredState)
                .content(new BundleScoreboardContent.Builder().entries(filteredEntries).build())
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
