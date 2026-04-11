import { useSuspenseQuery } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';

import { JerahmeelRole } from '../../modules/api/jerahmeel/role';
import { JophielRole } from '../../modules/api/jophiel/role';
import { UrielRole } from '../../modules/api/uriel/role';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';

export default function AdminIndexPage() {
  const {
    data: { role },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  if (role.jophiel === JophielRole.Admin || role.jophiel === JophielRole.Superadmin) {
    return <Navigate to="/admin/users" />;
  }
  if (role.uriel === UrielRole.Admin) {
    return <Navigate to="/admin/contests" />;
  }
  if (role.jerahmeel === JerahmeelRole.Admin) {
    return <Navigate to="/admin/courses" />;
  }
  return <Navigate to="/" />;
}
