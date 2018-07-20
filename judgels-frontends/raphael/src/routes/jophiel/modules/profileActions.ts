import { selectToken } from '../../../modules/session/sessionSelectors';
import { UserProfile } from '../../../modules/api/jophiel/userProfile';

export const profileActions = {
  getProfile: (userJid: string) => {
    return async (dispatch, getState, { userProfileAPI }) => {
      const token = selectToken(getState());
      return await userProfileAPI.getProfile(token, userJid);
    };
  },

  updateProfile: (userJid: string, profile: UserProfile) => {
    return async (dispatch, getState, { userProfileAPI, toastActions }) => {
      const token = selectToken(getState());
      await userProfileAPI.updateProfile(token, userJid, profile);

      toastActions.showSuccessToast('Profile updated.');
    };
  },
};
