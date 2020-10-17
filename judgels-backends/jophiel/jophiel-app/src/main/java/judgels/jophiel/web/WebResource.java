package judgels.jophiel.web;

import javax.inject.Inject;
import judgels.jophiel.api.web.WebConfig;
import judgels.jophiel.api.web.WebService;

public class WebResource implements WebService {
    private final WebConfiguration webConfig;

    @Inject
    public WebResource(WebConfiguration webConfig) {
        this.webConfig = webConfig;
    }

    @Override
    public WebConfig getWebConfig() {
        return new WebConfig.Builder()
                .announcements(webConfig.getAnnouncements())
                .build();
    }
}
