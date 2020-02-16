import { NotFoundError } from '../../../modules/api/error';
import { PutUser, DelUser } from './profileReducer';
import { userSearchAPI } from '../../../modules/api/jophiel/userSearch';
import { profileAPI } from '../../../modules/api/jophiel/profile';

export function getUser(username: string) {
  return async dispatch => {
    const userJidsByUsername = await userSearchAPI.translateUsernamesToJids([username]);
    if (userJidsByUsername[username] === undefined) {
      throw new NotFoundError();
    }
    const userJid = userJidsByUsername[username];
    dispatch(PutUser.create({ userJid, username }));
  };
}

export const clearUser = DelUser.create;

export function getProfile(userJid: string) {
  return async () => {
    const profiles = await profileAPI.getProfiles([userJid]);
    if (profiles[userJid] === undefined) {
      throw new NotFoundError();
    }
    return profiles[userJid];
  };
}

export function getTopRatedProfiles(page?: number, pageSize?: number) {
  return async () => {
    return await profileAPI.getTopRatedProfiles(page, pageSize);
  };
}
