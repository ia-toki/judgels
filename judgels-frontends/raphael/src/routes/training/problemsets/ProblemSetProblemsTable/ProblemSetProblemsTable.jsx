import { HTMLTable } from '@blueprintjs/core';

import './ProblemSetProblemsTable.css';

export function ProblemSetProblemsTable({ response: { data, problemsMap, contestsMap } }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-alias">Alias</th>
          <th>Slug</th>
          <th>Contest</th>
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = data.map(problem => (
      <tr key={problem.problemJid}>
        <td>{problem.alias}</td>
        <td>{problemsMap[problem.problemJid] && problemsMap[problem.problemJid].slug}</td>
        <td>
          {problem.contestJids
            .map(jid => contestsMap[jid])
            .filter(c => c)
            .map(c => c.slug)
            .join(';')}
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed problem-set-problems-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
