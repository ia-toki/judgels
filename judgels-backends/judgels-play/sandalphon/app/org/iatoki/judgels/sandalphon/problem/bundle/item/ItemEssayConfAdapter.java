package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemEssayConfView;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

public final class ItemEssayConfAdapter implements BundleItemConfAdapter {
    @Override
    public Form generateForm() {
        return Form.form(ItemEssayConfForm.class);
    }

    @Override
    public Form generateForm(String conf, String meta) {
        ItemEssayConf itemConf = new Gson().fromJson(conf, ItemEssayConf.class);
        ItemEssayConfForm itemForm = new ItemEssayConfForm();
        itemForm.statement = itemConf.statement;
        itemForm.meta = meta;
        if (itemConf.score != null) {
            itemForm.score = itemConf.score;
        }

        return Form.form(ItemEssayConfForm.class).fill(itemForm);
    }

    @Override
    public Html getConfHtml(Form form, Call target, String submitLabel) {
        return itemEssayConfView.render(form, target, submitLabel);
    }

    @Override
    public Form bindFormFromRequest(Http.Request request) {
        return Form.form(ItemEssayConfForm.class).bindFromRequest();
    }

    @Override
    public String getMetaFromForm(Form form) {
        Form<ItemEssayConfForm> realForm = (Form<ItemEssayConfForm>) form;
        ItemEssayConfForm itemForm = realForm.get();
        return itemForm.meta;
    }

    @Override
    public String processRequestForm(Form form) {
        Form<ItemEssayConfForm> realForm = (Form<ItemEssayConfForm>) form;
        ItemEssayConfForm itemForm = realForm.get();
        ItemEssayConf itemConf = new ItemEssayConf();
        itemConf.statement = itemForm.statement;
        itemConf.score = itemForm.score;

        return new Gson().toJson(itemConf);
    }

    @Override
    public BundleItemConf parseConfString(String conf) {
        return new Gson().fromJson(conf, ItemEssayConf.class);
    }
}
