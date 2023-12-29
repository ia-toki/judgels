import { Button, Classes, Dialog } from '@blueprintjs/core';

import ProblemSetEditForm from '../ProblemSetEditForm/ProblemSetEditForm';

export function ProblemSetEditDialog({ isOpen, problemSet, archiveSlug, onCloseDialog, onUpdateProblemSet }) {
  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={onCloseDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const updateProblemSet = async data => {
    await onUpdateProblemSet(problemSet.jid, {
      slug: data.slug,
      name: data.name,
      archiveSlug: data.archiveSlug,
      description: data.description,
      contestTime: new Date(data.contestTime).getTime(),
    });
  };

  const initialValues = problemSet && {
    slug: problemSet.slug,
    name: problemSet.name,
    archiveSlug: archiveSlug,
    description: problemSet.description,
    contestTime: new Date(problemSet.contestTime).toISOString(),
  };
  const props = {
    renderFormComponents: renderDialogForm,
    onSubmit: updateProblemSet,
    initialValues,
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit problemset" canOutsideClickClose={false}>
        <ProblemSetEditForm {...props} />
      </Dialog>
    </div>
  );
}
