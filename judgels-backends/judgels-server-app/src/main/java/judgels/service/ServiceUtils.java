package judgels.service;

import static jakarta.ws.rs.core.HttpHeaders.CACHE_CONTROL;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.HttpHeaders.LAST_MODIFIED;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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

    public static Response buildDownloadResponse(String fileUrl) {
        try {
            new URL(fileUrl);
            return Response.temporaryRedirect(URI.create(fileUrl)).build();
        } catch (MalformedURLException e) {
            File file = new File(fileUrl);
            if (!file.exists()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(file)
                    .header(CONTENT_TYPE, "application/x-download")
                    .header(CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                    .build();
        }
    }

    public static Response buildMediaResponse(String imageUrl, Optional<String> ifModifiedSince) {
        try {
            new URL(imageUrl);
            return Response.temporaryRedirect(URI.create(imageUrl)).build();
        } catch (MalformedURLException e) {
            File imageFile = new File(imageUrl);
            if (!imageFile.exists()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return buildMediaResponse(imageFile, ifModifiedSince);
        }
    }

    public static Response buildMediaResponse(File imageFile, Optional<String> ifModifiedSince) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        if (ifModifiedSince.isPresent()) {
            try {
                Date lastModified = sdf.parse(ifModifiedSince.get());
                if (imageFile.lastModified() - lastModified.getTime() < 1000) {
                    return Response.notModified().build();
                }
            } catch (ParseException e2) {
                // nothing
            }
        }

        Response.ResponseBuilder response = Response.ok();
        response.header(CACHE_CONTROL, "no-transform,public,max-age=300,s-maxage=900");
        response.header(LAST_MODIFIED, sdf.format(new Date(imageFile.lastModified())));

        try {
            String type = Files.probeContentType(imageFile.toPath());
            return buildMediaResponse(response, new FileInputStream(imageFile), type);
        } catch (IOException e2) {
            return Response.serverError().build();
        }
    }

    public static Response buildMediaResponse(
            InputStream stream,
            String type,
            Date lastModifiedStream,
            Optional<String> ifModifiedSince) {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        if (ifModifiedSince.isPresent()) {
            try {
                Date lastModified = sdf.parse(ifModifiedSince.get());
                if (lastModifiedStream.getTime() - lastModified.getTime() < 1000) {
                    return Response.notModified().build();
                }
            } catch (ParseException e2) {
                // nothing
            }
        }

        Response.ResponseBuilder response = Response.ok();
        response.header(CACHE_CONTROL, "no-transform,public,max-age=300,s-maxage=900");
        response.header(LAST_MODIFIED, lastModifiedStream);

        return buildMediaResponse(response, stream, type);
    }

    private static Response buildMediaResponse(Response.ResponseBuilder response, InputStream stream, String type) {
        response.header(CONTENT_TYPE, type);
        response.entity(stream);
        return response.build();
    }
}
