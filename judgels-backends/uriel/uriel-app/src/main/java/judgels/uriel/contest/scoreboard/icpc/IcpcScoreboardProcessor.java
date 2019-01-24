package judgels.uriel.contest.scoreboard.icpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import judgels.gabriel.api.Verdicts;
import judgels.sandalphon.api.submission.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class IcpcScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public Scoreboard parseFromString(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, IcpcScoreboard.class);
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
            List<Submission> submissionList) {
        if (!(styleModuleConfig instanceof IcpcStyleModuleConfig)) {
            throw new RuntimeException("ICPC-style contest given but styleModuleConfig is not ICPC");
        }
        IcpcStyleModuleConfig icpcStyleModuleConfig = (IcpcStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> problemJidsSet = new HashSet<>(problemJids);
        Set<String> contestantJids = scoreboardState.getContestantJids();

        Map<String, List<Submission>> submissionsMap = new HashMap<>();
        submissionList.forEach(s -> {
            submissionsMap.putIfAbsent(s.getUserJid(), new ArrayList<>());
            submissionsMap.get(s.getUserJid()).add(s);
        });

        Map<String, String> firstSolveSubmissionJid = new HashMap<>();
        submissionList.stream()
                .filter(s -> s.getLatestGrading().isPresent()
                        && s.getLatestGrading().get().getVerdict().equals(Verdicts.ACCEPTED)
                        && contestantJids.contains(s.getUserJid()))
                .forEach(s -> firstSolveSubmissionJid.putIfAbsent(s.getProblemJid(), s.getJid()));

        List<IcpcScoreboardEntry> scoreboardEntryList = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            List<Submission> submissions = submissionsMap.get(contestantJid);
            long lastAcceptedPenalty = 0;
            Map<String, Integer> attemptsMap = new HashMap<>();
            problemJids.forEach(p -> attemptsMap.put(p, 0));
            Map<String, Long> penaltyMap = new HashMap<>();
            problemJids.forEach(p -> penaltyMap.put(p, 0L));
            Map<String, IcpcScoreboardProblemState> problemStateMap = new HashMap<>();
            problemJids.forEach(p -> problemStateMap.put(p, IcpcScoreboardProblemState.NOT_ACCEPTED));

            for (Submission submission : submissions) {
                if (!contestantJids.contains(submission.getUserJid())) {
                    continue;
                }

                if (!problemJidsSet.contains(submission.getProblemJid())) {
                    continue;
                }

                if (!problemStateMap.get(submission.getProblemJid()).equals(IcpcScoreboardProblemState.NOT_ACCEPTED)) {
                    continue;
                }

                if (!submission.getLatestGrading().isPresent()
                        || submission.getLatestGrading().get().getVerdict().equals(Verdicts.PENDING)) {
                    continue;
                }

                attemptsMap.put(submission.getProblemJid(), attemptsMap.get(submission.getProblemJid()) + 1);

                long submissionPenalty = submissionPenaltyInMillisecond(
                        submission.getTime(),
                        contestantStartTimesMap.get(contestantJid),
                        contest.getBeginTime());
                penaltyMap.put(submission.getProblemJid(), convertPenaltyToMinutes(submissionPenalty));

                if (submission.getLatestGrading().get().getVerdict().equals(Verdicts.ACCEPTED)) {
                    if (firstSolveSubmissionJid.get(submission.getProblemJid()).equals(submission.getJid())) {
                        problemStateMap.put(
                                submission.getProblemJid(),
                                IcpcScoreboardProblemState.FIRST_ACCEPTED);
                    } else {
                        problemStateMap.put(
                                submission.getProblemJid(),
                                IcpcScoreboardProblemState.ACCEPTED);
                    }

                    lastAcceptedPenalty = submissionPenalty;
                }
            }

            scoreboardEntryList.add(new IcpcScoreboardEntry.Builder()
                    .rank(0)
                    .contestantJid(contestantJid)
                    .totalAccepted((int) problemStateMap.values()
                            .stream()
                            .filter(s -> s != IcpcScoreboardProblemState.NOT_ACCEPTED)
                            .count())
                    .totalPenalties(penaltyMap.keySet().stream()
                            .filter(p -> problemStateMap.get(p) != IcpcScoreboardProblemState.NOT_ACCEPTED)
                            .mapToLong(p -> ((attemptsMap.get(p) - 1)
                                    * icpcStyleModuleConfig.getWrongSubmissionPenalty())
                                    + penaltyMap.get(p))
                            .sum())
                    .lastAcceptedPenalty(lastAcceptedPenalty)
                    .addAllAttemptsList(problemJids
                            .stream()
                            .map(attemptsMap::get)
                            .collect(Collectors.toList()))
                    .addAllPenaltyList(problemJids
                            .stream()
                            .map(penaltyMap::get)
                            .collect(Collectors.toList()))
                    .addAllProblemStateList(problemJids
                            .stream()
                            .map(problemStateMap::get)
                            .collect(Collectors.toList()))
                    .build());
        }

        scoreboardEntryList = sortEntriesAndAssignRanks(scoreboardEntryList);

        try {
            return mapper.writeValueAsString(new IcpcScoreboard.Builder()
                    .state(scoreboardState)
                    .content(new IcpcScoreboardContent.Builder()
                            .addAllEntries(scoreboardEntryList)
                            .build())
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids) {
        IcpcScoreboard icpcScoreboard = (IcpcScoreboard) scoreboard;

        Set<String> filteredContestantJids = icpcScoreboard.getState().getContestantJids()
                .stream()
                .filter(contestantJids::contains)
                .collect(Collectors.toSet());

        ScoreboardState filteredState = new ScoreboardState.Builder()
                .from(icpcScoreboard.getState())
                .contestantJids(filteredContestantJids)
                .build();

        List<IcpcScoreboardEntry> filteredEntries = icpcScoreboard.getContent().getEntries()
                .stream()
                .filter(entry -> contestantJids.contains(entry.getContestantJid()))
                .map(entry -> new IcpcScoreboardEntry.Builder()
                        .from(entry)
                        .rank(-1)
                        .build())
                .collect(Collectors.toList());

        return new IcpcScoreboard.Builder()
                .state(filteredState)
                .content(new IcpcScoreboardContent.Builder().entries(filteredEntries).build())
                .build();
    }

    private List<IcpcScoreboardEntry> sortEntriesAndAssignRanks(List<IcpcScoreboardEntry> scoreboardEntries) {
        Comparator<IcpcScoreboardEntry> comparator = new StandardIcpcScoreboardEntryComparator();
        scoreboardEntries.sort(comparator);

        List<IcpcScoreboardEntry> result = new ArrayList<>();

        int currentRank = 1;
        for (int i = 0; i < scoreboardEntries.size(); i++) {
            if (i != 0 && comparator.compare(scoreboardEntries.get(i), scoreboardEntries.get(i - 1)) != 0) {
                currentRank = i + 1;
            }
            result.add(new IcpcScoreboardEntry.Builder().from(scoreboardEntries.get(i)).rank(currentRank).build());
        }

        return result;
    }

    private Long submissionPenaltyInMillisecond(
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
}
