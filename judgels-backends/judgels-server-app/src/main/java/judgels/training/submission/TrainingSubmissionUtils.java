package judgels.training.submission;

public class TrainingSubmissionUtils {
    private TrainingSubmissionUtils() {}

    public static boolean isProblemSet(String jid) {
        return jid.startsWith("JIDPRSE");
    }

    public static boolean isChapter(String jid) {
        return jid.startsWith("JIDSESS");
    }
}
