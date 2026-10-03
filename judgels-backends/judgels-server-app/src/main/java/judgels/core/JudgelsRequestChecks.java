package judgels.core;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.util.Optional;

public class JudgelsRequestChecks {
    private JudgelsRequestChecks() {}

    public static <T> T checkFound(Optional<T> obj) {
        return obj.orElseThrow(NotFoundException::new);
    }

    public static void checkAllowed(boolean allowed) {
        if (!allowed) {
            throw new ForbiddenException();
        }
    }

    public static void checkAllowed(Optional<String> reasonNotAllowed) {
        if (reasonNotAllowed.isPresent()) {
            throw new ForbiddenException(reasonNotAllowed.get());
        }
    }
}
