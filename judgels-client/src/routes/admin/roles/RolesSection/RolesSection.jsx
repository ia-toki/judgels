import { Alert, Button, Classes, HTMLTable, Intent, Tag } from '@blueprintjs/core';
import { Edit, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { isSuperadmin } from '../../../../modules/api/userRole';
import { setUserRolesMutationOptions } from '../../../../modules/queries/userRole';
import { useSession } from '../../../../modules/session';
import { getRoleFields } from '../RoleCheckboxes/RoleCheckboxes';

import * as toastActions from '../../../../modules/toast/toastActions';

export function buildUsers(roles, actorJid) {
  const { data, profilesMap } = roles;
  const users = data.map(({ userJid, roles }) => ({
    userJid,
    roles,
    profile: profilesMap[userJid],
    username: profilesMap[userJid]?.username ?? userJid,
    isSuperadmin: isSuperadmin(roles),
    isSelf: userJid === actorJid,
  }));
  return users.sort((a, b) => a.username.localeCompare(b.username));
}

export function RolesSection({ roles }) {
  const { user: actor } = useSession();

  const [userToRemove, setUserToRemove] = useState(undefined);

  const removeRoleMutation = useMutation(setUserRolesMutationOptions(userToRemove?.userJid));

  const roleFields = getRoleFields();
  const allUsers = buildUsers(roles, actor?.jid);
  const users = allUsers.filter(user => !user.isSuperadmin);

  const removeUser = async () => {
    await removeRoleMutation.mutateAsync([]);
    toastActions.showSuccessToast('Roles removed.');
    setUserToRemove(undefined);
  };

  const renderActions = user => (
    <ActionButtons flexWrap="nowrap">
      <ButtonLink
        small
        className={Classes.MINIMAL}
        aria-label={`Edit ${user.username}`}
        to={`/admin/roles/admins/${user.username}`}
      >
        <Edit />
      </ButtonLink>
      <Button
        small
        minimal
        intent={Intent.DANGER}
        icon={<Trash />}
        disabled={user.isSelf}
        title={user.isSelf ? 'You cannot remove your own roles.' : undefined}
        aria-label={`Remove ${user.username}`}
        onClick={() => setUserToRemove(user)}
      />
    </ActionButtons>
  );

  const renderRow = user => (
    <tr key={user.userJid}>
      <td>{user.profile ? <UserRef profile={user.profile} /> : user.username}</td>
      {roleFields.map(({ role }) => (
        <td key={role}>{user.roles.includes(role) && <Tag intent={Intent.DANGER}>ADMIN</Tag>}</td>
      ))}
      <td className="col-fit">{renderActions(user)}</td>
    </tr>
  );

  return (
    <Flex flexDirection="column" gap={2}>
      <HTMLTable striped className="table-list">
        <thead>
          <tr>
            <th>User</th>
            {roleFields.map(({ role, label }) => (
              <th key={role}>{label}</th>
            ))}
            <th />
          </tr>
        </thead>
        <tbody>{users.map(renderRow)}</tbody>
      </HTMLTable>

      <Alert
        isOpen={userToRemove !== undefined}
        intent={Intent.DANGER}
        icon={<Trash />}
        confirmButtonText="Remove"
        cancelButtonText="Cancel"
        loading={removeRoleMutation.isPending}
        onConfirm={removeUser}
        onCancel={() => setUserToRemove(undefined)}
      >
        {userToRemove && `Remove all roles from ${userToRemove.username}?`}
      </Alert>
    </Flex>
  );
}
