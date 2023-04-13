package judgels.michael.resource;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class RebaseVersionLocalChangesView extends TemplateView {
    private final String localChangesError;

    public RebaseVersionLocalChangesView(HtmlTemplate template, String localChangesError) {
        super("rebaseVersionLocalChangesView.ftl", template);
        this.localChangesError = localChangesError;
    }

    public String getLocalChangesError() {
        return localChangesError;
    }
}
