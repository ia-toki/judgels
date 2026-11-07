import { useSuspenseQuery } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { isContestAdmin, isSystemAdmin, isTrainingAdmin } from '../../modules/api/userRole';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';

export default function AdminIndexPage() {
  const {
    data: { roles },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  if (isSystemAdmin(roles)) {
    return <Navigate to="/admin/users" />;
  }
  if (isContestAdmin(roles)) {
    return <Navigate to="/admin/contests" />;
  }
  if (isTLX() && isTrainingAdmin(roles)) {
    return <Navigate to="/admin/courses" />;
  }
  return <Navigate to="/" />;
}
