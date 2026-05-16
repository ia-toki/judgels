import { queryOptions } from '@tanstack/react-query';

import { adminUserRoleAPI } from '../api/admin/userRole';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const userRolesQueryOptions = () =>
  queryOptions({
    queryKey: ['userRoles'],
    queryFn: () => adminUserRoleAPI.getUserRoles(getToken()),
  });

export const setUserRolesMutationOptions = () => ({
  mutationFn: usernameToRoleMap => adminUserRoleAPI.setUserRoles(getToken(), usernameToRoleMap),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['userRoles'] });
  },
});
