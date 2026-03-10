import { queryOptions } from '@tanstack/react-query';

import { userRoleAPI } from '../api/jophiel/userRole';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const userRolesQueryOptions = () =>
  queryOptions({
    queryKey: ['userRoles'],
    queryFn: () => userRoleAPI.getUserRoles(getToken()),
  });

export const setUserRolesMutationOptions = () => ({
  mutationFn: usernameToRoleMap => userRoleAPI.setUserRoles(getToken(), usernameToRoleMap),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['userRoles'] });
  },
});
