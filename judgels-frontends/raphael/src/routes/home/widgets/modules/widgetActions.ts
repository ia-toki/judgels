import { profileAPI } from '../../../../modules/api/jophiel/profile';
import { userStatsAPI } from '../../../../modules/api/jerahmeel/user';

export const widgetActions = {
  getTopRatedProfiles: (page?: number, pageSize?: number) => {
    return async () => {
      return await profileAPI.getTopRatedProfiles(page, pageSize);
    };
  },

  getTopUserStats: (page?: number, pageSize?: number) => {
    return async () => {
      return await userStatsAPI.getTopUserStats(page, pageSize);
    };
  },
};
