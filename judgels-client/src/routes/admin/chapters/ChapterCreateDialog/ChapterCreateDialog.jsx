import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';

import { createChapterMutationOptions } from '../../../../modules/queries/chapter';
import ChapterCreateForm from '../ChapterCreateForm/ChapterCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ChapterCreateDialog() {
  const navigate = useNavigate();
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const createChapterMutation = useMutation(createChapterMutationOptions);

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

  const createChapter = async data => {
    await createChapterMutation.mutateAsync(data, {
      onSuccess: chapter => {
        setIsDialogOpen(false);
        navigate({ to: `/admin/chapters/${chapter.jid}` });
        toastActions.showSuccessToast('Chapter created.');
      },
    });
  };

  return (
    <>
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New chapter
      </Button>
      <Dialog isOpen={isDialogOpen} onClose={toggleDialog} title="Create new chapter" canOutsideClickClose={false}>
        <ChapterCreateForm renderFormComponents={renderDialogForm} onSubmit={createChapter} />
      </Dialog>
    </>
  );
}
