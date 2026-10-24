import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { createProblemMutationOptions } from '../../../../modules/queries/problem';
import ProblemCreateForm from '../ProblemCreateForm/ProblemCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemCreatePage() {
  const navigate = useNavigate();

  const createProblemMutation = useMutation(createProblemMutationOptions);

  const createProblem = async data => {
    await createProblemMutation.mutateAsync(
      { ...data, additionalNote: data.additionalNote || '' },
      {
        onSuccess: problem => {
          navigate({ to: `/admin/problems/${problem.slug}` });
          toastActions.showSuccessToast('Problem created.');
        },
      }
    );
  };

  return (
    <ContentCard title={<BreadcrumbTitle parent="Problems" current="New" />}>
      <ProblemCreateForm onSubmit={createProblem} onCancel={() => navigate({ to: '/admin/problems' })} />
    </ContentCard>
  );
}
