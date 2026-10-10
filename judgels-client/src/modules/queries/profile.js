import { queryOptions } from '@tanstack/react-query';

import { profileAPI } from '../api/profile';

export const basicProfileQueryOptions = userJid =>
  queryOptions({
    queryKey: ['profile', userJid, 'basic'],
    queryFn: () => profileAPI.getBasicProfile(userJid),
  });

export const topRatedProfilesQueryOptions = params => {
  const { page, pageSize } = params || {};
  return queryOptions({
    queryKey: ['profiles', 'top-rated', ...(params ? [params] : [])],
    queryFn: () => profileAPI.getTopRatedProfiles(page, pageSize),
  });
};
