import { Button, Classes, Dialog, Intent, TextArea } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { setUserRolesMutationOptions } from '../../../../modules/queries/userRole';

import * as toastActions from '../../../../modules/toast/toastActions';

function buildCsvFromData(response) {
  if (!response) {
    return '';
  }

  const { data, profilesMap } = response;
  const lines = data.map(entry => {
    const profile = profilesMap[entry.userJid];
    const username = profile ? profile.username : entry.userJid;
    const { role } = entry;
    return [username, role.jophiel || '', role.sandalphon || '', role.uriel || '', role.jerahmeel || ''].join(',');
  });

  return lines.join('\n');
}

function parseCsvToRoleMap(csv) {
  const map = {};
  const lines = csv
    .split('\n')
    .map(l => l.trim())
    .filter(l => l.length > 0);

  for (const line of lines) {
    const parts = line.split(',').map(s => s.trim());
    const [username, jophiel, sandalphon, uriel, jerahmeel] = parts;
    if (!username) {
      continue;
    }
    const role = {};
    if (jophiel) {
      role.jophiel = jophiel;
    }
    if (sandalphon) {
      role.sandalphon = sandalphon;
    }
    if (uriel) {
      role.uriel = uriel;
    }
    if (jerahmeel) {
      role.jerahmeel = jerahmeel;
    }
    map[username] = role;
  }

  return map;
}

export function RoleEditDialog({ currentData }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [csv, setCsv] = useState('');

  const setRolesMutation = useMutation(setUserRolesMutationOptions());

  const toggleDialog = () => {
    if (!isDialogOpen) {
      setCsv(buildCsvFromData(currentData));
    }
    setIsDialogOpen(open => !open);
  };

  const handleSubmit = () => {
    const roleMap = parseCsvToRoleMap(csv);
    setRolesMutation.mutate(roleMap, {
      onSuccess: () => {
        toastActions.showSuccessToast('Roles updated.');
        setIsDialogOpen(false);
      },
    });
  };

  return (
    <>
      <Button intent={Intent.PRIMARY} icon={<Edit />} onClick={toggleDialog} disabled={isDialogOpen}>
        Edit roles
      </Button>
      <Dialog
        isOpen={isDialogOpen}
        onClose={toggleDialog}
        title="Edit roles"
        canOutsideClickClose={false}
        style={{ width: 700 }}
      >
        <div className={Classes.DIALOG_BODY}>
          <p>User roles can be edited via CSV.</p>

          <p>
            <strong>Row format:</strong>
          </p>
          <pre>{'<username>,<jophiel role>,<sandalphon role>,<uriel role>,<jerahmeel role>'}</pre>

          <ul>
            <li>
              <code>jophiel role</code>: user management role
            </li>
            <li>
              <code>sandalphon role</code>: problem/lesson management role
            </li>
            <li>
              <code>uriel role</code>: contest management role
            </li>
            <li>
              <code>jerahmeel role</code>: training management role
            </li>
          </ul>

          <p>
            Each role is either <code>ADMIN</code>, or an empty string.
          </p>

          <p>
            <strong>Example</strong>
          </p>
          <pre>{`andi,ADMIN,ADMIN,ADMIN,ADMIN\nbudi,,ADMIN,,`}</pre>

          <hr />

          <p>CSV rows, one user per row:</p>
          <TextArea fill rows={10} value={csv} onChange={e => setCsv(e.target.value)} />
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Cancel" onClick={toggleDialog} />
            <Button
              text="Submit"
              intent={Intent.PRIMARY}
              onClick={handleSubmit}
              disabled={!csv.trim()}
              loading={setRolesMutation.isPending}
            />
          </div>
        </div>
      </Dialog>
    </>
  );
}
