package org.iatoki.judgels.sandalphon.problem.bundle.item.statement;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.sandalphon.api.problem.bundle.StatementItemConfig;
import org.iatoki.judgels.sandalphon.problem.bundle.item.ItemConfigAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.statement.html.statementItemConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.twirl.api.Html;

public final class StatementItemConfigAdapter implements ItemConfigAdapter {
    private final ObjectMapper mapper;

    public StatementItemConfigAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Form<?> generateForm(FormFactory formFactory) {
        return formFactory.form(StatementItemConfigForm.class);
    }

    @Override
    public Form<?> generateForm(FormFactory formFactory, String conf, String meta) {
        StatementItemConfig itemConf = parseConfString(conf);
        StatementItemConfigForm itemForm = new StatementItemConfigForm();
        itemForm.statement = itemConf.getStatement();
        itemForm.meta = meta;

        return formFactory.form(StatementItemConfigForm.class).fill(itemForm);
    }

    @Override
    public Html getConfHtml(Form<?> form, Call target, String submitLabel) {
        return statementItemConfigView.render((Form<StatementItemConfigForm>) form, target, submitLabel);
    }

    @Override
    public Form<?> bindFormFromRequest(FormFactory formFactory, Http.Request req) {
        return formFactory.form(StatementItemConfigForm.class).bindFromRequest(req);
    }

    @Override
    public String getMetaFromForm(Form<?> form) {
        StatementItemConfigForm itemForm = ((Form<StatementItemConfigForm>) form).get();

        return itemForm.meta;
    }

    @Override
    public String processRequestForm(Form form) {
        StatementItemConfigForm itemForm = ((Form<StatementItemConfigForm>) form).get();
        StatementItemConfig itemConf = new StatementItemConfig.Builder()
                .statement(itemForm.statement)
                .build();

        try {
            return mapper.writeValueAsString(itemConf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StatementItemConfig parseConfString(String conf) {
        try {
            return mapper.readValue(conf, StatementItemConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
