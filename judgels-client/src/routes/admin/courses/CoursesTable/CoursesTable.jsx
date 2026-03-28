import { HTMLTable } from '@blueprintjs/core';
import { Link } from '@tanstack/react-router';

import './CoursesTable.scss';

export function CoursesTable({ courses }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-id">ID</th>
          <th className="col-slug">Slug</th>
          <th>Name</th>
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = courses.map(course => (
      <tr key={course.jid}>
        <td>{course.id}</td>
        <td>
          <Link to={`/admin/courses/${course.slug}`}>{course.slug}</Link>
        </td>
        <td>{course.name}</td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed courses-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
