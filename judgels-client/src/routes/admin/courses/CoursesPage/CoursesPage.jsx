import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { Link } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { coursesQueryOptions } from '../../../../modules/queries/course';
import { CourseCreateDialog } from '../CourseCreateDialog/CourseCreateDialog';

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

    const rows = courses.map(course => (
      <tr key={course.jid}>
        <td style={{ width: '60px' }}>{course.id}</td>
        <td style={{ width: '200px' }}>
          <Link to={`/admin/courses/${course.slug}`}>{course.slug}</Link>
        </td>
        <td>{course.name}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th style={{ width: '200px' }}>Slug</th>
            <th>Name</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
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
