import { queryOptions } from '@tanstack/react-query';

import { userInfoAPI } from '../api/jophiel/userInfo';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const userInfoQueryOptions = userJid =>
  queryOptions({
    queryKey: ['user', userJid, 'info'],
    queryFn: () => userInfoAPI.getInfo(getToken(), userJid),
  });

export const updateUserInfoMutationOptions = userJid => ({
  mutationFn: info => userInfoAPI.updateInfo(getToken(), userJid, info),
  onSuccess: () => {
    queryClient.invalidateQueries(userInfoQueryOptions(userJid));
  },
});
