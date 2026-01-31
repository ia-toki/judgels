import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useState } from 'react';

import ContestCreateForm from '../ContestCreateForm/ContestCreateForm';

export function ContestCreateDialog({ onCreateContest }) {
  const [state, setState] = useState({ isDialogOpen: false });

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
        New contest
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen }));
  };

  const renderDialog = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: createContest,
    };
    return (
      <Dialog
        isOpen={state.isDialogOpen}
        onClose={toggleDialog}
        title="Create new contest"
        canOutsideClickClose={false}
      >
        <ContestCreateForm {...props} />
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

  const createContest = async data => {
    await onCreateContest(data);
    setState(prevState => ({ ...prevState, isDialogOpen: false }));
  };

  return render();
}
