import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';

import { Chapter } from '../../../../modules/api/jerahmeel/chapter';

import './ChaptersTable.css';

export interface ChaptersTableProps {
  chapters: Chapter[];
  onEditChapter: (chapter: Chapter) => any;
  onEditChapterLessons: (chapter: Chapter) => any;
  onEditChapterProblems: (chapter: Chapter) => any;
}

export class ChaptersTable extends React.PureComponent<ChaptersTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed chapters-table">
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
          <th className="col-jid">JID</th>
          <th>Name</th>
          <th className="col-actions" />
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const { chapters } = this.props;

    const rows = chapters.map(chapter => (
      <tr key={chapter.jid}>
        <td>{chapter.id}</td>
        <td>{chapter.jid}</td>
        <td>{chapter.name}</td>
        <td>
          <Icon className="action" icon="edit" intent="primary" onClick={() => this.props.onEditChapter(chapter)} />
          <Icon
            className="action"
            icon="presentation"
            intent="primary"
            onClick={() => this.props.onEditChapterLessons(chapter)}
          />
          <Icon
            className="action"
            icon="manual"
            intent="primary"
            onClick={() => this.props.onEditChapterProblems(chapter)}
          />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
