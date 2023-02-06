package org.iatoki.judgels.sandalphon.problem.bundle.item.shortanswer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import judgels.sandalphon.api.problem.bundle.ShortAnswerItemConfig;
import org.iatoki.judgels.sandalphon.problem.bundle.item.ItemConfigAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.shortanswer.html.shortAnswerItemConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.twirl.api.Html;

public final class ShortAnswerItemConfigAdapter implements ItemConfigAdapter {
    private final ObjectMapper mapper;

    public ShortAnswerItemConfigAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Form<?> generateForm(FormFactory formFactory) {
        return formFactory.form(ShortAnswerItemConfigForm.class);
    }

    @Override
    public Form generateForm(FormFactory formFactory, String conf, String meta) {
        ShortAnswerItemConfig itemConf = parseConfString(conf);
        ShortAnswerItemConfigForm itemForm = new ShortAnswerItemConfigForm();
        itemForm.statement = itemConf.getStatement();
        itemForm.meta = meta;
        itemForm.score = itemConf.getScore();
        itemForm.penalty = itemConf.getPenalty();
        itemForm.inputValidationRegex = itemConf.getInputValidationRegex();
        itemForm.gradingRegex = itemConf.getGradingRegex().orElse(null);

        return formFactory.form(ShortAnswerItemConfigForm.class).fill(itemForm);
    }

    @Override
    public Html getConfHtml(Form<?> form, Call target, String submitLabel) {
        return shortAnswerItemConfigView.render((Form<ShortAnswerItemConfigForm>) form, target, submitLabel);
    }

    @Override
    public Form<?> bindFormFromRequest(FormFactory formFactory, Http.Request req) {
        Form<ShortAnswerItemConfigForm> form = formFactory.form(ShortAnswerItemConfigForm.class).bindFromRequest(req);
        if (!(form.hasErrors() || form.hasGlobalErrors())) {
            ShortAnswerItemConfigForm confForm = form.get();
            if (!isRegexValid(confForm.inputValidationRegex)) {
                form = form.withGlobalError("The input validation regex is invalid");
            }
            if (!confForm.gradingRegex.isEmpty() && !isRegexValid(confForm.gradingRegex)) {
                form = form.withGlobalError("The grading regex is invalid.");
            }
        }
        return form;
    }

    @Override
    public String getMetaFromForm(Form<?> form) {
        ShortAnswerItemConfigForm itemForm = ((Form<ShortAnswerItemConfigForm>) form).get();
        return itemForm.meta;
    }

    @Override
    public String processRequestForm(Form<?> form) {
        ShortAnswerItemConfigForm itemForm = ((Form<ShortAnswerItemConfigForm>) form).get();
        ShortAnswerItemConfig itemConf = new ShortAnswerItemConfig.Builder()
                .statement(itemForm.statement)
                .score(itemForm.score)
                .penalty(itemForm.penalty)
                .inputValidationRegex(itemForm.inputValidationRegex)
                .gradingRegex(itemForm.gradingRegex)
                .build();

        try {
            return mapper.writeValueAsString(itemConf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShortAnswerItemConfig parseConfString(String conf) {
        try {
            return mapper.readValue(conf, ShortAnswerItemConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isRegexValid(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
