import { useSuspenseQuery } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { ContestAdminRole } from '../../modules/api/contestAdminRole';
import { TrainingAdminRole } from '../../modules/api/trainingAdminRole';
import { UserAdminRole } from '../../modules/api/userAdminRole';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';

export default function AdminIndexPage() {
  const {
    data: { role },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  if (role.account === UserAdminRole.Admin || role.account === UserAdminRole.Superadmin) {
    return <Navigate to="/admin/users" />;
  }
  if (role.contest === ContestAdminRole.Admin) {
    return <Navigate to="/admin/contests" />;
  }
  if (isTLX() && role.training === TrainingAdminRole.Admin) {
    return <Navigate to="/admin/courses" />;
  }
  return <Navigate to="/" />;
}
