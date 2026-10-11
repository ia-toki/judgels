import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { setUserRoleMutationOptions } from '../../../../modules/queries/userRole';
import { RoleCheckboxes, getRoleFields } from '../RoleCheckboxes/RoleCheckboxes';

import * as toastActions from '../../../../modules/toast/toastActions';

export function RoleEditDialog({ user, onClose }) {
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

  const save = async () => {
    await setRoleMutation.mutateAsync(role);
    toastActions.showSuccessToast(
      isRemoving ? `All roles removed from ${user.username}.` : `Roles of ${user.username} updated.`
    );
    onClose();
  };

  return (
    <Dialog isOpen onClose={onClose} title={`Edit roles of ${user.username}`} canOutsideClickClose={false}>
      <div className={Classes.DIALOG_BODY}>
        <RoleCheckboxes value={checked} onChange={setChecked} disabledKeys={user.isSelf ? ['account'] : []} />
        {user.isSelf && (
          <p>
            <small>You cannot remove your own system admin role.</small>
          </p>
        )}
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" disabled={setRoleMutation.isPending} onClick={onClose} />
          <Button
            text={isRemoving ? 'Remove all roles' : 'Save'}
            intent={isRemoving ? Intent.DANGER : Intent.PRIMARY}
            loading={setRoleMutation.isPending}
            onClick={save}
          />
        </div>
      </div>
    </Dialog>
  );
}
