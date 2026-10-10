import { useSuspenseQuery } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { ContestAdminRole } from '../../modules/api/contestAdminRole';
import { ProblemAdminRole } from '../../modules/api/problemAdminRole';
import { SystemAdminRole } from '../../modules/api/systemAdminRole';
import { TrainingAdminRole } from '../../modules/api/trainingAdminRole';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';

export default function AdminIndexPage() {
  const {
    data: { role },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  if (role.account === SystemAdminRole.Admin || role.account === SystemAdminRole.Superadmin) {
    return <Navigate to="/admin/users" />;
  }
  if (role.problem === ProblemAdminRole.Admin) {
    return <Navigate to="/admin/problems" />;
  }
  if (role.contest === ContestAdminRole.Admin) {
    return <Navigate to="/admin/contests" />;
  }
  if (isTLX() && role.training === TrainingAdminRole.Admin) {
    return <Navigate to="/admin/courses" />;
  }
  return <Navigate to="/" />;
}
