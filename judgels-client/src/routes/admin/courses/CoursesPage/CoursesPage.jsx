import { HTMLTable, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useQuery } from '@tanstack/react-query';
import { Link } from '@tanstack/react-router';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { coursesQueryOptions } from '../../../../modules/queries/course';

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
        <td className="col-fit">{course.id}</td>
        <td>
          <Link to={`/admin/courses/${course.slug}`}>{course.slug}</Link>
        </td>
        <td>{course.name}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th className="col-fit">ID</th>
            <th>Slug</th>
            <th>Name</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  const renderAction = () => {
    return (
      <ButtonLink small intent={Intent.PRIMARY} to="/admin/courses/-/new">
        <Plus />
        <span>New course</span>
      </ButtonLink>
    );
  };

  return (
    <ContentCard title="Courses" action={renderAction()}>
      {renderCourses()}
    </ContentCard>
  );
}
