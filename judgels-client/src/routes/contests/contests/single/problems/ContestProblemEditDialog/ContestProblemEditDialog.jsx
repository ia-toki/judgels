import { Button, Callout, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import classNames from 'classnames';
import { useEffect, useState } from 'react';

import ContestProblemEditForm from '../ContestProblemEditForm/ContestProblemEditForm';
import { getContestProblemEditor } from '../modules/editor/contestProblemEditorRegistry';

import './ContestProblemEditDialog.scss';

export function ContestProblemEditDialog({ contest, problems, onSetProblems }) {
  const [state, setState] = useState({
    isDialogOpen: false,
    editor: getContestProblemEditor(contest.style),
  });

  const render = () => {
    return (
      <div className="content-card__section">
        {renderButton()}
        {renderDialog()}
      </div>
    );
  };

  useEffect(() => {
    setState(prevState => ({ ...prevState, editor: getContestProblemEditor(contest.style) }));
  }, [contest.style]);

  const renderButton = () => {
    return (
      <Button
        className="contest-problem-set-dialog__button"
        intent={Intent.PRIMARY}
        icon={<Edit />}
        onClick={toggleDialog}
        disabled={state.isDialogOpen}
      >
        Edit problems
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen }));
  };

  const renderDialog = () => {
    return (
      <Dialog
        className="contest-problem-set-dialog"
        isOpen={state.isDialogOpen}
        onClose={toggleDialog}
        title="Edit problems"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {renderDialogSetForm()}
      </Dialog>
    );
  };

  const renderDialogSetForm = () => {
    const editProblems = state.editor.serializer(problems);
    const props = {
      renderFormComponents: renderDialogForm,
      validator: state.editor.validator,
      onSubmit: setProblems,
      initialValues: { problems: editProblems },
    };
    return <ContestProblemEditForm {...props} />;
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-problem-edit-dialog__body')}>
        {fields}
        {renderInstructions()}
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const renderInstructions = () => {
    return (
      <Callout icon={null}>
        <p>
          <strong>Format:</strong> {state.editor.format}
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        {state.editor.example}
      </Callout>
    );
  };

  const setProblems = async data => {
    const deserializedProblems = state.editor.deserializer(data.problems);

    await onSetProblems(contest.jid, deserializedProblems);
    setState(prevState => ({ ...prevState, isDialogOpen: false }));
  };

  return render();
}
