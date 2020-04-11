import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { ChapterLessonsResponse } from '../../../../modules/api/jerahmeel/chapterLesson';

import './ChapterLessonsTable.css';

export interface ChapterLessonsTableProps {
  response: ChapterLessonsResponse;
}

export class ChapterLessonsTable extends React.PureComponent<ChapterLessonsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed chapter-lessons-table">
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
          <th>Slug</th>
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const { data, lessonsMap } = this.props.response;

    const rows = data.map(lesson => (
      <tr key={lesson.lessonJid}>
        <td>{lesson.alias}</td>
        <td>{lessonsMap[lesson.lessonJid] && lessonsMap[lesson.lessonJid].slug}</td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
