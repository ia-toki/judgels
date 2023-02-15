package judgels.service;

import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LAST_MODIFIED;

import com.google.common.io.Files;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public static Response buildLightImageResponseFromText(String text, Date lastModifiedStream) {
        return buildImageResponseFromText(
                text,
                lastModifiedStream,
                new Color(24, 32, 38),
                Color.WHITE,
                new Color(240, 240, 240));
    }

    public static Response buildDarkImageResponseFromText(String text, Date lastModifiedStream) {
        return buildImageResponseFromText(
                text,
                lastModifiedStream,
                new Color(204, 204, 204),
                new Color(48, 64, 77),
                new Color(57, 75, 89));
    }

    public static Response buildImageResponseFromText(
            String text, Date lastModifiedStream,
            Color textColor,
            Color backgroundColor,
            Color lineNumberBackgroundColor) {

        int fontSize = 13;
        int margin = 20;
        int charWidth = 8;
        int charHeight = 16;

        List<String> textList = Arrays.asList(text.split("\\r?\\n"))
                .stream()
                .map(s -> " " + s.replaceAll("\t", "    "))
                .collect(Collectors.toList());
        int maxDigitLineNumber = String.valueOf(textList.size()).length();
        String lineNumTemplate = String.format(" %%%dd ", maxDigitLineNumber);
        int prefixCharCount = String.format(lineNumTemplate, 0).length();
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        int longestText = textList.stream().map(String::length).max(Integer::compareTo).get();
        int width = charWidth * (prefixCharCount + longestText) + 2 * margin;
        int height = charHeight * textList.size() + margin;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(font);
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.setColor(lineNumberBackgroundColor);
        g2d.fillRect(0, 0, prefixCharCount * charWidth, img.getHeight());

        int nextLinePosition = margin;

        g2d.setColor(textColor);
        for (int i = 0; i < textList.size(); i++) {
            String s = textList.get(i);
            g2d.drawString(String.format(lineNumTemplate, i + 1), 0, nextLinePosition);
            g2d.drawString(s, prefixCharCount * charWidth, nextLinePosition);
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
            String type = Files.getFileExtension(imageFile.getAbsolutePath());
            return buildImageResponse(response, new FileInputStream(imageFile), type);
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

        return buildImageResponse(response, stream, type);
    }

    private static Response buildImageResponse(Response.ResponseBuilder response, BufferedImage img, String type) {
        response.header(CONTENT_TYPE, "image/" + type);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, type, baos);
            response.entity(baos.toByteArray());
        } catch (IOException e) {
            return Response.serverError().build();
        }

        return response.build();
    }

    private static Response buildImageResponse(Response.ResponseBuilder response, InputStream stream, String type) {
        response.header(CONTENT_TYPE, "image/" + type);
        response.entity(stream);
        return response.build();
    }
}
