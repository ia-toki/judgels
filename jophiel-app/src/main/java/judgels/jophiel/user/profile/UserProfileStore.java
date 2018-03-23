package judgels.jophiel.user.profile;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.UserProfile;
import judgels.jophiel.persistence.Daos.UserProfileDao;
import judgels.jophiel.persistence.UserProfileModel;
import judgels.jophiel.persistence.UserProfileModel_;

public class UserProfileStore {
    private final UserProfileDao userProfileDao;

    @Inject
    public UserProfileStore(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    public UserProfile getUserProfile(String userJid) {
        return userProfileDao.selectByUniqueColumn(UserProfileModel_.userJid, userJid)
                .map(UserProfileStore::fromModel)
                .orElse(new UserProfile.Builder().build());
    }

    public UserProfile upsertUserProfile(String userJid, UserProfile userProfile) {
        Optional<UserProfileModel> maybeModel = userProfileDao.selectByUniqueColumn(UserProfileModel_.userJid, userJid);
        if (maybeModel.isPresent()) {
            UserProfileModel model = maybeModel.get();
            toModel(userJid, userProfile, model);
            return fromModel(userProfileDao.update(model));
        } else {
            UserProfileModel model = new UserProfileModel();
            toModel(userJid, userProfile, model);
            return fromModel(userProfileDao.insert(model));
        }
    }

    private static void toModel(String userJid, UserProfile profile, UserProfileModel model) {
        model.userJid = userJid;
        model.name = profile.getName().orElse(null);
        model.gender = profile.getGender().orElse(null);
        model.nationality = profile.getNationality().orElse(null);
        model.homeAddress = profile.getHomeAddress().orElse(null);
        model.institution = profile.getInstitution().orElse(null);
        model.country = profile.getCountry().orElse(null);
        model.province = profile.getProvince().orElse(null);
        model.city = profile.getCity().orElse(null);
        model.shirtSize = profile.getShirtSize().orElse(null);
    }

    private static UserProfile fromModel(UserProfileModel model) {
        return new UserProfile.Builder()
                .name(Optional.ofNullable(model.name))
                .gender(Optional.ofNullable(model.gender))
                .nationality(Optional.ofNullable(model.nationality))
                .homeAddress(Optional.ofNullable(model.homeAddress))
                .institution(Optional.ofNullable(model.institution))
                .country(Optional.ofNullable(model.country))
                .province(Optional.ofNullable(model.province))
                .city(Optional.ofNullable(model.city))
                .shirtSize(Optional.ofNullable(model.shirtSize))
                .build();
    }
}
