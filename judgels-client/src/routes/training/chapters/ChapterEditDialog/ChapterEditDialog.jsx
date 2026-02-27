import { Button, Classes, Dialog } from '@blueprintjs/core';
import { useMutation } from '@tanstack/react-query';

import { updateChapterMutationOptions } from '../../../../modules/queries/chapter';
import ChapterEditForm from '../ChapterEditForm/ChapterEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ChapterEditDialog({ chapter, isOpen, onCloseDialog }) {
  const updateChapterMutation = useMutation(updateChapterMutationOptions(chapter?.jid));

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

  const updateChapter = async data => {
    await updateChapterMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Chapter updated.');
      },
    });
    onCloseDialog();
  };

  const initialValues = chapter && {
    name: chapter.name,
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit chapter" canOutsideClickClose={false}>
        <ChapterEditForm
          renderFormComponents={renderDialogForm}
          onSubmit={updateChapter}
          initialValues={initialValues}
        />
      </Dialog>
    </div>
  );
}
