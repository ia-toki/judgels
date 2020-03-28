import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';

import { Course } from '../../../../modules/api/jerahmeel/course';

import './CoursesTable.css';

export interface CoursesTableProps {
  courses: Course[];
  onEditCourse: (course: Course) => any;
}

export class CoursesTable extends React.PureComponent<CoursesTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed courses-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
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

  private renderRows = () => {
    const { courses } = this.props;

    const rows = courses.map(course => (
      <tr key={course.jid}>
        <td>{course.id}</td>
        <td>{course.slug}</td>
        <td>{course.name}</td>
        <td>
          <Icon className="action" icon="edit" intent="primary" onClick={() => this.props.onEditCourse(course)} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
