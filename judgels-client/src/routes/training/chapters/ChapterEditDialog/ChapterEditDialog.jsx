import { Button, Classes, Dialog } from '@blueprintjs/core';

import ChapterEditForm from '../ChapterEditForm/ChapterEditForm';

export function ChapterEditDialog({ chapter, isOpen, onCloseDialog, onUpdateChapter }) {
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
    await onUpdateChapter(chapter.jid, data);
  };

  const initialValues = chapter && {
    name: chapter.name,
  };
  const props = {
    renderFormComponents: renderDialogForm,
    onSubmit: updateChapter,
    initialValues,
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit chapter" canOutsideClickClose={false}>
        <ChapterEditForm {...props} />
      </Dialog>
    </div>
  );
}
