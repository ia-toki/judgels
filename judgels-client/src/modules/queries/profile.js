import { queryOptions } from '@tanstack/react-query';

import { NotFoundError } from '../api/error';
import { submissionProgrammingAPI } from '../api/jerahmeel/submissionProgramming';
import { profileAPI } from '../api/jophiel/profile';
import { userSearchAPI } from '../api/jophiel/userSearch';
import { contestHistoryAPI } from '../api/uriel/contestHistory';
import { getToken } from '../session';

export const userJidByUsernameQueryOptions = username =>
  queryOptions({
    queryKey: ['user-jid-by-username', username],
    queryFn: async () => {
      const userJidsByUsername = await userSearchAPI.translateUsernamesToJids([username]);
      if (userJidsByUsername[username] === undefined) {
        throw new NotFoundError();
      }
      return userJidsByUsername[username];
    },
  });

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

export const profileSubmissionsQueryOptions = (username, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['profile', username, 'submissions', ...(params ? [params] : [])],
    queryFn: () => submissionProgrammingAPI.getSubmissions(getToken(), undefined, username, undefined, undefined, page),
  });
};

export const profileContestHistoryQueryOptions = username =>
  queryOptions({
    queryKey: ['profile', username, 'contest-history'],
    queryFn: () => contestHistoryAPI.getPublicHistory(username),
  });
