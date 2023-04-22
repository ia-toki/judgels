package org.iatoki.judgels.play.controllers.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.inject.Inject;
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
}
