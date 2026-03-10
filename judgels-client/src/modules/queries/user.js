import { queryOptions } from '@tanstack/react-query';

import { userAPI } from '../api/jophiel/user';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const userQueryOptions = userJid =>
  queryOptions({
    queryKey: ['user', userJid],
    queryFn: () => userAPI.getUser(getToken(), userJid),
  });

export const usersQueryOptions = params => {
  const { page, orderBy, orderDir } = params || {};
  return queryOptions({
    queryKey: ['users', ...(params ? [params] : [])],
    queryFn: () => userAPI.getUsers(getToken(), page, orderBy, orderDir),
  });
};

export const upsertUsersMutationOptions = () => ({
  mutationFn: csv => userAPI.upsertUsers(getToken(), csv),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['users'] });
  },
});
