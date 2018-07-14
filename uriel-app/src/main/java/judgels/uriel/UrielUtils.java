package judgels.uriel;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class UrielUtils {
    private UrielUtils() {}

    public static FormDataBodyPart checkPartExists(FormDataMultiPart multipart, String field) {
        FormDataBodyPart part = multipart.getField(field);
        if (part == null) {
            throw new IllegalArgumentException(field + " is missing");
        }
        return part;
    }
}
