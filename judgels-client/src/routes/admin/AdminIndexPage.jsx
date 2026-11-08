import { useSuspenseQuery } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';

import { isContestAdmin, isProblemWriter, isSystemAdmin, isTrainingAdmin } from '../../modules/api/userRole';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';
import { isTrainingEnabled } from '../../modules/webConfig';

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
  if (isTrainingEnabled() && isTrainingAdmin(roles)) {
    return <Navigate to="/admin/courses" />;
  }
  if (isProblemWriter(roles)) {
    return <Navigate to="/admin/problems" />;
  }
  return <Navigate to="/" />;
}
