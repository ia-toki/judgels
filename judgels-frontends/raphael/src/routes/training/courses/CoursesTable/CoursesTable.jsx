import { HTMLTable, Icon } from '@blueprintjs/core';

import './CoursesTable.css';

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
          <Icon className="action" icon="edit" intent="primary" onClick={() => onEditCourse(course)} />
          <Icon className="action" icon="properties" intent="primary" onClick={() => onEditCourseChapters(course)} />
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
