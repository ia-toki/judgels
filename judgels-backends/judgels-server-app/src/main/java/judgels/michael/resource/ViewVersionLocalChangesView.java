package judgels.michael.resource;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ViewVersionLocalChangesView extends TemplateView {
    private final boolean isClean;

    public ViewVersionLocalChangesView(HtmlTemplate template, CommitVersionForm form, boolean isClean) {
        super("viewVersionLocalChangesView.ftl", template, form);
        this.isClean = isClean;
    }

    public boolean getIsClean() {
        return isClean;
    }
}
