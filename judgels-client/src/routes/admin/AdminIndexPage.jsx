import { useSuspenseQuery } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { ArchiveAdminRole } from '../../modules/api/archiveAdminRole';
import { ContestAdminRole } from '../../modules/api/contestAdminRole';
import { UserAdminRole } from '../../modules/api/userAdminRole';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';

export default function AdminIndexPage() {
  const {
    data: { role },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  if (role.jophiel === UserAdminRole.Admin || role.jophiel === UserAdminRole.Superadmin) {
    return <Navigate to="/admin/users" />;
  }
  if (role.uriel === ContestAdminRole.Admin) {
    return <Navigate to="/admin/contests" />;
  }
  if (isTLX() && role.jerahmeel === ArchiveAdminRole.Admin) {
    return <Navigate to="/admin/courses" />;
  }
  return <Navigate to="/" />;
}
