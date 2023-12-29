import { Button, Classes, Dialog } from '@blueprintjs/core';

import ArchiveEditForm from '../ArchiveEditForm/ArchiveEditForm';

export function ArchiveEditDialog({ archive, isOpen, onCloseDialog, onUpdateArchive }) {
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

  const updateArchive = async data => {
    await onUpdateArchive(archive.jid, data);
  };

  const initialValues = archive && {
    slug: archive.slug,
    name: archive.name,
    category: archive.category,
    description: archive.description,
  };
  const props = {
    renderFormComponents: renderDialogForm,
    onSubmit: updateArchive,
    initialValues,
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit archive" canOutsideClickClose={false}>
        <ArchiveEditForm {...props} />
      </Dialog>
    </div>
  );
}
