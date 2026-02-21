import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { contestBySlugQueryOptions, updateContestMutationOptions } from '../../../../../../modules/queries/contest';
import { formatDuration, parseDuration } from '../../../../../../utils/duration';
import ContestEditGeneralForm from '../ContestEditGeneralForm/ContestEditGeneralForm';
import { ContestEditGeneralTable } from '../ContestEditGeneralTable/ContestEditGeneralTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export default function ContestEditGeneralTab() {
  const navigate = useNavigate();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const updateContestMutation = useMutation(updateContestMutationOptions(contest.jid, contestSlug));

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
    if (isEditing) {
      const initialValues = {
        slug: contest.slug,
        name: contest.name,
        style: contest.style,
        beginTime: new Date(contest.beginTime).toISOString(),
        duration: formatDuration(contest.duration),
      };
      const formProps = {
        onCancel: () => setIsEditing(false),
      };
      return <ContestEditGeneralForm initialValues={initialValues} onSubmit={updateContest} {...formProps} />;
    }
    return <ContestEditGeneralTable contest={contest} />;
  };

  const updateContest = async data => {
    const updateData = {
      slug: data.slug,
      name: data.name,
      style: data.style,
      beginTime: new Date(data.beginTime).getTime(),
      duration: parseDuration(data.duration),
    };
    await updateContestMutation.mutateAsync(updateData, {
      onSuccess: () => toastActions.showSuccessToast('Contest updated.'),
    });
    setIsEditing(false);

    if (updateData.slug && updateData.slug !== contestSlug) {
      navigate({ to: `/contests/${updateData.slug}` });
    }
  };

  return (
    <>
      <h4>
        General settings
        {renderEditButton()}
      </h4>
      <hr />
      {renderContent()}
    </>
  );
}
