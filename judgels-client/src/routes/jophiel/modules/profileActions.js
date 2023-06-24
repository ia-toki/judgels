import { NotFoundError } from '../../../modules/api/error';
import { PutUser, DelUser } from './profileReducer';
import { userSearchAPI } from '../../../modules/api/jophiel/userSearch';
import { profileAPI } from '../../../modules/api/jophiel/profile';

export function getUser(username) {
  return async dispatch => {
    const userJidsByUsername = await userSearchAPI.translateUsernamesToJids([username]);
    if (userJidsByUsername[username] === undefined) {
      throw new NotFoundError();
    }
    const userJid = userJidsByUsername[username];
    dispatch(PutUser({ userJid, username }));
  };
}

export const clearUser = DelUser;

export function getTopRatedProfiles(page, pageSize) {
  return async () => {
    return await profileAPI.getTopRatedProfiles(page, pageSize);
  };
}
