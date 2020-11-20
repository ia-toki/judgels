import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';

import './ChaptersTable.css';

export function ChaptersTable({ chapters, onEditChapter, onEditChapterLessons, onEditChapterProblems }) {
  const renderHeader = () => {
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

  const renderRows = () => {
    const rows = chapters.map(chapter => (
      <tr key={chapter.jid}>
        <td>{chapter.id}</td>
        <td>{chapter.jid}</td>
        <td>{chapter.name}</td>
        <td>
          <Icon className="action" icon="edit" intent="primary" onClick={() => onEditChapter(chapter)} />
          <Icon className="action" icon="presentation" intent="primary" onClick={() => onEditChapterLessons(chapter)} />
          <Icon className="action" icon="manual" intent="primary" onClick={() => onEditChapterProblems(chapter)} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed chapters-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
