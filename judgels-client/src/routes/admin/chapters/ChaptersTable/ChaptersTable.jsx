import { HTMLTable } from '@blueprintjs/core';
import { Edit, Manual, Presentation } from '@blueprintjs/icons';

import './ChaptersTable.scss';

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
          <Edit className="action" intent="primary" onClick={() => onEditChapter(chapter)} />
          <Presentation className="action" intent="primary" onClick={() => onEditChapterLessons(chapter)} />
          <Manual className="action" intent="primary" onClick={() => onEditChapterProblems(chapter)} />
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
