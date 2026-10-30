import { Button } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';

import { useEditableSection } from '../../../../components/EditableSections/EditableSections';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { updateChapterMutationOptions } from '../../../../modules/queries/chapter';
import ChapterGeneralEditForm from '../ChapterGeneralEditForm/ChapterGeneralEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ChapterGeneralSection({ chapter }) {
  const updateChapterMutation = useMutation(updateChapterMutationOptions(chapter.jid));

  const { isEditing, isEditDisabled, setIsEditing } = useEditableSection();

  const keyStyles = { width: '250px' };

  const rows = [{ key: 'name', title: 'Name', value: chapter.name }];

  const updateChapter = async data => {
    await updateChapterMutation.mutateAsync(data, {
      onSuccess: () => toastActions.showSuccessToast('Chapter updated.'),
    });
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small variant="outlined" icon={<Edit />} disabled={isEditDisabled} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        name: chapter.name || '',
      };
      return (
        <ChapterGeneralEditForm
          initialValues={initialValues}
          onSubmit={updateChapter}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="center" gap={2}>
        <h4>
          <span>General</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
