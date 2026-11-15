import { Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { isProblemAdmin, isSystemAdmin } from '../../../../modules/api/userRole';
import { userWebConfigQueryOptions } from '../../../../modules/queries/userWeb';

export function RolesActions() {
  const {
    data: { roles },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  return (
    <ActionButtons justifyContent="end">
      {isSystemAdmin(roles) && (
        <ButtonLink small intent={Intent.PRIMARY} to="/admin/roles/admins/-/add">
          <Plus />
          <span>Add admins</span>
        </ButtonLink>
      )}
      {isProblemAdmin(roles) && (
        <ButtonLink small intent={Intent.PRIMARY} to="/admin/roles/problem-writers/-/add">
          <Plus />
          <span>Add problem writers</span>
        </ButtonLink>
      )}
    </ActionButtons>
  );
}
