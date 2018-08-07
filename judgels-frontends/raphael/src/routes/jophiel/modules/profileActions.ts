import { NotFoundError } from 'modules/api/error';

import { DelUserJid, PutUserJid } from './profileReducer';

export const profileActions = {
  getUserJid: (username: string) => {
    return async (dispatch, getState, { userAPI }) => {
      const users = await userAPI.translateUsernamesToJids([username]);
      if (users[username] === undefined) {
        throw new NotFoundError();
      }
      const userJid = users[username];
      dispatch(PutUserJid.create(userJid));
    };
  },

  clearUserJid: DelUserJid.create,

  getProfile: (userJid: string) => {
    return async (dispatch, getState, { profileAPI }) => {
      const profiles = await profileAPI.getProfiles([userJid]);
      if (profiles[userJid] === undefined) {
        throw new NotFoundError();
      }
      return profiles[userJid];
    };
  },

  getTopRatedProfiles: (page?: number, pageSize?: number) => {
    return async (dispatch, getState, { profileAPI }) => {
      return await profileAPI.getTopRatedProfiles(page, pageSize);
    };
  },

  getBasicProfile: (userJid: string) => {
    return async (dispatch, getState, { profileAPI }) => {
      return await profileAPI.getBasicProfile(userJid);
    };
  },
};
