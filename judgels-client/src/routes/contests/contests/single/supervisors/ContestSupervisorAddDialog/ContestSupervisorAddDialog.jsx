import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useState } from 'react';

import { SupervisorManagementPermission } from '../../../../../../modules/api/uriel/contestSupervisor';
import { upsertContestSupervisorsMutationOptions } from '../../../../../../modules/queries/contestSupervisor';
import ContestSupervisorAddForm from '../ContestSupervisorAddForm/ContestSupervisorAddForm';
import { ContestSupervisorAddResultTable } from '../ContestSupervisorAddResultTable/ContestSupervisorAddResultTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function ContestSupervisorAddDialog({ contest }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [submitted, setSubmitted] = useState(undefined);

  const upsertSupervisorsMutation = useMutation(upsertContestSupervisorsMutationOptions(contest.jid));

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
    setSubmitted(undefined);
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-supervisor-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const getPermissionList = managementPermissions => {
    return !managementPermissions
      ? []
      : Object.keys(managementPermissions)
          .filter(p => managementPermissions[p])
          .map(p => SupervisorManagementPermission[p]);
  };

  const addSupervisors = async dataForm => {
    const usernames = dataForm.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    const data = {
      usernames,
      managementPermissions: getPermissionList(dataForm.managementPermissions),
    };
    const response = await upsertSupervisorsMutation.mutateAsync(data);
    if (usernames.length === Object.keys(response.upsertedSupervisorProfilesMap).length) {
      toastActions.showSuccessToast('Supervisors added.');
      setIsDialogOpen(false);
    } else {
      setSubmitted({ usernames, response });
    }
  };

  const renderDialogAddResultTable = () => {
    const { usernames, response } = submitted;
    const { upsertedSupervisorProfilesMap: insertedSupervisorProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-supervisor-dialog-result-body')}>
          <ContestSupervisorAddResultTable
            usernames={usernames}
            insertedSupervisorProfilesMap={insertedSupervisorProfilesMap}
          />
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Done" intent={Intent.PRIMARY} onClick={toggleDialog} />
          </div>
        </div>
      </>
    );
  };

  const dialogBody =
    submitted !== undefined ? (
      renderDialogAddResultTable()
    ) : (
      <ContestSupervisorAddForm renderFormComponents={renderDialogForm} onSubmit={addSupervisors} />
    );
  const dialogTitle = submitted !== undefined ? 'Add/update supervisors results' : 'Add/update supervisors';

  return (
    <div className="content-card__section">
      <Button
        className="contest-supervisor-dialog-button"
        intent={Intent.PRIMARY}
        icon={<Plus />}
        onClick={toggleDialog}
        disabled={isDialogOpen}
      >
        Add/update supervisors
      </Button>
      <Dialog
        className="contest-supervisor-dialog"
        isOpen={isDialogOpen}
        onClose={toggleDialog}
        title={dialogTitle}
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {dialogBody}
      </Dialog>
    </div>
  );
}
