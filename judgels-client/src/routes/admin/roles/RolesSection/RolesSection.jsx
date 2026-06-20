import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { setUserRolesMutationOptions } from '../../../../modules/queries/userRole';
import { RoleSelect, getRoleFields, roleNames } from '../RoleSelect/RoleSelect';
import { RolesAddDialog } from '../RolesAddDialog/RolesAddDialog';

import * as toastActions from '../../../../modules/toast/toastActions';

import './RolesSection.scss';

function buildRows(roles) {
  const { data, profilesMap } = roles;
  return data.map(entry => {
    const profile = profilesMap[entry.userJid];
    const { role } = entry;
    return {
      username: profile ? profile.username : entry.userJid,
      account: role.account === 'ADMIN' ? 'ADMIN' : '',
      problem: role.problem || '',
      contest: role.contest || '',
      training: role.training || '',
    };
  });
}

function buildRoleMap(rows) {
  const map = {};
  for (const row of rows) {
    const role = {};
    for (const { key } of getRoleFields()) {
      if (row[key]) {
        role[key] = row[key];
      }
    }
    map[row.username] = role;
  }
  return map;
}

export function RolesSection({ roles }) {
  const [isEditing, setIsEditing] = useState(false);
  const [rows, setRows] = useState(() => buildRows(roles));

  const setRolesMutation = useMutation(setUserRolesMutationOptions());

  const roleFields = getRoleFields();

  const updateRow = (index, patch) => {
    setRows(prev => prev.map((row, i) => (i === index ? { ...row, ...patch } : row)));
  };

  const removeRow = index => {
    setRows(prev => prev.filter((_, i) => i !== index));
  };

  const addUsers = users => {
    setRows(prev => {
      const next = [...prev];
      for (const user of users) {
        const index = next.findIndex(row => row.username === user.username);
        if (index >= 0) {
          next[index] = user;
        } else {
          next.push(user);
        }
      }
      return next;
    });
  };

  const startEditing = () => {
    setIsEditing(true);
  };

  const cancelEditing = () => {
    setRows(buildRows(roles));
    setIsEditing(false);
  };

  const handleSave = async () => {
    await setRolesMutation.mutateAsync(buildRoleMap(rows));
    toastActions.showSuccessToast('Roles updated.');
    setIsEditing(false);
  };

  const renderRow = (row, index) => (
    <tr key={row.username}>
      <td>{row.username}</td>
      {roleFields.map(({ key }) =>
        isEditing ? (
          <td key={key}>
            <RoleSelect
              label={`${key} role for ${row.username}`}
              value={row[key]}
              onChange={value => updateRow(index, { [key]: value })}
            />
          </td>
        ) : (
          <td key={key}>{roleNames[row[key]] || '-'}</td>
        )
      )}
      {isEditing && (
        <td className="col-fit">
          <Trash
            className="action"
            intent={Intent.DANGER}
            title={`remove ${row.username}`}
            onClick={() => removeRow(index)}
          />
        </td>
      )}
    </tr>
  );

  return (
    <Flex flexDirection="column" gap={2}>
      {!isEditing && (
        <ActionButtons>
          <Button intent={Intent.PRIMARY} icon={<Edit />} onClick={startEditing}>
            Edit roles
          </Button>
        </ActionButtons>
      )}

      <HTMLTable striped className="table-list roles-table">
        <thead>
          <tr>
            <th>User</th>
            {roleFields.map(({ key, label }) => (
              <th key={key}>{label}</th>
            ))}
            {isEditing && <th />}
          </tr>
        </thead>
        <tbody>{rows.map(renderRow)}</tbody>
      </HTMLTable>

      {rows.length === 0 && (
        <p>
          <small>No roles yet.</small>
        </p>
      )}

      {isEditing && (
        <Flex justifyContent="space-between" alignItems="center" gap={1}>
          <RolesAddDialog onAdd={addUsers} />

          <ActionButtons>
            <Button text="Cancel" disabled={setRolesMutation.isPending} onClick={cancelEditing} />
            <Button text="Save" intent={Intent.PRIMARY} loading={setRolesMutation.isPending} onClick={handleSave} />
          </ActionButtons>
        </Flex>
      )}
    </Flex>
  );
}
