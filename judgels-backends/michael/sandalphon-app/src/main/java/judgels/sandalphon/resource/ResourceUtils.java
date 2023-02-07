package judgels.sandalphon.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.FilenameUtils;

public class ResourceUtils {
    private ResourceUtils() {}

    public static Response okAsImage(Optional<String> ifModifiedSince, String imageUrl) {
        File imageFile = new File(imageUrl);
        if (!imageFile.exists()) {
            return Response.status(Status.NOT_FOUND).build();
        }

        SimpleDateFormat ifModifiedSinceFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        if (ifModifiedSince.isPresent()) {
            try {
                Date lastUpdate = ifModifiedSinceFormat.parse(ifModifiedSince.get());
                if (imageFile.lastModified() - lastUpdate.getTime() < 1000) {
                    return Response.notModified().build();
                }
            } catch (ParseException e2) {
                // nothing
            }
        }

        try {
            return Response.ok(new FileInputStream(imageFile))
                    .type("image/" + FilenameUtils.getExtension(imageFile.getAbsolutePath()))
                    .header("Cache-Control", "no-transform,public,max-age=300,s-maxage=900")
                    .header("Last-Modified", ifModifiedSinceFormat.format(new Date(imageFile.lastModified())))
                    .build();
        } catch (FileNotFoundException e2) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
