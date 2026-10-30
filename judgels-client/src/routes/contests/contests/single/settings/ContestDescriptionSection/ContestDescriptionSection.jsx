import { Button } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import {
  contestBySlugQueryOptions,
  contestDescriptionQueryOptions,
  updateContestDescriptionMutationOptions,
} from '../../../../../../modules/queries/contest';
import ContestDescriptionEditForm from '../ContestDescriptionEditForm/ContestDescriptionEditForm';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export default function ContestDescriptionSection() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: response } = useQuery(contestDescriptionQueryOptions(contest.jid));

  const updateDescriptionMutation = useMutation(updateContestDescriptionMutationOptions(contest.jid));

  const [isEditing, setIsEditing] = useState(false);

  const updateContestDescription = async data => {
    await updateDescriptionMutation.mutateAsync(data.description, {
      onSuccess: () => toastActions.showSuccessToast('Description updated.'),
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
    if (response === undefined) {
      return <LoadingState />;
    }
    if (isEditing) {
      return (
        <ContestDescriptionEditForm
          initialValues={{ description: response.description }}
          onSubmit={updateContestDescription}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    if (!response.description) {
      return (
        <p>
          <small>No description.</small>
        </p>
      );
    }
    return (
      <ContentCard compact>
        <HtmlText profilesMap={response.profilesMap}>{response.description}</HtmlText>
      </ContentCard>
    );
  };

  return (
    <div>
      <Flex asChild gap={2} alignItems="center">
        <h4>
          <span>Description</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
