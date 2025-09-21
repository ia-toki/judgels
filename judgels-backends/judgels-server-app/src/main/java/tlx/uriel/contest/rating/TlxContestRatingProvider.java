package tlx.uriel.contest.rating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.contrib.uriel.contest.rating.ContestRatingProvider;
import judgels.jophiel.api.user.rating.UserRating;
import tlx.jophiel.api.user.rating.TlxRating;

public class TlxContestRatingProvider implements ContestRatingProvider {
    @Override
    public boolean isRatingInDivision(Optional<UserRating> rating, int division) {
        int publicRating = rating.map(UserRating::getPublicRating).orElse(TlxRating.INITIAL_RATING);
        if (division == 2) {
            return publicRating < 2000;
        }
        if (division == 1) {
            return publicRating >= 2000;
        }
        return false;
    }

    @Override
    public Map<String, UserRating> getUpdatedRatings(
            List<String> contestantJids,
            Map<String, Integer> ranksMap,
            Map<String, UserRating> currentRatingsMap) {

        Map<String, Integer> publicRatingsMap = new HashMap<>();
        Map<String, Integer> hiddenRatingsMap = new HashMap<>();

        for (String contestantJid : contestantJids) {
            UserRating rating = currentRatingsMap.get(contestantJid);
            if (currentRatingsMap.containsKey(contestantJid)) {
                publicRatingsMap.put(contestantJid, rating.getPublicRating());
                hiddenRatingsMap.put(contestantJid, rating.getHiddenRating());
            } else {
                publicRatingsMap.put(contestantJid, TlxRating.INITIAL_RATING);
                hiddenRatingsMap.put(contestantJid, TlxRating.INITIAL_RATING);
            }
        }

        Map<String, UserRating> result = new HashMap<>();

        int N = contestantJids.size();
        for (String cA : contestantJids) {
            int rankA = ranksMap.get(cA);
            double hiddenA = hiddenRatingsMap.get(cA);
            double publicA = publicRatingsMap.get(cA);

            double delta = 0;
            for (String cB : contestantJids) {
                if (cA.equals(cB)) {
                    continue;
                }

                int rankB = ranksMap.get(cB);
                int hiddenB = hiddenRatingsMap.get(cB);

                if (rankA < rankB) {
                    delta += getScore(N, hiddenA, hiddenB);
                } else if (rankA > rankB) {
                    delta -= getScore(N, hiddenB, hiddenA);
                }
            }
            delta /= N;

            double debt = hiddenA - publicA;
            if (delta >= 0) {
                publicA += .2 * delta;
                debt += .8 * delta;
                if (debt > 0) {
                    publicA += debt;
                    debt = 0;
                }
            } else {
                debt += delta;
                publicA += .5 * debt;
                debt = .5 * debt;
            }

            hiddenA = publicA + debt;

            result.put(cA, new UserRating.Builder()
                    .hiddenRating((int) hiddenA)
                    .publicRating((int) publicA)
                    .build());
        }
        return Map.copyOf(result);
    }

    private static double getScore(int N, double hiddenA, double hiddenB) {
        return Math.max(10.0, (sigmoid(Math.sqrt(hiddenB / hiddenA)) - .7) * log2(N) * TlxRating.INITIAL_RATING);
    }

    private static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
