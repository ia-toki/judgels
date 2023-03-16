package judgels.michael.resource;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserStore;
import judgels.sandalphon.api.resource.Partner;
import judgels.sandalphon.api.resource.PartnerPermission;

public class PartnerUtils {
    private PartnerUtils() {}

    public static String partnersToCsv(List<Partner> partners, Map<String, Profile> profilesMap) {
        return partners
                .stream()
                .map(partner -> profilesMap.get(partner.getUserJid()).getUsername() + "," + partner.getPermission())
                .collect(joining("\r\n"));
    }

    public static Optional<List<Partner>> csvToPartners(String csv, UserStore userStore) {
        List<String> usernames = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        for (String line : csv.replaceAll("\r", "").split("\n")) {
            String[] tokens = line.split(",");
            if (tokens.length == 0) {
                continue;
            }

            String username = tokens[0].trim();
            if (username.isEmpty()) {
                continue;
            }

            String permission = "UPDATE";
            if (tokens.length == 2) {
                permission = tokens[1].trim();
            } else if (tokens.length > 2) {
                return Optional.empty();
            }

            usernames.add(username);
            permissions.add(permission);
        }

        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(new HashSet<>(usernames));

        List<Partner> partners = new ArrayList<>();
        for (int i = 0; i < usernames.size(); i++) {
            String username = usernames.get(i);
            PartnerPermission permission;

            if (!usernameToJidMap.containsKey(username)) {
                continue;
            }
            String userJid = usernameToJidMap.get(username);

            try {
                permission = PartnerPermission.valueOf(permissions.get(i));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }

            partners.add(new Partner.Builder()
                    .userJid(userJid)
                    .permission(permission)
                    .build());

            if (partners.size() > 100) {
                return Optional.empty();
            }
        }

        return Optional.of(partners);
    }
}
