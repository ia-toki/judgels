import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { createLessonMutationOptions } from '../../../../modules/queries/lesson';
import LessonCreateForm from '../LessonCreateForm/LessonCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function LessonCreatePage() {
  const navigate = useNavigate();

  const createLessonMutation = useMutation(createLessonMutationOptions);

  const createLesson = async data => {
    await createLessonMutation.mutateAsync(
      { ...data, additionalNote: data.additionalNote || '' },
      {
        onSuccess: lesson => {
          navigate({ to: `/admin/lessons/${lesson.slug}` });
          toastActions.showSuccessToast('Lesson created.');
        },
      }
    );
  };

  return (
    <ContentCard title={<BreadcrumbTitle parent="Lessons" current="New" />}>
      <LessonCreateForm onSubmit={createLesson} onCancel={() => navigate({ to: '/admin/lessons' })} />
    </ContentCard>
  );
}
