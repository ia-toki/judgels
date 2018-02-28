package judgels.uriel.contest;

import javax.ws.rs.ForbiddenException;
import judgels.uriel.api.contest.Contest;

/**
 * @deprecated This is a temporary workaround until role authorization is implemented.
 */
@Deprecated
public class ContestHacks {
    private ContestHacks() {}

    public static final String ALLOWED_CONTEST_NAME = "TOKI Open Contest";
    public static final String ALLOWED_CONTEST_NAME_EXCEPTION = "Testing";

    public static Contest checkAllowed(Contest contest) {
        String name = contest.getName();
        boolean allowed = name.contains(ALLOWED_CONTEST_NAME) && !name.contains(ALLOWED_CONTEST_NAME_EXCEPTION);
        if (!allowed) {
            throw new ForbiddenException();
        }
        return contest;
    }
}
