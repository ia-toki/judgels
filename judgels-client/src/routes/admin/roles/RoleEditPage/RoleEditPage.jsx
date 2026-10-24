import { Button, FormGroup, Intent } from '@blueprintjs/core';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { NotFoundError } from '../../../../modules/api/error';
import { setUserRoleMutationOptions, userRolesQueryOptions } from '../../../../modules/queries/userRole';
import { useSession } from '../../../../modules/session';
import { RoleCheckboxes, getRoleFields } from '../RoleCheckboxes/RoleCheckboxes';
import { buildUsers } from '../RolesSection/RolesSection';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function RoleEditPage() {
  const { username } = useParams({ strict: false });
  const { user: actor } = useSession();

  const { data: roles } = useSuspenseQuery(userRolesQueryOptions());
  const user = buildUsers(roles, actor?.jid).find(user => user.username === username);
  if (!user || user.isSuperadmin) {
    throw new NotFoundError();
  }

  return (
    <ContentCard title={<BreadcrumbTitle parent="Roles" current={username} />}>
      <RoleEditForm user={user} />
    </ContentCard>
  );
}

function RoleEditForm({ user }) {
  const navigate = useNavigate();
  const [checked, setChecked] = useState(() =>
    Object.fromEntries(getRoleFields().map(({ key }) => [key, !!user.role[key]]))
  );

  const setRoleMutation = useMutation(setUserRoleMutationOptions(user.userJid));

  // Roles not shown here are kept as they are.
  const role = Object.fromEntries(Object.entries(user.role).filter(([, value]) => value));
  for (const { key } of getRoleFields()) {
    if (checked[key]) {
      role[key] = 'ADMIN';
    } else {
      delete role[key];
    }
  }
  const isRemoving = Object.keys(role).length === 0;

  const goToRoles = () => navigate({ to: '/admin/roles' });

  const save = async () => {
    await setRoleMutation.mutateAsync(role);
    toastActions.showSuccessToast(isRemoving ? 'Roles removed.' : 'Roles updated.');
    goToRoles();
  };

  return (
    <div>
      <FormGroup label="Roles">
        <RoleCheckboxes value={checked} onChange={setChecked} disabledKeys={user.isSelf ? ['account'] : []} />
      </FormGroup>
      <ActionButtons justifyContent="end">
        <Button text="Cancel" disabled={setRoleMutation.isPending} onClick={goToRoles} />
        <Button
          text={isRemoving ? 'Remove all roles' : 'Save'}
          intent={isRemoving ? Intent.DANGER : Intent.PRIMARY}
          loading={setRoleMutation.isPending}
          onClick={save}
        />
      </ActionButtons>
    </div>
  );
}
