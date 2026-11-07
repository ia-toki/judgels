import { useSuspenseQuery } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';

import { isSystemAdmin } from '../../../modules/api/userRole';
import { userWebConfigQueryOptions } from '../../../modules/queries/userWeb';

export default function RolesIndexPage() {
  const {
    data: { roles },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  return <Navigate to={isSystemAdmin(roles) ? '/admin/roles/admins' : '/admin/roles/problem-writers'} replace />;
}
