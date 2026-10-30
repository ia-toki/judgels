import { Button } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';

import { useEditableSection } from '../../../../../../components/EditableSections/EditableSections';
import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { FormTable } from '../../../../../../components/forms/FormTable/FormTable';
import { contestBySlugQueryOptions, updateContestMutationOptions } from '../../../../../../modules/queries/contest';
import { formatDateTime } from '../../../../../../utils/datetime';
import { formatDuration, parseDuration } from '../../../../../../utils/duration';
import ContestGeneralEditForm from '../ContestGeneralEditForm/ContestGeneralEditForm';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export default function ContestGeneralSection() {
  const navigate = useNavigate();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const updateContestMutation = useMutation(updateContestMutationOptions(contest.jid, contestSlug));

  const { isEditing, isEditDisabled, setIsEditing } = useEditableSection();

  const keyStyles = { width: '250px' };

  const rows = [
    { key: 'jid', title: 'JID', value: contest.jid },
    { key: 'slug', title: 'Slug', value: contest.slug },
    { key: 'name', title: 'Name', value: contest.name },
    { key: 'style', title: 'Style', value: contest.style },
    { key: 'beginTime', title: 'Begin time', value: formatDateTime(new Date(contest.beginTime), true) },
    { key: 'duration', title: 'Duration', value: <FormattedDuration value={contest.duration} /> },
  ];

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
    if (updateData.slug !== contestSlug) {
      navigate({ to: `/contests/${updateData.slug}/settings/general` });
    }
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
        slug: contest.slug,
        name: contest.name,
        style: contest.style,
        beginTime: new Date(contest.beginTime).toISOString(),
        duration: formatDuration(contest.duration),
      };
      return (
        <ContestGeneralEditForm
          initialValues={initialValues}
          onSubmit={updateContest}
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
