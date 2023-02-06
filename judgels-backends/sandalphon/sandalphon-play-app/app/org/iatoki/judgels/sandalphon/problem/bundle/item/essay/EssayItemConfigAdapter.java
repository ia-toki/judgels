package org.iatoki.judgels.sandalphon.problem.bundle.item.essay;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.sandalphon.api.problem.bundle.EssayItemConfig;
import org.iatoki.judgels.sandalphon.problem.bundle.item.ItemConfigAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.essay.html.essayItemConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.twirl.api.Html;

public final class EssayItemConfigAdapter implements ItemConfigAdapter {
    private final ObjectMapper mapper;

    public EssayItemConfigAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Form<?> generateForm(FormFactory formFactory) {
        return formFactory.form(EssayItemConfigForm.class);
    }

    @Override
    public Form<?> generateForm(FormFactory formFactory, String conf, String meta) {
        EssayItemConfig itemConf = parseConfString(conf);
        EssayItemConfigForm itemForm = new EssayItemConfigForm();
        itemForm.statement = itemConf.getStatement();
        itemForm.meta = meta;
        itemForm.score = itemConf.getScore();

        return formFactory.form(EssayItemConfigForm.class).fill(itemForm);
    }

    @Override
    public Html getConfHtml(Form<?> form, Call target, String submitLabel) {
        return essayItemConfigView.render((Form<EssayItemConfigForm>) form, target, submitLabel);
    }

    @Override
    public Form<?> bindFormFromRequest(FormFactory formFactory, Http.Request request) {
        return formFactory.form(EssayItemConfigForm.class).bindFromRequest(request);
    }

    @Override
    public String getMetaFromForm(Form<?> form) {
        EssayItemConfigForm itemForm = ((Form<EssayItemConfigForm>) form).get();
        return itemForm.meta;
    }

    @Override
    public String processRequestForm(Form<?> form) {
        EssayItemConfigForm itemForm = ((Form<EssayItemConfigForm>) form).get();

        EssayItemConfig itemConf = new EssayItemConfig.Builder()
                .statement(itemForm.statement)
                .score(itemForm.score)
                .build();

        try {
            return mapper.writeValueAsString(itemConf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EssayItemConfig parseConfString(String conf) {
        try {
            return mapper.readValue(conf, EssayItemConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
