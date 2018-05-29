import { selectToken, selectUserJid } from '../../../../../../modules/session/sessionSelectors';
import { PutProfile } from '../../../../../../modules/session/sessionReducer';
import { UserProfile } from 'modules/api/jophiel/userProfile';

export const profileActions = {
  fetch: () => {
    return async (dispatch, getState, { userProfileAPI }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      const profile = await userProfileAPI.getProfile(token, userJid);

      dispatch(PutProfile.create(profile));
    };
  },

  update: (profile: UserProfile) => {
    return async (dispatch, getState, { userProfileAPI, toastActions }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      const newProfile = await userProfileAPI.updateProfile(token, userJid, profile);

      dispatch(PutProfile.create(newProfile));

      toastActions.showSuccessToast('Profile updated.');
    };
  },
};
