import { Classes, Button, Dialog } from '@blueprintjs/core';
import classNames from 'classnames';

import ContestAnnouncementEditForm from '../ContestAnnouncementEditForm/ContestAnnouncementEditForm';

import './ContestAnnouncementEditDialog.css';

export function ContestAnnouncementEditDialog({ contest, announcement, onToggleEditDialog, onUpdateAnnouncement }) {
  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-announcement-edit-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={closeDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const updateAnnouncement = async data => {
    await onUpdateAnnouncement(contest.jid, announcement.jid, data);
    closeDialog();
  };

  const closeDialog = () => {
    onToggleEditDialog();
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
