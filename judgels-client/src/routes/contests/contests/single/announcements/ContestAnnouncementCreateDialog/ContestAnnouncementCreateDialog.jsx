import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import classNames from 'classnames';
import { useState } from 'react';

import ContestAnnouncementCreateForm from '../ContestAnnouncementCreateForm/ContestAnnouncementCreateForm';

import './ContestAnnouncementCreateDialog.scss';

export function ContestAnnouncementCreateDialog({ contest, onCreateAnnouncement }) {
  const [state, setState] = useState({
    isDialogOpen: false,
  });

  const render = () => {
    return (
      <div className="content-card__section">
        {renderButton()}
        {renderDialog()}
      </div>
    );
  };

  const renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={state.isDialogOpen}>
        New announcement
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen }));
  };

  const renderDialog = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: createAnnouncement,
    };
    return (
      <Dialog
        className="contest-announcement-create-dialog"
        isOpen={state.isDialogOpen}
        onClose={toggleDialog}
        title="Create new announcement"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <ContestAnnouncementCreateForm {...props} />
      </Dialog>
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
    await onCreateAnnouncement(contest.jid, data);
    setState(prevState => ({ ...prevState, isDialogOpen: false }));
  };

  return render();
}
