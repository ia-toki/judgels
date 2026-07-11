package judgels.contest.scoreboard.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.api.contest.contestant.ContestContestant;
import judgels.api.contest.module.StyleModuleConfig;
import judgels.api.contest.scoreboard.BundleScoreboard;
import judgels.api.contest.scoreboard.BundleScoreboard.BundleScoreboardContent;
import judgels.api.contest.scoreboard.BundleScoreboard.BundleScoreboardEntry;
import judgels.api.contest.scoreboard.ScoreboardEntry;
import judgels.api.contest.scoreboard.ScoreboardState;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.contest.scoreboard.ScoreboardIncrementalContent;
import judgels.contest.scoreboard.ScoreboardProcessParams;
import judgels.contest.scoreboard.ScoreboardProcessResult;
import judgels.contest.scoreboard.ScoreboardProcessor;

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
    public ScoreboardProcessResult process(ScoreboardProcessParams param) {
        ScoreboardState scoreboardState = param.getScoreboardState();
        Optional<ScoreboardIncrementalContent> incrementalContent = param.getIncrementalContent();
        StyleModuleConfig styleModuleConfig = param.getStyleModuleConfig();
        Map<String, Set<ContestContestant>> contestContestantsMap = param.getContestContestantsMap();
        List<ItemSubmission> bundleItemSubmissions = param.getBundleItemSubmissions();
        Set<String> unofficialContestantJids = param.getUnofficialContestantJids();

        List<String> problemJids = scoreboardState.getProblemJids();

        Set<String> contestantJids = new HashSet<>();
        for (var entry : contestContestantsMap.entrySet()) {
            for (ContestContestant contestant : entry.getValue()) {
                contestantJids.add(contestant.getUserJid());
            }
        }

        Map<String, List<ItemSubmission>> submissionsByUserJid = bundleItemSubmissions.stream()
                .collect(Collectors.groupingBy(ItemSubmission::getUserJid));

        List<BundleScoreboardEntry> entries = contestantJids.stream()
                .map(contestantJid -> {
                    Map<String, Double> scoresMap = new HashMap<>();
                    problemJids.forEach(p -> scoresMap.putIfAbsent(p, 0.0));

                    for (ItemSubmission submission : submissionsByUserJid.getOrDefault(contestantJid, List.of())) {
                        if (!scoresMap.containsKey(submission.getProblemJid())) {
                            continue;
                        }
                        if (submission.getGrading().isEmpty() || submission.getGrading().get().getScore().isEmpty()) {
                            continue;
                        }
                        scoresMap.put(
                                submission.getProblemJid(),
                                scoresMap.get(submission.getProblemJid()) + submission.getGrading().get().getScore().get());
                    }

                    Optional<Instant> lastAnsweredTime = submissionsByUserJid
                            .getOrDefault(contestantJid, Collections.emptyList())
                            .stream()
                            .map(ItemSubmission::getTime)
                            .max(Instant::compareTo);

                    double totalScores = 0;
                    for (double score : scoresMap.values()) {
                        totalScores += score;
                    }

                    return new BundleScoreboardEntry.Builder()
                            .rank(0)
                            .contestantJid(contestantJid)
                            .scores(problemJids
                                    .stream()
                                    .map(jid -> (int) (double) scoresMap.get(jid))
                                    .collect(Collectors.toList()))
                            .totalScores((int) totalScores)
                            .lastAnsweredTime(lastAnsweredTime)
                            .build();
                })
                .collect(Collectors.toList());

        entries = sortEntriesAndAssignRanks(
                new UsingTotalScoresBundleScoreboardEntryComparator(), entries, unofficialContestantJids);
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
            UsingTotalScoresBundleScoreboardEntryComparator comparator,
            List<BundleScoreboardEntry> entries,
            Set<String> unofficialContestantJids) {

        entries.sort(comparator);

        ImmutableList.Builder<BundleScoreboardEntry> newEntries = ImmutableList.builder();

        int officialRank = 0;
        int officialCount = 0;
        BundleScoreboardEntry previousOfficial = null;
        for (BundleScoreboardEntry entry : entries) {
            if (unofficialContestantJids.contains(entry.getContestantJid())) {
                newEntries.add(new BundleScoreboardEntry.Builder().from(entry).rank(0).build());
                continue;
            }

            officialCount++;
            if (previousOfficial == null
                    || comparator.compareWithoutTieBreakerForEqualRanks(entry, previousOfficial) != 0) {
                officialRank = officialCount;
            }
            previousOfficial = entry;

            newEntries.add(new BundleScoreboardEntry.Builder().from(entry).rank(officialRank).build());
        }

        return newEntries.build();
    }
}
