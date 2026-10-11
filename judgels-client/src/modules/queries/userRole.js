import { queryOptions } from '@tanstack/react-query';

import { userRoleAPI } from '../api/userRole';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const userRolesQueryOptions = () =>
  queryOptions({
    queryKey: ['user-roles'],
    queryFn: () => userRoleAPI.getRoles(getToken()),
  });

export const upsertUserRolesMutationOptions = () => ({
  mutationFn: ({ usernames, role }) => userRoleAPI.upsertRoles(getToken(), usernames, role),
  onSuccess: () => {
    queryClient.invalidateQueries(userRolesQueryOptions());
  },
});

export const setUserRoleMutationOptions = userJid => ({
  mutationFn: role => userRoleAPI.setRole(getToken(), userJid, role),
  onSuccess: () => {
    queryClient.invalidateQueries(userRolesQueryOptions());
  },
});
