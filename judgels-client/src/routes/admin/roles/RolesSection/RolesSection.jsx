import { Alert, Button, Classes, HTMLTable, Intent, Tag } from '@blueprintjs/core';
import { Edit, Trash, Upload } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { setUserRoleMutationOptions } from '../../../../modules/queries/userRole';
import { useSession } from '../../../../modules/session';
import { getRoleFields } from '../RoleCheckboxes/RoleCheckboxes';

import * as toastActions from '../../../../modules/toast/toastActions';

export function buildUsers(roles, actorJid) {
  const { data, profilesMap } = roles;
  const users = data.map(({ userJid, role }) => ({
    userJid,
    role,
    profile: profilesMap[userJid],
    username: profilesMap[userJid]?.username ?? userJid,
    isSuperadmin: role.account === 'SUPERADMIN',
    isSelf: userJid === actorJid,
  }));
  return users.sort((a, b) => a.username.localeCompare(b.username));
}

export function RolesSection({ roles }) {
  const { user: actor } = useSession();

  const [userToRemove, setUserToRemove] = useState(undefined);

  const removeRoleMutation = useMutation(setUserRoleMutationOptions(userToRemove?.userJid));

  const roleFields = getRoleFields();
  const allUsers = buildUsers(roles, actor?.jid);
  const users = allUsers.filter(user => !user.isSuperadmin);

  const removeUser = async () => {
    await removeRoleMutation.mutateAsync({});
    toastActions.showSuccessToast('Roles removed.');
    setUserToRemove(undefined);
  };

  const renderActions = user => (
    <ActionButtons>
      <ButtonLink
        small
        className={Classes.MINIMAL}
        aria-label={`Edit ${user.username}`}
        to={`/admin/roles/${user.username}`}
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
      {roleFields.map(({ key }) => (
        <td key={key}>{user.role[key] && <Tag intent={Intent.DANGER}>ADMIN</Tag>}</td>
      ))}
      <td className="col-fit">{renderActions(user)}</td>
    </tr>
  );

  return (
    <Flex flexDirection="column" gap={2}>
      <ActionButtons>
        <ButtonLink intent={Intent.PRIMARY} to="/admin/roles/-/upsert">
          <Upload />
          <span>Upsert roles</span>
        </ButtonLink>
      </ActionButtons>

      <HTMLTable striped className="table-list">
        <thead>
          <tr>
            <th>User</th>
            {roleFields.map(({ key, label }) => (
              <th key={key}>{label}</th>
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
