import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useState } from 'react';

import { createContestAnnouncementMutationOptions } from '../../../../../../modules/queries/contestAnnouncement';
import ContestAnnouncementCreateForm from '../ContestAnnouncementCreateForm/ContestAnnouncementCreateForm';

import * as toastActions from '../../../../../../modules/toast/toastActions';

import './ContestAnnouncementCreateDialog.scss';

export function ContestAnnouncementCreateDialog({ contest }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const createAnnouncementMutation = useMutation(createContestAnnouncementMutationOptions(contest.jid));

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
  };

  const renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New announcement
      </Button>
    );
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY)}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const createAnnouncement = async data => {
    await createAnnouncementMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Announcement created.');
      },
      onSettled: () => {
        setIsDialogOpen(false);
      },
    });
  };

  const renderDialog = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: createAnnouncement,
    };
    return (
      <Dialog
        className="contest-announcement-create-dialog"
        isOpen={isDialogOpen}
        onClose={toggleDialog}
        title="Create new announcement"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <ContestAnnouncementCreateForm {...props} />
      </Dialog>
    );
  };

  return (
    <div className="content-card__section">
      {renderButton()}
      {renderDialog()}
    </div>
  );
}
