import { Button, Classes, Dialog } from '@blueprintjs/core';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';

import { updateContestAnnouncementMutationOptions } from '../../../../../../modules/queries/contestAnnouncement';
import ContestAnnouncementEditForm from '../ContestAnnouncementEditForm/ContestAnnouncementEditForm';

import * as toastActions from '../../../../../../modules/toast/toastActions';

import './ContestAnnouncementEditDialog.scss';

export function ContestAnnouncementEditDialog({ contest, announcement, onToggleEditDialog }) {
  const updateAnnouncementMutation = useMutation(
    updateContestAnnouncementMutationOptions(contest.jid, announcement?.jid)
  );

  const closeDialog = () => {
    onToggleEditDialog();
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY)}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={closeDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const updateAnnouncement = async data => {
    await updateAnnouncementMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Announcement updated.');
      },
    });
    closeDialog();
  };

  const props = {
    renderFormComponents: renderDialogForm,
    onSubmit: updateAnnouncement,
    initialValues: announcement && {
      title: announcement.title,
      content: announcement.content,
      status: announcement.status,
    },
  };

  return (
    <Dialog
      className="contest-announcement-edit-dialog"
      isOpen={!!announcement}
      onClose={closeDialog}
      title="Edit announcement"
      canOutsideClickClose={false}
      enforceFocus={false}
    >
      <ContestAnnouncementEditForm {...props} />
    </Dialog>
  );
}
