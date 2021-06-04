import { HTMLTable, Icon } from '@blueprintjs/core';

import './ProblemSetsTable.scss';

export function ProblemSetsTable({ problemSets, archiveSlugsMap, onEditProblemSet, onEditProblemSetProblems }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-id">ID</th>
          <th className="col-slug">Slug</th>
          <th>Name</th>
          <th>Archive</th>
          <th className="col-actions" />
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = problemSets.map(problemSet => (
      <tr key={problemSet.jid}>
        <td>{problemSet.id}</td>
        <td>{problemSet.slug}</td>
        <td>{problemSet.name}</td>
        <td>{archiveSlugsMap[problemSet.archiveJid]}</td>
        <td>
          <Icon className="action" icon="edit" intent="primary" onClick={() => onEditProblemSet(problemSet)} />
          <Icon
            className="action"
            icon="manual"
            intent="primary"
            onClick={() => onEditProblemSetProblems(problemSet)}
          />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed problem-sets-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
