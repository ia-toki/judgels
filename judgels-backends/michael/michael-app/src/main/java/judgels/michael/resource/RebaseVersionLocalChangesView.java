package judgels.michael.resource;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class RebaseVersionLocalChangesView extends TemplateView {
    private final String localChangesError;
    private final String backUrl;

    public RebaseVersionLocalChangesView(HtmlTemplate template, String localChangesError, String backUrl) {
        super("rebaseVersionLocalChangesView.ftl", template);
        this.localChangesError = localChangesError;
        this.backUrl = backUrl;
    }

    public String getLocalChangesError() {
        return localChangesError;
    }

    public String getBackUrl() {
        return backUrl;
    }
}
