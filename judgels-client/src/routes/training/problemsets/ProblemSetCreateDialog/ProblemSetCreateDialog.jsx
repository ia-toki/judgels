import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useState } from 'react';

import ProblemSetCreateForm from '../ProblemSetCreateForm/ProblemSetCreateForm';

export function ProblemSetCreateDialog({ onCreateProblemSet }) {
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
        New problemset
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen }));
  };

  const renderDialog = () => {
    const initialValues = {
      contestTime: new Date().toISOString(),
    };
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: createProblemSet,
      initialValues,
    };
    return (
      <Dialog
        isOpen={state.isDialogOpen}
        onClose={toggleDialog}
        title="Create new problemset"
        canOutsideClickClose={false}
      >
        <ProblemSetCreateForm {...props} />
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

  const createProblemSet = async data => {
    await onCreateProblemSet({
      slug: data.slug,
      name: data.name,
      archiveSlug: data.archiveSlug,
      description: data.description,
      contestTime: new Date(data.contestTime).getTime(),
    });
    setState(prevState => ({ ...prevState, isDialogOpen: false }));
  };

  return render();
}
