import { NotFoundError } from '../../../../modules/api/error';

export const profilesActions = {
  fetchPublic: (username: string) => {
    return async (dispatch, getState, { userProfileAPI, userAPI }) => {
      const users = await userAPI.findUsersByUsernames([username]);
      if (users[username] === undefined) {
        throw new NotFoundError();
      }
      const userJid = users[username].jid;
      return await userProfileAPI.getPublicProfile(userJid);
    };
  },
};
