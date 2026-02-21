import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
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
import ContestEditDescriptionForm from '../ContestEditDescriptionForm/ContestEditDescriptionForm';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export default function ContestEditDescriptionTab() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: response } = useQuery(contestDescriptionQueryOptions(contest.jid));

  const updateDescriptionMutation = useMutation(updateContestDescriptionMutationOptions(contest.jid));

  const [isEditing, setIsEditing] = useState(false);

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button
          small
          className="right-action-button"
          intent={Intent.PRIMARY}
          icon={<Edit />}
          onClick={() => setIsEditing(true)}
        >
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
      const initialValues = {
        description: response.description,
      };
      const formProps = {
        onCancel: () => setIsEditing(false),
      };
      return (
        <ContestEditDescriptionForm initialValues={initialValues} onSubmit={updateContestDescription} {...formProps} />
      );
    }
    return renderDescription(response);
  };

  const renderDescription = ({ description, profilesMap }) => {
    if (!description) {
      return (
        <p>
          <small>No description.</small>
        </p>
      );
    }
    return (
      <ContentCard className="contest-edit-dialog__content">
        <HtmlText profilesMap={profilesMap}>{description}</HtmlText>
      </ContentCard>
    );
  };

  const updateContestDescription = async data => {
    await updateDescriptionMutation.mutateAsync(data.description, {
      onSuccess: () => toastActions.showSuccessToast('Description updated.'),
    });
    setIsEditing(false);
  };

  return (
    <>
      <h4>
        Description settings
        {renderEditButton()}
      </h4>
      <hr />
      {renderContent()}
    </>
  );
}
