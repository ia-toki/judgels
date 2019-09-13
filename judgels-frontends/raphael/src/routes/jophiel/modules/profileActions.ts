import { NotFoundError } from '../../../modules/api/error';

import { PutUser, DelUser } from './profileReducer';

export const profileActions = {
  getUser: (username: string) => {
    return async (dispatch, getState, { userSearchAPI }) => {
      const userJidsByUsername = await userSearchAPI.translateUsernamesToJids([username]);
      if (userJidsByUsername[username] === undefined) {
        throw new NotFoundError();
      }
      const userJid = userJidsByUsername[username];
      dispatch(PutUser.create({ userJid, username }));
    };
  },

  clearUser: DelUser.create,

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
