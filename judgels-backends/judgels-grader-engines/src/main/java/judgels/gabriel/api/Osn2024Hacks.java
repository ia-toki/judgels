package judgels.gabriel.api;

import java.util.Arrays;
import java.util.List;

public class Osn2024Hacks {
    private Osn2024Hacks() {}

    private static final List<String> CERTAIN_PROBLEM_JIDS = Arrays.asList(
            "JIDPROG0NHjqvK7B7oiYf6mOOov",
            "JIDPROGiZN73iuSVYJE5btHUzni",
            "JIDPROGbDLh6vwDRHTXFijN3HYC",
            "JIDPROGSohGUsEykcREuewkE2cn"
    );

    public static String checkForHack(String problemJid, String gradingConfig) {
        if (CERTAIN_PROBLEM_JIDS.contains(problemJid)) {
            String hacked = gradingConfig;
            hacked = hacked.substring(0, hacked.length() - 1);
            hacked = hacked + ",\"scoringConfig\":{\"roundingMode\":\"FLOOR\"}}";
            return hacked;
        }
        return gradingConfig;
    }
}
