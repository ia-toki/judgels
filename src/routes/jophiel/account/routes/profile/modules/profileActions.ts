import { selectToken, selectUserJid } from '../../../../../../modules/session/sessionSelectors';
import { PutProfile } from '../../../../../../modules/session/sessionReducer';
import { UserProfile } from 'modules/api/jophiel/user';

export const profileActions = {
  get: () => {
    return async (dispatch, getState, { userAPI }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      const profile = await userAPI.getUserProfile(token, userJid);

      dispatch(PutProfile.create(profile));
    };
  },

  update: (profile: UserProfile) => {
    return async (dispatch, getState, { userAPI, toastActions }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      const newProfile = await userAPI.updateUserProfile(token, userJid, profile);

      dispatch(PutProfile.create(newProfile));

      toastActions.showSuccessToast('Profile updated.');
    };
  },
};
