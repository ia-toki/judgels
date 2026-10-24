import { Button, Intent, Tag } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { getDisplayedTopicTags } from '../../../../components/ProblemTopicTags/ProblemTopicTags';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import {
  problemTopicTagsQueryOptions,
  setProblemTopicTagsMutationOptions,
} from '../../../../modules/queries/problemTag';
import ProblemTopicTagsEditForm from '../ProblemTopicTagsEditForm/ProblemTopicTagsEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemTopicTagsPanel({ problemJid }) {
  const { data: topicTags } = useSuspenseQuery(problemTopicTagsQueryOptions(problemJid));

  const setTopicTagsMutation = useMutation(setProblemTopicTagsMutationOptions(problemJid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const rows = [
    {
      key: 'topicTags',
      title: 'Tags',
      value: (
        <Flex gap={1} flexWrap="wrap">
          {getDisplayedTopicTags(topicTags).map(tag => (
            <Tag key={tag} round multiline intent={Intent.PRIMARY}>
              {tag}
            </Tag>
          ))}
        </Flex>
      ),
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
      !isEditing && (
        <Button small variant="outlined" icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = { topicTags };
      return (
        <ProblemTopicTagsEditForm
          initialValues={initialValues}
          onSubmit={setTopicTags}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <div>
      <Flex asChild gap={2} alignItems="center">
        <h4>
          <span>Tags</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
