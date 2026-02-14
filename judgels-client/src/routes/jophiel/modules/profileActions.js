import { profileAPI } from '../../../modules/api/jophiel/profile';

export function getTopRatedProfiles(page, pageSize) {
  return async () => {
    return await profileAPI.getTopRatedProfiles(page, pageSize);
  };
}
