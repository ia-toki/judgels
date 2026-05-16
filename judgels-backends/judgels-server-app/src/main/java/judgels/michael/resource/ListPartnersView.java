package judgels.michael.resource;

import java.util.List;
import java.util.Map;
import judgels.api.profile.Profile;
import judgels.api.resource.Partner;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ListPartnersView extends TemplateView {
    private final List<Partner> partners;
    private final Map<String, Profile> profilesMap;

    public ListPartnersView(HtmlTemplate template, List<Partner> partners, Map<String, Profile> profilesMap) {
        super("listPartnersView.ftl", template);
        this.partners = partners;
        this.profilesMap = profilesMap;
    }

    public List<Partner> getPartners() {
        return partners;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }
}
