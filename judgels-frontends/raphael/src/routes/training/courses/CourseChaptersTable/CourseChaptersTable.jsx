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
    const rows = data.map(courseChapter => (
      <tr key={courseChapter.chapterJid}>
        <td>{courseChapter.alias}</td>
        <td>{chaptersMap[courseChapter.chapterJid] && chaptersMap[courseChapter.chapterJid].name}</td>
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
