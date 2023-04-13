package judgels.michael.resource;

import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.resource.Partner;

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
