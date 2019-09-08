package judgels.uriel.contest.rating;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import judgels.jophiel.api.user.rating.UserRating;

public class ContestRatingComputer {
    @Inject
    public ContestRatingComputer() {}

    public Map<String, UserRating> compute(
            List<String> contestantJids,
            Map<String, Integer> ranksMap,
            Map<String, Integer> publicRatingsMap,
            Map<String, Integer> hiddenRatingsMap) {

        ImmutableMap.Builder<String, UserRating> result = ImmutableMap.builder();

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
        return result.build();
    }

    private static double getScore(int N, double hiddenA, double hiddenB) {
        return Math.max(10.0, (sigmoid(Math.sqrt(hiddenB / hiddenA)) - .7) * log2(N) * UserRating.INITIAL_RATING);
    }

    private static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
