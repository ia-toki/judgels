import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { CourseChaptersResponse } from '../../../../modules/api/jerahmeel/courseChapter';

import './CourseChaptersTable.css';

export interface CourseChaptersTableProps {
  response: CourseChaptersResponse;
}

export class CourseChaptersTable extends React.PureComponent<CourseChaptersTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed course-chapters-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-alias">Alias</th>
          <th>Name</th>
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const { data, chaptersMap } = this.props.response;

    const rows = data.map(chapter => (
      <tr key={chapter.chapterJid}>
        <td>{chapter.alias}</td>
        <td>{chaptersMap[chapter.chapterJid] && chaptersMap[chapter.chapterJid].name}</td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
