import { queryOptions } from '@tanstack/react-query';

import { adminUserAPI } from '../api/admin/user';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const userByUsernameQueryOptions = username =>
  queryOptions({
    queryKey: ['user', 'username', username],
    queryFn: () => adminUserAPI.getUserByUsername(getToken(), username),
  });

export const usersQueryOptions = params => {
  const { page, orderBy, orderDir } = params || {};
  return queryOptions({
    queryKey: ['users', ...(params ? [params] : [])],
    queryFn: () => adminUserAPI.getUsers(getToken(), page, orderBy, orderDir),
  });
};

export const upsertUsersMutationOptions = () => ({
  mutationFn: csv => adminUserAPI.upsertUsers(getToken(), csv),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['users'] });
  },
});
