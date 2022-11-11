package judgels.uriel.contest.scoreboard.troc;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import judgels.gabriel.api.Verdict;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.module.TrocStyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard.TrocScoreboardContent;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard.TrocScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard.TrocScoreboardProblemState;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;
import judgels.uriel.contest.scoreboard.ScoreboardIncrementalContent;
import judgels.uriel.contest.scoreboard.ScoreboardProcessResult;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class TrocScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public TrocScoreboard parse(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, TrocScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TrocScoreboard create(ScoreboardState state, List<? extends ScoreboardEntry> entries) {
        return new TrocScoreboard.Builder()
                .state(state)
                .content(new TrocScoreboardContent.Builder()
                        .entries(Lists.transform(entries, e -> (TrocScoreboardEntry) e))
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

        TrocStyleModuleConfig trocStyleModuleConfig = (TrocStyleModuleConfig) styleModuleConfig;
        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> contestantJids = contestants.stream().map(ContestContestant::getUserJid).collect(toSet());
        Map<String, Optional<Instant>> contestantStartTimesMap = contestants.stream()
                .collect(toMap(ContestContestant::getUserJid, ContestContestant::getContestStartTime));

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
        contestantJids.forEach(c -> submissionsMap.put(c, new ArrayList<>()));
        programmingSubmissions.forEach(s -> submissionsMap.get(s.getUserJid()).add(s));
        Optional<TrocScoreboardIncrementalContent> maybeIncrementalContent =
                incrementalContent.map(content -> (TrocScoreboardIncrementalContent) content);

        Map<String, String> firstToSolveSubmissionJids = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getFirstToSolveSubmissionJids()).orElse(emptyMap()));
        Map<String, Map<String, Integer>> attemptsMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getAttemptsMapsByContestantJid()).orElse(emptyMap()));
        Map<String, Map<String, Long>> penaltyMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getPenaltyMapsByContestantJid()).orElse(emptyMap()));
        Map<String, Map<String, TrocScoreboardProblemState>> problemStateMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getProblemStateMapsByContestantJid()).orElse(emptyMap()));
        Map<String, String> currentFirstToSolveSubmissionJids = new HashMap<>(firstToSolveSubmissionJids);

        Optional<Long> nextLastSubmissionId = maybeIncrementalContent.flatMap(ic -> ic.getLastSubmissionId());
        for (Submission s : programmingSubmissions) {
            if (!s.getLatestGrading().isPresent() || s.getLatestGrading().get().getVerdict() == Verdict.PENDING) {
                break;
            }
            if (s.getLatestGrading().get().getVerdict() == Verdict.ACCEPTED) {
                currentFirstToSolveSubmissionJids.putIfAbsent(s.getProblemJid(), s.getJid());
            }
            nextLastSubmissionId = Optional.of(s.getId());
        }
        firstToSolveSubmissionJids = ImmutableMap.copyOf(currentFirstToSolveSubmissionJids);

        List<TrocScoreboardEntry> entries = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            Map<String, Integer> attemptsMap = new HashMap<>(
                    attemptsMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));
            Map<String, Long> penaltyMap = new HashMap<>(
                    penaltyMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));
            Map<String, TrocScoreboardProblemState> problemStateMap = new HashMap<>(
                    problemStateMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));

            Profile contestantProfile = profilesMap.get(contestantJid);
            Optional<UserRating> maybeUserRating = contestantProfile.getRating();
            UserRating userRating = maybeUserRating.orElseGet(
                    () -> new UserRating.Builder().publicRating(0).hiddenRating(0).build());

            problemJids.forEach(p -> {
                attemptsMap.putIfAbsent(p, 0);
                penaltyMap.putIfAbsent(p, 0L);
                problemStateMap.putIfAbsent(p, TrocScoreboardProblemState.NOT_ACCEPTED);
            });

            for (Submission submission : submissionsMap.get(contestantJid)) {
                String problemJid = submission.getProblemJid();
                if (isAccepted(problemStateMap.get(problemJid))) {
                    continue;
                }

                if (submission.getTime().isBefore(freezeTime.orElse(Instant.MAX))) {
                    Verdict verdict = submission.getLatestGrading().get().getVerdict();
                    if (verdict.equals(Verdict.PENDING) || verdict.equals(Verdict.COMPILATION_ERROR)) {
                        continue;
                    }

                    attemptsMap.put(problemJid, attemptsMap.get(problemJid) + 1);

                    if (verdict.equals(Verdict.ACCEPTED)) {
                        if (submission.getJid().equals(currentFirstToSolveSubmissionJids.get(problemJid))) {
                            problemStateMap.put(problemJid, TrocScoreboard.TrocScoreboardProblemState.FIRST_ACCEPTED);
                        } else {
                            problemStateMap.put(problemJid, TrocScoreboard.TrocScoreboardProblemState.ACCEPTED);
                        }
                        long penalty = computePenalty(
                                submission.getTime(),
                                contestantStartTimesMap.get(contestantJid),
                                contest.getBeginTime());
                        penaltyMap.put(problemJid, convertPenaltyToMinutes(penalty));
                    }
                } else {
                    problemStateMap.put(problemJid, TrocScoreboardProblemState.FROZEN);
                }

                if (submission.getId() <= nextLastSubmissionId.orElse(Long.MAX_VALUE)) {
                    attemptsMapsByContestantJid.put(contestantJid, ImmutableMap.copyOf(attemptsMap));
                    penaltyMapsByContestantJid.put(contestantJid, ImmutableMap.copyOf(penaltyMap));
                    problemStateMapsByContestantJid.put(contestantJid, ImmutableMap.copyOf(problemStateMap));
                }
            }

            entries.add(new TrocScoreboardEntry.Builder()
                    .rank(0)
                    .contestantJid(contestantJid)
                    .contestantUsername(contestantProfile.getUsername())
                    .contestantRating(userRating.getPublicRating())
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
                            .mapToLong(p -> (attemptsMap.get(p) - 1)
                                    * trocStyleModuleConfig.getWrongSubmissionPenalty())
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

        return new ScoreboardProcessResult.Builder()
                .entries(entries)
                .incrementalContent(new TrocScoreboardIncrementalContent.Builder()
                        .lastSubmissionId(nextLastSubmissionId)
                        .firstToSolveSubmissionJids(firstToSolveSubmissionJids)
                        .attemptsMapsByContestantJid(attemptsMapsByContestantJid)
                        .penaltyMapsByContestantJid(penaltyMapsByContestantJid)
                        .problemStateMapsByContestantJid(problemStateMapsByContestantJid)
                        .build())
                .build();
    }

    @Override
    public TrocScoreboardEntry clearEntryRank(ScoreboardEntry entry) {
        return new TrocScoreboardEntry.Builder()
                .from((TrocScoreboardEntry) entry)
                .rank(-1)
                .build();
    }

    private List<TrocScoreboardEntry> sortEntriesAndAssignRanks(List<TrocScoreboardEntry> entries) {
        ScoreboardEntryComparator<TrocScoreboardEntry> comparator = new StandardTrocScoreboardEntryComparator();
        entries.sort(comparator);

        List<TrocScoreboardEntry> result = new ArrayList<>();

        int currentRank = 1;
        for (int i = 0; i < entries.size(); i++) {
            if (i != 0 && comparator.compareWithoutTieBreakerForEqualRanks(entries.get(i), entries.get(i - 1)) != 0) {
                currentRank = i + 1;
            }
            result.add(new TrocScoreboardEntry.Builder().from(entries.get(i)).rank(currentRank).build());
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

    private boolean isAccepted(TrocScoreboardProblemState problemState) {
        return problemState.equals(TrocScoreboardProblemState.FIRST_ACCEPTED)
                || problemState.equals(TrocScoreboardProblemState.ACCEPTED);
    }
}
