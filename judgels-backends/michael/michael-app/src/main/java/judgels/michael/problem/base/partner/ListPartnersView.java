package judgels.michael.problem.base.partner;

import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.partner.ProblemPartnerV2;

public class ListPartnersView extends TemplateView {
    private final List<ProblemPartnerV2> partners;
    private final Map<String, Profile> profilesMap;

    public ListPartnersView(HtmlTemplate template, List<ProblemPartnerV2> partners, Map<String, Profile> profilesMap) {
        super("listPartnersView.ftl", template);
        this.partners = partners;
        this.profilesMap = profilesMap;
    }

    public List<ProblemPartnerV2> getPartners() {
        return partners;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }
}
