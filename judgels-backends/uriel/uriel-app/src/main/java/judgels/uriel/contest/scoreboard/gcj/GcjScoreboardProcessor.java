package judgels.uriel.contest.scoreboard.gcj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.GcjStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardContent;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class GcjScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public Scoreboard parseFromString(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, GcjScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getTotalEntries(Scoreboard scoreboard) {
        return ((GcjScoreboard) scoreboard).getContent().getEntries().size();
    }

    @Override
    public List<?> getEntries(Scoreboard scoreboard) {
        return ((GcjScoreboard) scoreboard).getContent().getEntries();
    }

    @Override
    public Scoreboard replaceEntries(Scoreboard scoreboard, List<?> entries) {
        GcjScoreboard gcjScoreboard = (GcjScoreboard) scoreboard;
        return new GcjScoreboard.Builder()
                .state(gcjScoreboard.getState())
                .content(new GcjScoreboardContent.Builder()
                        .entries((List<? extends GcjScoreboardEntry>) entries)
                        .build())
                .build();
    }

    @Override
    public String computeToString(
            ObjectMapper mapper,
            ScoreboardState scoreboardState,
            Contest contest,
            StyleModuleConfig styleModuleConfig,
            Map<String, Optional<Instant>> contestantStartTimesMap,
            List<Submission> submissions,
            Optional<Instant> freezeTime) {
        GcjStyleModuleConfig gcjStyleModuleConfig = (GcjStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        Set<String> contestantJids = scoreboardState.getContestantJids();

        Map<String, Integer> pointsMap = new HashMap<>();
        if (scoreboardState.getProblemPoints().isPresent()) {
            for (int i = 0; i < scoreboardState.getProblemJids().size(); i++) {
                pointsMap.putIfAbsent(
                        scoreboardState.getProblemJids().get(i),
                        scoreboardState.getProblemPoints().get().get(i));
            }
        } else {
            scoreboardState.getProblemJids().forEach(p -> pointsMap.putIfAbsent(p, 0));
        }

        Map<String, List<Submission>> submissionsMap = new HashMap<>();
        Map<String, List<Submission>> frozenSubmissionsMap = new HashMap<>();
        contestantJids.forEach(c -> {
            submissionsMap.put(c, new ArrayList<>());
            frozenSubmissionsMap.put(c, new ArrayList<>());
        });

        List<Submission> filteredSubmissions = submissions.stream()
                .filter(s -> contestantJids.contains(s.getUserJid()))
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .collect(Collectors.toList());

        filteredSubmissions.stream()
                .filter(s -> s.getLatestGrading().isPresent())
                .filter(s -> s.getTime().isBefore(freezeTime.orElse(Instant.MAX)))
                .forEach(s -> submissionsMap.get(s.getUserJid()).add(s));

        filteredSubmissions.stream()
                .filter(s -> !s.getTime().isBefore(freezeTime.orElse(Instant.MAX)))
                .forEach(s -> frozenSubmissionsMap.get(s.getUserJid()).add(s));

        List<GcjScoreboardEntry> entries = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            Map<String, Integer> attemptsMap = new HashMap<>();
            Map<String, Long> penaltyMap = new HashMap<>();
            Map<String, GcjScoreboardProblemState> problemStateMap = new HashMap<>();

            problemJids.forEach(p -> {
                attemptsMap.put(p, 0);
                penaltyMap.put(p, 0L);
                problemStateMap.put(p, GcjScoreboardProblemState.NOT_ACCEPTED);
            });

            for (Submission submission : submissionsMap.get(contestantJid)) {
                String problemJid = submission.getProblemJid();

                Verdict verdict = submission.getLatestGrading().get().getVerdict();
                if (verdict.equals(Verdicts.PENDING)) {
                    continue;
                }

                if (!verdict.equals(Verdicts.ACCEPTED)) {
                    attemptsMap.put(problemJid, attemptsMap.get(problemJid) + 1);
                }

                if (!isAccepted(problemStateMap.get(problemJid)) && verdict.equals(Verdicts.ACCEPTED)) {
                    long penalty = computePenalty(
                            submission.getTime(),
                            contestantStartTimesMap.get(contestantJid),
                            contest.getBeginTime());
                    penaltyMap.put(problemJid, convertPenaltyToMinutes(penalty));
                    problemStateMap.put(problemJid, GcjScoreboardProblemState.ACCEPTED);
                }
            }

            for (Submission submission : frozenSubmissionsMap.get(contestantJid)) {
                String problemJid = submission.getProblemJid();
                if (isAccepted(problemStateMap.get(problemJid))) {
                    continue;
                }

                problemStateMap.put(problemJid, GcjScoreboardProblemState.FROZEN);
            }

            entries.add(new GcjScoreboardEntry.Builder()
                    .rank(0)
                    .contestantJid(contestantJid)
                    .totalPoints(problemJids.stream()
                            .filter(p -> isAccepted(problemStateMap.get(p)))
                            .mapToInt(p -> scoreboardState.getProblemPoints().map($ -> pointsMap.get(p)).orElse(0))
                            .sum())
                    .totalPenalties(penaltyMap.keySet().stream()
                            .filter(p -> isAccepted(problemStateMap.get(p)))
                            .mapToLong(penaltyMap::get)
                            .max()
                            .orElse(0)
                            + penaltyMap.keySet().stream()
                            .filter(p -> isAccepted(problemStateMap.get(p)))
                            .mapToLong(p -> attemptsMap.get(p)
                                    * gcjStyleModuleConfig.getWrongSubmissionPenalty())
                            .sum()
                    )
                    .attemptsList(problemJids
                            .stream()
                            .map(attemptsMap::get)
                            .collect(Collectors.toList()))
                    .penaltyList(problemJids
                            .stream()
                            .map(penaltyMap::get)
                            .collect(Collectors.toList()))
                    .problemStateList(problemJids
                            .stream()
                            .map(problemStateMap::get)
                            .collect(Collectors.toList()))
                    .build());
        }

        entries = sortEntriesAndAssignRanks(entries);

        try {
            return mapper.writeValueAsString(new GcjScoreboard.Builder()
                    .state(scoreboardState)
                    .content(new GcjScoreboardContent.Builder()
                            .entries(entries)
                            .build())
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids) {
        GcjScoreboard gcjScoreboard = (GcjScoreboard) scoreboard;

        Set<String> filteredContestantJids = gcjScoreboard.getState().getContestantJids()
                .stream()
                .filter(contestantJids::contains)
                .collect(Collectors.toSet());

        ScoreboardState filteredState = new ScoreboardState.Builder()
                .from(gcjScoreboard.getState())
                .contestantJids(filteredContestantJids)
                .build();

        List<GcjScoreboardEntry> filteredEntries = gcjScoreboard.getContent().getEntries()
                .stream()
                .filter(entry -> contestantJids.contains(entry.getContestantJid()))
                .map(entry -> new GcjScoreboardEntry.Builder()
                        .from(entry)
                        .rank(-1)
                        .build())
                .collect(Collectors.toList());

        return new GcjScoreboard.Builder()
                .state(filteredState)
                .content(new GcjScoreboardContent.Builder().entries(filteredEntries).build())
                .build();
    }

    private List<GcjScoreboardEntry> sortEntriesAndAssignRanks(List<GcjScoreboardEntry> scoreboardEntries) {
        Comparator<GcjScoreboardEntry> comparator = new StandardGcjScoreboardEntryComparator();
        scoreboardEntries.sort(comparator);

        List<GcjScoreboardEntry> result = new ArrayList<>();

        int currentRank = 1;
        for (int i = 0; i < scoreboardEntries.size(); i++) {
            if (i != 0 && comparator.compare(scoreboardEntries.get(i), scoreboardEntries.get(i - 1)) != 0) {
                currentRank = i + 1;
            }
            result.add(new GcjScoreboardEntry.Builder().from(scoreboardEntries.get(i)).rank(currentRank).build());
        }

        return result;
    }

    private long computePenalty(
            Instant submissionTime,
            Optional<Instant> contestantStartTime,
            Instant contestBeginTime) {

        return submissionTime.toEpochMilli() - contestantStartTime.orElse(contestBeginTime).toEpochMilli();
    }

    private long convertPenaltyToMinutes(long penaltyInMilliseconds) {
        long penaltyInMinutes = TimeUnit.MILLISECONDS.toMinutes(penaltyInMilliseconds);
        if (TimeUnit.MINUTES.toMillis(penaltyInMinutes) != penaltyInMilliseconds) {
            penaltyInMinutes++;
        }

        return penaltyInMinutes;
    }

    private boolean isAccepted(GcjScoreboardProblemState problemState) {
        return problemState.equals(GcjScoreboardProblemState.ACCEPTED);
    }
}
