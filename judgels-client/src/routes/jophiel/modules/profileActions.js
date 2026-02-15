import { profileAPI } from '../../../modules/api/jophiel/profile';

export async function getTopRatedProfiles(page, pageSize) {
  return await profileAPI.getTopRatedProfiles(page, pageSize);
}
