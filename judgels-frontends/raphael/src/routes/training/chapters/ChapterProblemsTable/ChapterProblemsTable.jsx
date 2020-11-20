import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import './ChapterProblemsTable.css';

export function ChapterProblemsTable({ response: { data, problemsMap } }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-alias">Alias</th>
          <th>Slug</th>
          <th className="col-type">Type</th>
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = data.map(problem => (
      <tr key={problem.problemJid}>
        <td>{problem.alias}</td>
        <td>{problemsMap[problem.problemJid] && problemsMap[problem.problemJid].slug}</td>
        <td>{problem.type}</td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed chapter-problems-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
