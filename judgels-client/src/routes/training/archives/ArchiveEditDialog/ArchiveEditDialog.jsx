import { Button, Classes, Dialog } from '@blueprintjs/core';
import { useMutation } from '@tanstack/react-query';

import { updateArchiveMutationOptions } from '../../../../modules/queries/archive';
import ArchiveEditForm from '../ArchiveEditForm/ArchiveEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ArchiveEditDialog({ archive, isOpen, onCloseDialog }) {
  const updateArchiveMutation = useMutation(updateArchiveMutationOptions(archive?.jid));

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
    await updateArchiveMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Archive updated.');
      },
    });
    onCloseDialog();
  };

  const initialValues = archive && {
    slug: archive.slug,
    name: archive.name,
    category: archive.category,
    description: archive.description,
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit archive" canOutsideClickClose={false}>
        <ArchiveEditForm
          renderFormComponents={renderDialogForm}
          onSubmit={updateArchive}
          initialValues={initialValues}
        />
      </Dialog>
    </div>
  );
}
