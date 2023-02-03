package judgels.jerahmeel.submission;

public class SubmissionUtils {
    private SubmissionUtils() {}

    public static boolean isProblemSet(String jid) {
        return jid.startsWith("JIDPRSE");
    }

    public static boolean isChapter(String jid) {
        return jid.startsWith("JIDSESS");
    }
}
