import { HTMLTable } from '@blueprintjs/core';

import './CourseChaptersTable.scss';

export function CourseChaptersTable({ response: { data, chaptersMap } }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-alias">Alias</th>
          <th>Name</th>
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = data.map(chapter => (
      <tr key={chapter.chapterJid}>
        <td>{chapter.alias}</td>
        <td>{chaptersMap[chapter.chapterJid] && chaptersMap[chapter.chapterJid].name}</td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed course-chapters-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
