import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { createContestMutationOptions } from '../../../../modules/queries/contest';
import ContestCreateForm from '../ContestCreateForm/ContestCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ContestCreatePage() {
  const navigate = useNavigate();

  const createContestMutation = useMutation(createContestMutationOptions());

  const createContest = async data => {
    await createContestMutation.mutateAsync(data, {
      onSuccess: (_data, { slug }) => {
        navigate({ to: `/contests/${slug}/settings/general` });
        toastActions.showSuccessToast('Contest created.');
      },
    });
  };

  return (
    <ContentCard
      title={
        <BreadcrumbTitle
          items={[
            { text: 'Contests', to: '/admin/contests' },
            { text: 'New', to: '/admin/contests/-/new' },
          ]}
        />
      }
    >
      <ContestCreateForm
        onSubmit={createContest}
        onCancel={() => navigate({ to: '/admin/contests', ignoreBlocker: true })}
      />
    </ContentCard>
  );
}
