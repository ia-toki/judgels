package judgels.michael.template;

import io.dropwizard.views.View;

public abstract class TemplateView extends View {
    private final HtmlTemplate template;

    public TemplateView(String templateName, HtmlTemplate template) {
        super(templateName);
        this.template = template;
    }

    public HtmlTemplate getVars() {
        return template;
    }
}
