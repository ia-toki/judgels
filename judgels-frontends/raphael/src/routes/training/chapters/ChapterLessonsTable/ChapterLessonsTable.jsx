import { HTMLTable } from '@blueprintjs/core';

import './ChapterLessonsTable.scss';

export function ChapterLessonsTable({ response: { data, lessonsMap } }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-alias">Alias</th>
          <th>Slug</th>
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = data.map(lesson => (
      <tr key={lesson.lessonJid}>
        <td>{lesson.alias}</td>
        <td>{lessonsMap[lesson.lessonJid] && lessonsMap[lesson.lessonJid].slug}</td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed chapter-lessons-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
