import { NotFoundError } from '../../../modules/api/error';
import { profileAPI } from '../../../modules/api/jophiel/profile';
import { userSearchAPI } from '../../../modules/api/jophiel/userSearch';
import { DelUser, PutUser } from './profileReducer';

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
