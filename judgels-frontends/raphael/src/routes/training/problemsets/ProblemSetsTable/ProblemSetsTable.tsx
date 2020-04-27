import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';

import { ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';

import './ProblemSetsTable.css';

export interface ProblemSetsTableProps {
  problemSets: ProblemSet[];
  archiveSlugsMap: { [archiveJid: string]: string };
  onEditProblemSet: (problemSet: ProblemSet) => any;
  onEditProblemSetProblems: (problemSet: ProblemSet) => any;
}

export class ProblemSetsTable extends React.PureComponent<ProblemSetsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed problem-sets-table">
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
          <th className="col-slug">Slug</th>
          <th>Name</th>
          <th>Archive</th>
          <th className="col-actions" />
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const { problemSets, archiveSlugsMap } = this.props;

    const rows = problemSets.map(problemSet => (
      <tr key={problemSet.jid}>
        <td>{problemSet.id}</td>
        <td>{problemSet.slug}</td>
        <td>{problemSet.name}</td>
        <td>{archiveSlugsMap[problemSet.archiveJid]}</td>
        <td>
          <Icon
            className="action"
            icon="edit"
            intent="primary"
            onClick={() => this.props.onEditProblemSet(problemSet)}
          />
          <Icon
            className="action"
            icon="manual"
            intent="primary"
            onClick={() => this.props.onEditProblemSetProblems(problemSet)}
          />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
