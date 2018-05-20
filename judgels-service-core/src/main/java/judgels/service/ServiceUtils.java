package judgels.service;

import java.util.Optional;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

public class ServiceUtils {
    private ServiceUtils() {}

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
