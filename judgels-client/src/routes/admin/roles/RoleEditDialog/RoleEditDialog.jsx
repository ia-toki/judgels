import { Button, Classes, Collapse, Dialog, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Required } from '../../../../components/forms/validations';
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
    return [username, role.account || '', role.problem || '', role.contest || '', role.training || ''].join(',');
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
    const [username, account, problem, contest, training] = parts;
    if (!username) {
      continue;
    }
    const role = {};
    if (account) {
      role.account = account;
    }
    if (problem) {
      role.problem = problem;
    }
    if (contest) {
      role.contest = contest;
    }
    if (training) {
      role.training = training;
    }
    map[username] = role;
  }

  return map;
}

export function RoleEditDialog({ currentData }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isInstructionOpen, setIsInstructionOpen] = useState(false);

  const setRolesMutation = useMutation(setUserRolesMutationOptions());

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
  };

  const handleSubmit = async values => {
    const roleMap = parseCsvToRoleMap(values.csv);
    await setRolesMutation.mutateAsync(roleMap);
    toastActions.showSuccessToast('Roles updated.');
    setIsDialogOpen(false);
  };

  const csvField = {
    name: 'csv',
    label: 'CSV rows, one user per row',
    rows: 10,
    isCode: true,
    validate: Required,
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
        <Form onSubmit={handleSubmit} initialValues={{ csv: buildCsvFromData(currentData) }}>
          {({ handleSubmit, submitting }) => (
            <form onSubmit={handleSubmit}>
              <div className={Classes.DIALOG_BODY}>
                <Button
                  onClick={() => setIsInstructionOpen(!isInstructionOpen)}
                  rightIcon={isInstructionOpen ? 'chevron-up' : 'chevron-down'}
                  text="Instructions"
                  style={{ marginBottom: '10px' }}
                />
                <Collapse isOpen={isInstructionOpen}>
                  <h5>Row format</h5>
                  <pre>{'<username>,<account role>,<problem role>,<contest role>,<training role>'}</pre>
                  <br />
                  <ul>
                    <li>
                      <code>account role</code>: account management role
                    </li>
                    <li>
                      <code>problem role</code>: problem management role
                    </li>
                    <li>
                      <code>contest role</code>: contest management role
                    </li>
                    <li>
                      <code>training role</code>: training management role
                    </li>
                  </ul>
                  <br />
                  <p>
                    Each role is either <code>ADMIN</code>, or an empty string.
                  </p>
                  <hr />
                  <h5>Example</h5>
                  <pre>{`andi,ADMIN,ADMIN,ADMIN,ADMIN\nbudi,,ADMIN,,`}</pre>
                  <hr />
                </Collapse>

                <Field component={FormTextArea} {...csvField} />
              </div>
              <div className={Classes.DIALOG_FOOTER}>
                <div className={Classes.DIALOG_FOOTER_ACTIONS}>
                  <Button text="Cancel" onClick={toggleDialog} />
                  <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={submitting} />
                </div>
              </div>
            </form>
          )}
        </Form>
      </Dialog>
    </>
  );
}
