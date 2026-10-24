import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { createCourseMutationOptions } from '../../../../modules/queries/course';
import CourseCreateForm from '../CourseCreateForm/CourseCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function CourseCreatePage() {
  const navigate = useNavigate();

  const createCourseMutation = useMutation(createCourseMutationOptions);

  const createCourse = async data => {
    await createCourseMutation.mutateAsync(data, {
      onSuccess: (_data, { slug }) => {
        navigate({ to: `/admin/courses/${slug}` });
        toastActions.showSuccessToast('Course created.');
      },
    });
  };

  return (
    <ContentCard title={<BreadcrumbTitle parent="Courses" current="New" />}>
      <CourseCreateForm onSubmit={createCourse} onCancel={() => navigate({ to: '/admin/courses' })} />
    </ContentCard>
  );
}
