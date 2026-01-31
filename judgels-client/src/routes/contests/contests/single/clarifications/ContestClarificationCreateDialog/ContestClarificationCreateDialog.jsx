import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useState } from 'react';

import ContestClarificationCreateForm from '../ContestClarificationCreateForm/ContestClarificationCreateForm';

export function ContestClarificationCreateDialog({
  contest,
  problemJids,
  problemAliasesMap,
  problemNamesMap,
  onCreateClarification,
}) {
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
        New clarification
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen }));
  };

  const renderDialog = () => {
    const props = {
      contestJid: contest.jid,
      problemJids,
      problemAliasesMap,
      problemNamesMap,
      renderFormComponents: renderDialogForm,
      onSubmit: createClarification,
      initialValues: {
        topicJid: contest.jid,
      },
    };
    return (
      <Dialog
        isOpen={state.isDialogOpen}
        onClose={toggleDialog}
        title="Submit new clarification"
        canOutsideClickClose={false}
      >
        <ContestClarificationCreateForm {...props} />
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

  const createClarification = async data => {
    await onCreateClarification(contest.jid, data);
    setState(prevState => ({ ...prevState, isDialogOpen: false }));
  };

  return render();
}
