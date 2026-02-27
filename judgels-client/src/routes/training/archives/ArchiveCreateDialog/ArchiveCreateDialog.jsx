import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { createArchiveMutationOptions } from '../../../../modules/queries/archive';
import ArchiveCreateForm from '../ArchiveCreateForm/ArchiveCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ArchiveCreateDialog() {
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const createArchiveMutation = useMutation(createArchiveMutationOptions);

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
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

  const createArchive = async data => {
    await createArchiveMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Archive created.');
      },
    });
    setIsDialogOpen(false);
  };

  return (
    <div className="content-card__section">
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New archive
      </Button>
      <Dialog isOpen={isDialogOpen} onClose={toggleDialog} title="Create new archive" canOutsideClickClose={false}>
        <ArchiveCreateForm renderFormComponents={renderDialogForm} onSubmit={createArchive} />
      </Dialog>
    </div>
  );
}
