package judgels.service;

import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LAST_MODIFIED;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

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

    public static Response buildImageResponseFromText(String text, Date lastModifiedStream) {
        int fontSize = 13;
        int margin = 20;
        int charWidth = 8;
        int charHeight = 16;

        String[] textList = text.split("\\r?\\n");
        int maxDigitLineNumber = String.valueOf(textList.length).length();
        String lineNumTemplate = String.format(" %%%dd | ", maxDigitLineNumber);
        int prefixDigitCount = String.format(lineNumTemplate, 0).length();
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        int longestText = Arrays.asList(textList).stream().map(String::length).max(Integer::compareTo).get();
        int width = charWidth * longestText + 2 * margin;
        int height = charHeight * textList.length + 2 * margin;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        int nextLinePosition = margin;
        for (int i = 0; i < textList.length; i++) {
            String s = textList[i];
            g2d.setColor(Color.BLUE);
            g2d.drawString(String.format(lineNumTemplate, i + 1), 0, nextLinePosition);
            g2d.setColor(Color.BLACK);
            g2d.drawString(s.replaceAll("\t", "    "), prefixDigitCount * charWidth, nextLinePosition);
            nextLinePosition += charHeight;
        }
        g2d.dispose();

        Response.ResponseBuilder response = Response.ok();
        response.header(CACHE_CONTROL, "no-transform,public,max-age=300,s-maxage=900");
        response.header(LAST_MODIFIED, lastModifiedStream);

        return buildImageResponse(response, img, "jpg");
    }

    public static Response buildImageResponse(String imageUrl, Optional<String> ifModifiedSince) {
        try {
            new URL(imageUrl);
            return Response.temporaryRedirect(URI.create(imageUrl)).build();
        } catch (MalformedURLException e) {
            File imageFile = new File(imageUrl);
            if (!imageFile.exists()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return buildImageResponse(imageFile, ifModifiedSince);
        }
    }

    public static Response buildImageResponse(File imageFile, Optional<String> ifModifiedSince) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        if (ifModifiedSince.isPresent()) {
            try {
                Date lastModified = sdf.parse(ifModifiedSince.get());
                if (imageFile.lastModified() <= lastModified.getTime()) {
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
            BufferedImage img = ImageIO.read(imageFile);
            if (img == null) {
                response.header(CONTENT_TYPE, URLConnection.guessContentTypeFromName(imageFile.getName()));
                response.entity(Files.toByteArray(imageFile));
                return response.build();
            }

            String type = Files.getFileExtension(imageFile.getAbsolutePath());
            return buildImageResponse(response, img, type);
        } catch (IOException e2) {
            return Response.serverError().build();
        }
    }

    public static Response buildImageResponse(
            InputStream stream,
            String type,
            Date lastModifiedStream,
            Optional<String> ifModifiedSince) {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        if (ifModifiedSince.isPresent()) {
            try {
                Date lastModified = sdf.parse(ifModifiedSince.get());
                if (lastModifiedStream.getTime() <= lastModified.getTime()) {
                    return Response.notModified().build();
                }
            } catch (ParseException e2) {
                // nothing
            }
        }

        Response.ResponseBuilder response = Response.ok();
        response.header(CACHE_CONTROL, "no-transform,public,max-age=300,s-maxage=900");
        response.header(LAST_MODIFIED, lastModifiedStream);

        try {
            BufferedImage img = ImageIO.read(stream);
            if (img == null) {
                response.header(CONTENT_TYPE, "image/" + type);
                response.entity(ByteStreams.toByteArray(stream));
                return response.build();
            }

            return buildImageResponse(response, img, type);
        } catch (IOException e2) {
            return Response.serverError().build();
        }
    }

    private static Response buildImageResponse(Response.ResponseBuilder response, BufferedImage img, String type) {
        try {
            response.header(CONTENT_TYPE, "image/" + type);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, type, baos);
            response.entity(baos.toByteArray());

            return response.build();
        } catch (IOException e2) {
            return Response.serverError().build();
        }
    }
}
