import { Button, Callout, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useMemo, useState } from 'react';

import { setContestProblemsMutationOptions } from '../../../../../../modules/queries/contestProblem';
import ContestProblemEditForm from '../ContestProblemEditForm/ContestProblemEditForm';
import { getContestProblemEditor } from '../modules/editor/contestProblemEditorRegistry';

import * as toastActions from '../../../../../../modules/toast/toastActions';

import './ContestProblemEditDialog.scss';

export function ContestProblemEditDialog({ contest, problems }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const editor = useMemo(() => getContestProblemEditor(contest.style), [contest.style]);
  const setProblemsMutation = useMutation(setContestProblemsMutationOptions(contest.jid));

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
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
          <strong>Format:</strong> {editor.format}
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        {editor.example}
      </Callout>
    );
  };

  const setProblems = async data => {
    const deserializedProblems = editor.deserializer(data.problems);

    await setProblemsMutation.mutateAsync(deserializedProblems, {
      onSuccess: () => {
        toastActions.showSuccessToast('Problems updated.');
      },
      onSettled: () => {
        setIsDialogOpen(false);
      },
    });
  };

  const editProblems = editor.serializer(problems);
  const formProps = {
    renderFormComponents: renderDialogForm,
    validator: editor.validator,
    onSubmit: setProblems,
    initialValues: { problems: editProblems },
  };

  return (
    <div className="content-card__section">
      <Button
        className="contest-problem-set-dialog__button"
        intent={Intent.PRIMARY}
        icon={<Edit />}
        onClick={toggleDialog}
        disabled={isDialogOpen}
      >
        Edit problems
      </Button>
      <Dialog
        className="contest-problem-set-dialog"
        isOpen={isDialogOpen}
        onClose={toggleDialog}
        title="Edit problems"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <ContestProblemEditForm {...formProps} />
      </Dialog>
    </div>
  );
}
