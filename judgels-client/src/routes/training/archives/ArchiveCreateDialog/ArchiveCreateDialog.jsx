import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useState } from 'react';

import ArchiveCreateForm from '../ArchiveCreateForm/ArchiveCreateForm';

export function ArchiveCreateDialog({ onCreateArchive }) {
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
        New archive
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen }));
  };

  const renderDialog = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: createArchive,
    };
    return (
      <Dialog
        isOpen={state.isDialogOpen}
        onClose={toggleDialog}
        title="Create new archive"
        canOutsideClickClose={false}
      >
        <ArchiveCreateForm {...props} />
      </Dialog>
    );
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const createArchive = async data => {
    await onCreateArchive(data);
    setState(prevState => ({ ...prevState, isDialogOpen: false }));
  };

  return render();
}
