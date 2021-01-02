package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemShortAnswerConfView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.twirl.api.Html;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class ItemShortAnswerConfAdapter implements BundleItemConfAdapter {
    @Override
    public Form generateForm(FormFactory formFactory) {
        return formFactory.form(ItemShortAnswerConfForm.class);
    }

    @Override
    public Form generateForm(FormFactory formFactory, String conf, String meta) {
        ItemShortAnswerConf itemConf = new Gson().fromJson(conf, ItemShortAnswerConf.class);
        ItemShortAnswerConfForm itemForm = new ItemShortAnswerConfForm();
        itemForm.statement = itemConf.statement;
        itemForm.meta = meta;
        if (itemConf.score != null) {
            itemForm.score = itemConf.score;
        }
        if (itemConf.penalty != null) {
            itemForm.penalty = itemConf.penalty;
        }
        itemForm.inputValidationRegex = itemConf.inputValidationRegex;
        itemForm.gradingRegex = itemConf.gradingRegex;

        return formFactory.form(ItemShortAnswerConfForm.class).fill(itemForm);
    }

    @Override
    public Html getConfHtml(Form form, Call target, String submitLabel) {
        return itemShortAnswerConfView.render(form, target, submitLabel);
    }

    @Override
    public Form bindFormFromRequest(FormFactory formFactory, Http.Request request) {
        Form form = formFactory.form(ItemShortAnswerConfForm.class).bindFromRequest();
        if (!(form.hasErrors() || form.hasGlobalErrors())) {
            ItemShortAnswerConfForm confForm = ((Form<ItemShortAnswerConfForm>) form).get();
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
    public String getMetaFromForm(Form form) {
        Form<ItemShortAnswerConfForm> realForm = (Form<ItemShortAnswerConfForm>) form;
        ItemShortAnswerConfForm itemForm = realForm.get();
        return itemForm.meta;
    }

    @Override
    public String processRequestForm(Form form) {
        Form<ItemShortAnswerConfForm> realForm = (Form<ItemShortAnswerConfForm>) form;
        ItemShortAnswerConfForm itemForm = realForm.get();
        ItemShortAnswerConf itemConf = new ItemShortAnswerConf();
        itemConf.statement = itemForm.statement;
        itemConf.score = itemForm.score;
        itemConf.penalty = itemForm.penalty;
        itemConf.inputValidationRegex = itemForm.inputValidationRegex;
        itemConf.gradingRegex = itemForm.gradingRegex;

        return new Gson().toJson(itemConf);
    }

    @Override
    public BundleItemConf parseConfString(String conf) {
        return new Gson().fromJson(conf, ItemShortAnswerConf.class);
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
