import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { updateCurriculumMutationOptions } from '../../../../modules/queries/curriculum';
import CurriculumGeneralEditForm from '../CurriculumGeneralEditForm/CurriculumGeneralEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function CurriculumGeneralSection({ curriculum }) {
  const updateCurriculumMutation = useMutation(updateCurriculumMutationOptions(curriculum.jid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const rows = [
    { key: 'name', title: 'Name', value: curriculum.name },
    { key: 'description', title: 'Description', value: curriculum.description },
  ];

  const updateCurriculum = async data => {
    await updateCurriculumMutation.mutateAsync(data, {
      onSuccess: () => toastActions.showSuccessToast('Curriculum updated.'),
    });
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        name: curriculum.name || '',
        description: curriculum.description || '',
      };
      return (
        <CurriculumGeneralEditForm
          initialValues={initialValues}
          onSubmit={updateCurriculum}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>General</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
