import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { createProblemSetMutationOptions } from '../../../../modules/queries/problemSet';
import ProblemSetCreateForm from '../ProblemSetCreateForm/ProblemSetCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemSetCreatePage() {
  const navigate = useNavigate();

  const createProblemSetMutation = useMutation(createProblemSetMutationOptions);

  // Computed once, so that the form does not reset whenever the page re-renders.
  const [initialValues] = useState(() => ({
    contestTime: new Date().toISOString(),
  }));

  const createProblemSet = async data => {
    await createProblemSetMutation.mutateAsync(
      {
        slug: data.slug,
        name: data.name,
        archiveSlug: data.archiveSlug,
        description: data.description,
        contestTime: new Date(data.contestTime).getTime(),
      },
      {
        onSuccess: (_data, { slug }) => {
          navigate({ to: `/admin/problemsets/${slug}` });
          toastActions.showSuccessToast('Problemset created.');
        },
      }
    );
  };

  return (
    <ContentCard title={<BreadcrumbTitle parent="Problemsets" current="New" />}>
      <ProblemSetCreateForm
        onSubmit={createProblemSet}
        onCancel={() => navigate({ to: '/admin/problemsets' })}
        initialValues={initialValues}
      />
    </ContentCard>
  );
}
