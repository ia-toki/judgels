import { useQuery } from '@tanstack/react-query';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { coursesQueryOptions } from '../../../../modules/queries/course';
import { CourseCreateDialog } from '../CourseCreateDialog/CourseCreateDialog';
import { CoursesTable } from '../CoursesTable/CoursesTable';

export default function CoursesPage() {
  const { data: response } = useQuery(coursesQueryOptions());

  const renderCourses = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: courses } = response;
    if (courses.length === 0) {
      return (
        <p>
          <small>No courses.</small>
        </p>
      );
    }

    return <CoursesTable courses={courses} />;
  };

  const renderAction = () => {
    return (
      <ActionButtons>
        <CourseCreateDialog />
      </ActionButtons>
    );
  };

  return (
    <ContentCard title="Courses">
      {renderAction()}
      {renderCourses()}
    </ContentCard>
  );
}
