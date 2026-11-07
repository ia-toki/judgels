import { useSuspenseQuery } from '@tanstack/react-query';

import { Topbar } from '../../../../components/Topbar/Topbar';
import { isProblemAdmin, isSystemAdmin } from '../../../../modules/api/userRole';
import { userWebConfigQueryOptions } from '../../../../modules/queries/userWeb';

export function RolesTopbar({ activeItemPath }) {
  const {
    data: { roles },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  const items = [
    isSystemAdmin(roles) && { path: '/admin/roles/admins', title: 'Admins' },
    isProblemAdmin(roles) && { path: '/admin/roles/problem-writers', title: 'Problem Writers' },
  ].filter(Boolean);

  if (items.length < 2) {
    return null;
  }
  return <Topbar activeItemPath={activeItemPath} items={items} onResolveItemUrl={path => path} />;
}
