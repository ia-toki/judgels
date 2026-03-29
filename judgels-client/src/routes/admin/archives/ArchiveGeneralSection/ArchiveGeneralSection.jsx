import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { updateArchiveMutationOptions } from '../../../../modules/queries/archive';
import ArchiveGeneralEditForm from '../ArchiveGeneralEditForm/ArchiveGeneralEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ArchiveGeneralSection({ archive }) {
  const updateArchiveMutation = useMutation(updateArchiveMutationOptions(archive.jid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const rows = [
    { key: 'slug', title: 'Slug', value: archive.slug },
    { key: 'name', title: 'Name', value: archive.name },
    { key: 'category', title: 'Category', value: archive.category },
    { key: 'description', title: 'Description', value: archive.description },
  ];

  const updateArchive = async data => {
    await updateArchiveMutation.mutateAsync(data, {
      onSuccess: () => toastActions.showSuccessToast('Archive updated.'),
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
        slug: archive.slug || '',
        name: archive.name || '',
        category: archive.category || '',
        description: archive.description || '',
      };
      return (
        <ArchiveGeneralEditForm
          initialValues={initialValues}
          onSubmit={updateArchive}
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
