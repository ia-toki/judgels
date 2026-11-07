import { Flex } from '@blueprintjs/labs';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { isSystemAdmin } from '../../../../modules/api/userRole';
import { userRolesQueryOptions } from '../../../../modules/queries/userRole';
import { userWebConfigQueryOptions } from '../../../../modules/queries/userWeb';
import { RolesSection } from '../RolesSection/RolesSection';
import { RolesTopbar } from '../RolesTopbar/RolesTopbar';

import { RolesActions } from '../RolesActions/RolesActions';

export default function RolesPage() {
  const {
    data: { roles },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  if (!isSystemAdmin(roles)) {
    return <Navigate to="/admin/roles/problem-writers" replace />;
  }
  return <AdminRoles />;
}

function AdminRoles() {
  const { data: response } = useQuery(userRolesQueryOptions());

  return (
    <ContentCard title="Roles" action={<RolesActions />}>
      <Flex flexDirection="column" gap={5}>
        <RolesTopbar activeItemPath="/admin/roles/admins" />
        {response ? <RolesSection roles={response} /> : <LoadingState />}
      </Flex>
    </ContentCard>
  );
}
