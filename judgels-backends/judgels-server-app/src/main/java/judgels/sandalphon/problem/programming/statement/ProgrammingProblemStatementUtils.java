package judgels.sandalphon.problem.programming.statement;

import java.io.IOException;
import java.io.InputStream;

public final class ProgrammingProblemStatementUtils {
    private ProgrammingProblemStatementUtils() {}

    public static String getDefaultText(String language) {
        String lang = language;
        if (!lang.equals("en-US") && !lang.equals("id-ID")) {
            lang = "en-US";
        }

        try (InputStream stream = ProgrammingProblemStatementUtils.class.getClassLoader().getResourceAsStream(
                    "judgels/sandalphon/problem/programming/statement/" + lang + ".html")) {
            return new String(stream.readAllBytes());
        } catch (IOException e) {
            return "";
        }
    }
}
