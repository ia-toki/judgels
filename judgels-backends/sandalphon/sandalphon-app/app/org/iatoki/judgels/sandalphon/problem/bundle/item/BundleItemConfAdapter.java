package org.iatoki.judgels.sandalphon.problem.bundle.item;

import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.twirl.api.Html;

public interface BundleItemConfAdapter {

    Form generateForm(FormFactory formFactory);

    Form generateForm(FormFactory formFactory, String conf, String meta);

    Html getConfHtml(Form form, Call target, String submitLabel);

    Form bindFormFromRequest(FormFactory formFactory, Http.Request request);

    String getMetaFromForm(Form form);

    String processRequestForm(Form form);

    BundleItemConf parseConfString(String conf);
}
