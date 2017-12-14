package judgels.jophiel.user.info;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.UserInfo;

public class UserInfoStore {
    private final UserInfoDao userInfoDao;

    @Inject
    public UserInfoStore(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }

    public UserInfo getUserInfo(String userJid) {
        return userInfoDao.selectByUserJid(userJid)
                .map(UserInfoStore::fromModel)
                .orElse(new UserInfo.Builder().build());
    }

    public void upsertUserInfo(String userJid, UserInfo userInfo) {
        Optional<UserInfoModel> maybeModel = userInfoDao.selectByUserJid(userJid);
        if (maybeModel.isPresent()) {
            UserInfoModel model = maybeModel.get();
            toModel(userJid, userInfo, model);
            userInfoDao.update(model);
        } else {
            UserInfoModel model = new UserInfoModel();
            toModel(userJid, userInfo, model);
            userInfoDao.insert(model);
        }
    }

    private static void toModel(String userJid, UserInfo info, UserInfoModel model) {
        model.userJid = userJid;
        model.gender = info.getGender().orElse(null);
        model.streetAddress = info.getStreetAddress().orElse(null);
        model.postalCode = info.getPostalCode().orElse(null);
        model.institution = info.getInstitution().orElse(null);
        model.city = info.getCity().orElse(null);
        model.provinceOrState = info.getProvinceOrState().orElse(null);
        model.country = info.getCountry().orElse(null);
        model.shirtSize = info.getShirtSize().orElse(null);
    }

    private static UserInfo fromModel(UserInfoModel model) {
        return new UserInfo.Builder()
                .gender(Optional.ofNullable(model.gender))
                .streetAddress(Optional.ofNullable(model.streetAddress))
                .postalCode(Optional.ofNullable(model.postalCode))
                .institution(Optional.ofNullable(model.institution))
                .city(Optional.ofNullable(model.city))
                .provinceOrState(Optional.ofNullable(model.provinceOrState))
                .country(Optional.ofNullable(model.country))
                .shirtSize(Optional.ofNullable(model.shirtSize))
                .build();
    }
}
