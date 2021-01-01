package org.iatoki.judgels.play.controllers.apis;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.client.ClientChecker;
import org.apache.commons.io.FilenameUtils;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

public abstract class AbstractJudgelsAPIController extends Controller {
    @Inject
    protected FormFactory formFactory;

    protected static void authenticateAsJudgelsAppClient(ClientChecker clientChecker) {
        Optional<String> authHeaderString = request().getHeaders().get("Authorization");
        BasicAuthHeader authHeader = authHeaderString.isPresent() ? null : BasicAuthHeader.valueOf(authHeaderString.get());
        clientChecker.check(authHeader);
    }

    protected Result okAsJson(Object responseBody) {
        String finalResponseBody;
        if (responseBody instanceof JsonObject) {
            finalResponseBody = responseBody.toString();
        } else {
            finalResponseBody = new Gson().toJson(responseBody);
        }

        DynamicForm dForm = formFactory.form().bindFromRequest();
        String callback = dForm.get("callback");

        if (callback != null) {
            return ok(callback + "(" + finalResponseBody + ");")
                    .as("application/javascript");
        } else {
            return ok(finalResponseBody)
                    .as("application/json");
        }
    }

    protected static Result okAsImage(String imageUrl) {
        try {
            new URL(imageUrl);
            return temporaryRedirect(imageUrl);
        } catch (MalformedURLException e) {
            File imageFile = new File(imageUrl);
            if (!imageFile.exists()) {
                return Results.notFound();
            }

            response().setHeader("Cache-Control", "no-transform,public,max-age=300,s-maxage=900");

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            response().setHeader("Last-Modified", sdf.format(new Date(imageFile.lastModified())));

            boolean modified = true;

            if (request().getHeaders().get("If-Modified-Since").isPresent()) {
                try {
                    Date lastUpdate = sdf.parse(request().getHeaders().get("If-Modified-Since").get());
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

                if (in == null) {
                    return ok(baos.toByteArray()).as(URLConnection.guessContentTypeFromName(imageFile.getName()));
                }

                String type = FilenameUtils.getExtension(imageFile.getAbsolutePath());

                return ok(baos.toByteArray()).as("image/" + type);
            } catch (IOException e2) {
                return Results.internalServerError(e2.getMessage());
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
                return Results.notFound();
            }

            response().setHeader("Content-disposition", "attachment; filename=" + resource.getName());

            return ok(resource).as("application/x-download");
        }
    }
}
