import { Alert, Button, HTMLTable, Intent, Tag } from '@blueprintjs/core';
import { Edit, Tick, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { setUserRoleMutationOptions } from '../../../../modules/queries/userRole';
import { useSession } from '../../../../modules/session';
import { getRoleFields } from '../RoleCheckboxes/RoleCheckboxes';
import { RoleEditDialog } from '../RoleEditDialog/RoleEditDialog';
import { RolesAddDialog } from '../RolesAddDialog/RolesAddDialog';

import * as toastActions from '../../../../modules/toast/toastActions';

function buildUsers(roles, actorJid) {
  const { data, profilesMap } = roles;
  const users = data.map(({ userJid, role }) => ({
    userJid,
    role,
    profile: profilesMap[userJid],
    username: profilesMap[userJid]?.username ?? userJid,
    isSuperadmin: role.account === 'SUPERADMIN',
    isSelf: userJid === actorJid,
  }));
  return users.sort((a, b) => b.isSuperadmin - a.isSuperadmin || a.username.localeCompare(b.username));
}

export function RolesSection({ roles }) {
  const { user: actor } = useSession();

  const [userToEdit, setUserToEdit] = useState(undefined);
  const [userToRemove, setUserToRemove] = useState(undefined);

  const removeRoleMutation = useMutation(setUserRoleMutationOptions(userToRemove?.userJid));

  const roleFields = getRoleFields();
  const users = buildUsers(roles, actor?.jid);

  const removeUser = async () => {
    await removeRoleMutation.mutateAsync({});
    toastActions.showSuccessToast(`All roles removed from ${userToRemove.username}.`);
    setUserToRemove(undefined);
  };

  const renderActions = user => {
    if (user.isSuperadmin) {
      return <Tag minimal>Built-in</Tag>;
    }
    return (
      <ActionButtons>
        <Button small minimal icon={<Edit />} onClick={() => setUserToEdit(user)}>
          Edit
        </Button>
        <Button
          small
          minimal
          intent={Intent.DANGER}
          icon={<Trash />}
          disabled={user.isSelf}
          title={user.isSelf ? 'You cannot remove your own roles.' : undefined}
          aria-label={`Remove ${user.username}`}
          onClick={() => setUserToRemove(user)}
        >
          Remove
        </Button>
      </ActionButtons>
    );
  };

  const renderRow = user => (
    <tr key={user.userJid}>
      <td>{user.profile ? <UserRef profile={user.profile} /> : user.username}</td>
      {roleFields.map(({ key, label }) => (
        <td key={key}>{user.role[key] && <Tick title={`${label} admin`} />}</td>
      ))}
      <td className="col-fit">{renderActions(user)}</td>
    </tr>
  );

  return (
    <Flex flexDirection="column" gap={2}>
      <ActionButtons>
        <RolesAddDialog superadminUsername={users.find(user => user.isSuperadmin)?.username} />
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

      {userToEdit && <RoleEditDialog user={userToEdit} onClose={() => setUserToEdit(undefined)} />}

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
