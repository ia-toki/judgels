import { Button, Callout, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useMemo, useState } from 'react';

import { setContestProblemsMutationOptions } from '../../../../../modules/queries/contestProblem';
import ContestProblemEditForm from '../ContestProblemEditForm/ContestProblemEditForm';
import { getContestProblemEditor } from '../modules/editor/contestProblemEditorRegistry';

import * as toastActions from '../../../../../modules/toast/toastActions';

export function ContestProblemEditDialog({ contest, problems }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const editor = useMemo(() => getContestProblemEditor(contest.style), [contest.style]);
  const setProblemsMutation = useMutation(setContestProblemsMutationOptions(contest.jid));

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>
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
        setIsDialogOpen(false);
        toastActions.showSuccessToast('Problems updated.');
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
    <div>
      <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={toggleDialog} disabled={isDialogOpen}>
        Edit problems
      </Button>
      <Dialog
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
