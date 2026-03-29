import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { updateProblemSetMutationOptions } from '../../../../modules/queries/problemSet';
import ProblemSetGeneralEditForm from '../ProblemSetGeneralEditForm/ProblemSetGeneralEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ProblemSetGeneralSection({ problemSet, archiveSlug }) {
  const updateProblemSetMutation = useMutation(updateProblemSetMutationOptions(problemSet.jid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const detailRows = [
    { key: 'slug', title: 'Slug', value: problemSet.slug },
    { key: 'name', title: 'Name', value: problemSet.name },
    { key: 'archiveSlug', title: 'Archive slug', value: archiveSlug },
    { key: 'contestTime', title: 'Contest time', value: new Date(problemSet.contestTime).toISOString() },
    { key: 'description', title: 'Description', value: problemSet.description },
  ];

  const updateProblemSet = async data => {
    await updateProblemSetMutation.mutateAsync(
      {
        slug: data.slug,
        name: data.name,
        archiveSlug: data.archiveSlug,
        description: data.description,
        contestTime: new Date(data.contestTime).getTime(),
      },
      {
        onSuccess: () => toastActions.showSuccessToast('Problemset updated.'),
      }
    );
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
        slug: problemSet.slug || '',
        name: problemSet.name || '',
        archiveSlug: archiveSlug || '',
        description: problemSet.description || '',
        contestTime: new Date(problemSet.contestTime).toISOString(),
      };
      return (
        <ProblemSetGeneralEditForm
          initialValues={initialValues}
          onSubmit={updateProblemSet}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={detailRows} />;
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
