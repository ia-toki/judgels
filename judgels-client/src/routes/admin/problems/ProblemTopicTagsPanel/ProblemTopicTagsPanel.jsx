import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import {
  problemTopicTagsQueryOptions,
  setProblemTopicTagsMutationOptions,
} from '../../../../modules/queries/problemTag';
import ProblemTopicTagsEditForm from '../ProblemTopicTagsEditForm/ProblemTopicTagsEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemTopicTagsPanel({ problemJid, canEdit }) {
  const { data: topicTags } = useSuspenseQuery(problemTopicTagsQueryOptions(problemJid));

  const setTopicTagsMutation = useMutation(setProblemTopicTagsMutationOptions(problemJid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const rows = [
    {
      key: 'topicTags',
      title: 'Tags',
      value: topicTags
        .slice()
        .sort()
        .map(tag => tag.substring('topic-'.length))
        .join(', '),
    },
  ];

  const setTopicTags = async data => {
    await setTopicTagsMutation.mutateAsync(data.topicTags || [], {
      onSuccess: () => toastActions.showSuccessToast('Tags updated.'),
    });
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      canEdit &&
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      return (
        <ProblemTopicTagsEditForm
          initialValues={{ topicTags }}
          onSubmit={setTopicTags}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <section aria-label="Tags">
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Tags</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </section>
  );
}
