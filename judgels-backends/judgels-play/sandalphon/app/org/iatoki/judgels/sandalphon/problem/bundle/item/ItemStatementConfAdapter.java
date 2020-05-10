package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.gson.Gson;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.itemStatementConfView;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

public final class ItemStatementConfAdapter implements BundleItemConfAdapter {

    @Override
    public Form generateForm() {
        return Form.form(ItemStatementConfForm.class);
    }

    @Override
    public Form generateForm(String conf, String meta) {
        ItemStatementConf itemConf = new Gson().fromJson(conf, ItemStatementConf.class);
        ItemStatementConfForm itemForm = new ItemStatementConfForm();
        itemForm.statement = itemConf.statement;
        itemForm.meta = meta;

        return Form.form(ItemStatementConfForm.class).fill(itemForm);
    }

    @Override
    public Html getConfHtml(Form form, Call target, String submitLabel) {
        return itemStatementConfView.render(form, target, submitLabel);
    }

    @Override
    public Form bindFormFromRequest(Http.Request request) {
        return Form.form(ItemStatementConfForm.class).bindFromRequest();
    }

    @Override
    public String getMetaFromForm(Form form) {
        Form<ItemStatementConfForm> realForm = (Form<ItemStatementConfForm>) form;
        ItemStatementConfForm itemForm = realForm.get();

        return itemForm.meta;
    }

    @Override
    public String processRequestForm(Form form) {
        Form<ItemStatementConfForm> realForm = (Form<ItemStatementConfForm>) form;
        ItemStatementConfForm itemForm = realForm.get();
        ItemStatementConf itemConf = new ItemStatementConf();
        itemConf.statement = itemForm.statement;

        return new Gson().toJson(itemConf);
    }

    @Override
    public BundleItemConf parseConfString(String conf) {
        return new Gson().fromJson(conf, ItemStatementConf.class);
    }
}
