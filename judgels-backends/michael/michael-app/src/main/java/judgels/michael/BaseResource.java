package judgels.michael;

import judgels.michael.template.HtmlTemplate;

public abstract class BaseResource {
    private final MichaelConfiguration config;

    public BaseResource(MichaelConfiguration config) {
        this.config = config;
    }

    public HtmlTemplate newTemplate() {
        return new HtmlTemplate(config.getName());
    }
}
