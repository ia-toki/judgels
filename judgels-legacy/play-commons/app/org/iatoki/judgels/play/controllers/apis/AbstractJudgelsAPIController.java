package org.iatoki.judgels.play.controllers.apis;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.iatoki.judgels.play.api.JudgelsAPIBadRequestException;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.api.JudgelsAPIUnauthorizedException;
import org.iatoki.judgels.play.api.JudgelsAppClient;
import org.iatoki.judgels.play.api.JudgelsAppClientAPIIdentity;
import org.iatoki.judgels.play.api.JudgelsAppClientService;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@JudgelsAPIGuard
public abstract class AbstractJudgelsAPIController extends AbstractJudgelsController {

    protected static JudgelsAppClientAPIIdentity authenticateAsJudgelsAppClient(JudgelsAppClientService clientService) {
        if (!request().hasHeader("Authorization")) {
            throw new JudgelsAPIUnauthorizedException("Basic authentication required.");
        }

        String[] authorization = request().getHeader("Authorization").split(" ");

        if (authorization.length != 2) {
            throw new JudgelsAPIUnauthorizedException("Basic authentication required.");
        }

        String method = authorization[0];
        String credentialsString = authorization[1];

        if (!"Basic".equals(method)) {
            throw new JudgelsAPIUnauthorizedException("Basic authentication required.");
        }

        String decodedCredentialsString = new String(Base64.decodeBase64(credentialsString));
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(decodedCredentialsString);

        String clientJid = credentials.getUserName();
        String clientSecret = credentials.getPassword();

        if (!clientService.clientExistsByJid(clientJid)) {
            throw new JudgelsAPIUnauthorizedException("Bad credentials.");
        }

        JudgelsAppClient client = clientService.findClientByJid(clientJid);

        if (!client.getSecret().equals(clientSecret)) {
            throw new JudgelsAPIUnauthorizedException("Bad credentials.");
        }

        return new JudgelsAppClientAPIIdentity(client.getJid(), client.getName());
    }

    protected static <T> T parseRequestBody(Type type) {
        try {
            return new Gson().fromJson(request().body().asText(), type);
        } catch (JsonSyntaxException e) {
            throw new JudgelsAPIBadRequestException("Bad JSON request body.");
        }
    }

    protected static <T> T parseRequestBodyAsUrlFormEncoded(Class<T> clazz) {
        Form<T> form = Form.form(clazz).bindFromRequest();
        return form.get();
    }

    protected static Result okAsJson(Object responseBody) {
        String finalResponseBody;
        if (responseBody instanceof JsonObject) {
            finalResponseBody = responseBody.toString();
        } else {
            finalResponseBody = new Gson().toJson(responseBody);
        }

        DynamicForm dForm = DynamicForm.form().bindFromRequest();
        String callback = dForm.get("callback");

        if (callback != null) {
            response().setContentType("application/javascript");
            return ok(callback + "(" + finalResponseBody + ");");
        } else {
            response().setContentType("application/json");
            return ok(finalResponseBody);
        }
    }

    protected static Result okAsImage(String imageUrl) {
        try {
            new URL(imageUrl);
            return temporaryRedirect(imageUrl);
        } catch (MalformedURLException e) {
            File imageFile = new File(imageUrl);
            if (!imageFile.exists()) {
                throw new JudgelsAPINotFoundException();
            }

            response().setHeader("Cache-Control", "no-transform,public,max-age=300,s-maxage=900");

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            response().setHeader("Last-Modified", sdf.format(new Date(imageFile.lastModified())));

            boolean modified = true;

            if (request().hasHeader("If-Modified-Since")) {
                try {
                    Date lastUpdate = sdf.parse(request().getHeader("If-Modified-Since"));
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

                if (in == null) {
                    baos.write(Files.toByteArray(imageFile));
                    response().setContentType(URLConnection.guessContentTypeFromName(imageFile.getName()));
                    return ok(baos.toByteArray());
                }

                String type = FilenameUtils.getExtension(imageFile.getAbsolutePath());

                ImageIO.write(in, type, baos);

                response().setContentType("image/" + type);
                return ok(baos.toByteArray());
            } catch (IOException e2) {
                throw new JudgelsAPIInternalServerErrorException(e2);
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
                throw new JudgelsAPINotFoundException();
            }

            response().setContentType("application/x-download");
            response().setHeader("Content-disposition", "attachment; filename=" + resource.getName());

            return ok(resource);
        }
    }

    protected static void setAccessControlOrigin(String domains, String methods, long maxAge) {
        response().setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, domains);
        response().setHeader(ACCESS_CONTROL_ALLOW_METHODS, methods);
        response().setHeader(ACCESS_CONTROL_MAX_AGE, maxAge + "");
        response().setHeader(ACCESS_CONTROL_ALLOW_HEADERS, StringUtils.join(new String[] {ORIGIN, X_REQUESTED_WITH, CONTENT_TYPE, ACCEPT, AUTHORIZATION}, ','));
    }

    protected static String createJsonPResponse(String callback, String json) {
        StringBuilder sb = new StringBuilder(callback);
        sb.append("(").append(json).append(")");
        return sb.toString();
    }
}
