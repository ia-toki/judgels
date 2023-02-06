package org.iatoki.judgels.play.controllers.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public abstract class AbstractJudgelsAPIController extends Controller {
    @Inject
    protected FormFactory formFactory;

    private final ObjectMapper mapper;

    protected AbstractJudgelsAPIController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    protected Result okAsJson(Http.Request req, Object responseBody) {
        String finalResponseBody;
        try {
            finalResponseBody = mapper.writeValueAsString(responseBody);
        } catch (IOException e) {
            return internalServerError(e.getMessage());
        }

        DynamicForm dForm = formFactory.form().bindFromRequest(req);
        String callback = dForm.get("callback");

        if (callback != null) {
            return ok(callback + "(" + finalResponseBody + ");")
                    .as("application/javascript");
        } else {
            return ok(finalResponseBody)
                    .as("application/json");
        }
    }

    protected static Result okAsImage(Http.Request req, String imageUrl) {
        try {
            new URL(imageUrl);
            return temporaryRedirect(imageUrl);
        } catch (MalformedURLException e) {
            File imageFile = new File(imageUrl);
            if (!imageFile.exists()) {
                return notFound();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            boolean modified = true;

            if (req.getHeaders().get("If-Modified-Since").isPresent()) {
                try {
                    Date lastUpdate = sdf.parse(req.getHeaders().get("If-Modified-Since").get());
                    if (imageFile.lastModified() <= lastUpdate.getTime()) {
                        modified = false;
                    }
                } catch (ParseException e2) {
                    // nothing
                }
            }

            if (!modified) {
                return status(304);
            }

            try {
                BufferedImage in = ImageIO.read(imageFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(Files.toByteArray(imageFile));

                String contentType;
                if (in == null) {
                    contentType = URLConnection.guessContentTypeFromName(imageFile.getName());
                } else {
                    contentType = "image/" + FilenameUtils.getExtension(imageFile.getAbsolutePath());
                }

                return ok(baos.toByteArray())
                        .as(contentType)
                        .withHeader("Cache-Control", "no-transform,public,max-age=300,s-maxage=900")
                        .withHeader("Last-Modified", sdf.format(new Date(imageFile.lastModified())));
            } catch (IOException e2) {
                return internalServerError(e2.getMessage());
            }
        }
    }

    protected static Result okAsDownload(String resourceUrl) {
        try {
            new URL(resourceUrl);
            return redirect(resourceUrl);
        } catch (MalformedURLException e) {
            File resource = new File(resourceUrl);
            if (!resource.exists()) {
                return notFound();
            }

            return ok(resource)
                    .as("application/x-download")
                    .withHeader("Content-disposition", "attachment; filename=" + resource.getName());
        }
    }
}
