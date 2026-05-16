import { queryOptions } from '@tanstack/react-query';

import { adminUserInfoAPI } from '../api/admin/userInfo';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const adminUserInfoQueryOptions = userJid =>
  queryOptions({
    queryKey: ['admin', 'user', userJid, 'info'],
    queryFn: () => adminUserInfoAPI.getInfo(getToken(), userJid),
  });

export const updateAdminUserInfoMutationOptions = userJid => ({
  mutationFn: info => adminUserInfoAPI.updateInfo(getToken(), userJid, info),
  onSuccess: () => {
    queryClient.invalidateQueries(adminUserInfoQueryOptions(userJid));
  },
});
