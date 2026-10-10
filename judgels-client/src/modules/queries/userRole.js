import { queryOptions } from '@tanstack/react-query';

import { userRoleAPI } from '../api/userRole';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const userRolesQueryOptions = () =>
  queryOptions({
    queryKey: ['user-roles'],
    queryFn: () => userRoleAPI.getRoles(getToken()),
  });

export const setUserRolesMutationOptions = () => ({
  mutationFn: usernameToRoleMap => userRoleAPI.setRoles(getToken(), usernameToRoleMap),
  onSuccess: () => {
    queryClient.invalidateQueries(userRolesQueryOptions());
  },
});
