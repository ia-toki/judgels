import { HTMLTable } from '@blueprintjs/core';
import { Edit, Properties } from '@blueprintjs/icons';

import './CoursesTable.scss';

export function CoursesTable({ courses, onEditCourse, onEditCourseChapters }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-id">ID</th>
          <th className="col-slug">Slug</th>
          <th>Name</th>
          <th className="col-actions" />
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = courses.map(course => (
      <tr key={course.jid}>
        <td>{course.id}</td>
        <td>{course.slug}</td>
        <td>{course.name}</td>
        <td>
          <Edit className="action" intent="primary" onClick={() => onEditCourse(course)} />
          <Properties className="action" intent="primary" onClick={() => onEditCourseChapters(course)} />
        </td>
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
