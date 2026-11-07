import { Classes, Intent } from '@blueprintjs/core';
import { Edit, Upload } from '@blueprintjs/icons';
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
        <ButtonLink small intent={Intent.PRIMARY} to="/admin/roles/admins/-/upsert">
          <Upload />
          <span>Upsert admins</span>
        </ButtonLink>
      )}
      {isProblemAdmin(roles) && (
        <ButtonLink small className={Classes.OUTLINED} to="/admin/roles/problem-writers/edit">
          <Edit />
          <span>Edit problem writers</span>
        </ButtonLink>
      )}
    </ActionButtons>
  );
}
