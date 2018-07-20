import { NotFoundError } from '../../../modules/api/error';
import { DelPublicProfile, PutPublicProfile } from './publicProfileReducer';

export const publicProfileActions = {
  getPublicProfile: (username: string, skipDispatch?: boolean) => {
    return async (dispatch, getState, { userProfileAPI, userAPI }) => {
      const users = await userAPI.getUsersByUsernames([username]);
      if (users[username] === undefined) {
        throw new NotFoundError();
      }
      const userJid = users[username].jid;
      const profile = await userProfileAPI.getPublicProfile(userJid);

      if (!skipDispatch) {
        dispatch(PutPublicProfile.create(profile));
      }
    };
  },

  clearPublicProfile: DelPublicProfile.create,
};
