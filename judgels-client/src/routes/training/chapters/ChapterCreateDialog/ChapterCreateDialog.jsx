import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useState } from 'react';

import ChapterCreateForm from '../ChapterCreateForm/ChapterCreateForm';

export function ChapterCreateDialog({ onCreateChapter }) {
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
        New chapter
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen }));
  };

  const renderDialog = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: createChapter,
    };
    return (
      <Dialog
        isOpen={state.isDialogOpen}
        onClose={toggleDialog}
        title="Create new chapter"
        canOutsideClickClose={false}
      >
        <ChapterCreateForm {...props} />
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

  const createChapter = async data => {
    await onCreateChapter(data);
    setState(prevState => ({ ...prevState, isDialogOpen: false }));
  };

  return render();
}
