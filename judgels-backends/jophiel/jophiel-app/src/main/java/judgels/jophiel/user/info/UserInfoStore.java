package judgels.jophiel.user.info;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.persistence.UserInfoDao;
import judgels.jophiel.persistence.UserInfoModel;

public class UserInfoStore {
    private final UserInfoDao profileDao;

    @Inject
    public UserInfoStore(UserInfoDao profileDao) {
        this.profileDao = profileDao;
    }

    public UserInfo getInfo(String userJid) {
        return profileDao.selectByUserJid(userJid)
                .map(UserInfoStore::fromModel)
                .orElse(new UserInfo.Builder().build());
    }

    public Map<String, UserInfo> getInfos(Set<String> userJids) {
        return profileDao.selectAllByUserJids(userJids).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> fromModel(e.getValue())));
    }

    public UserInfo upsertInfo(String userJid, UserInfo profile) {
        Optional<UserInfoModel> maybeModel = profileDao.selectByUserJid(userJid);
        if (maybeModel.isPresent()) {
            UserInfoModel model = maybeModel.get();
            toModel(userJid, profile, model);
            return fromModel(profileDao.update(model));
        } else {
            UserInfoModel model = new UserInfoModel();
            toModel(userJid, profile, model);
            return fromModel(profileDao.insert(model));
        }
    }

    private static void toModel(String userJid, UserInfo profile, UserInfoModel model) {
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

    private static UserInfo fromModel(UserInfoModel model) {
        return new UserInfo.Builder()
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
