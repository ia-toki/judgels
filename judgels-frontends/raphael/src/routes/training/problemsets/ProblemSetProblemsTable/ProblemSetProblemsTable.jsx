import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { ProblemSetProblemsResponse } from '../../../../modules/api/jerahmeel/problemSetProblem';

import './ProblemSetProblemsTable.css';

export interface ProblemSetProblemsTableProps {
  response: ProblemSetProblemsResponse;
}

export class ProblemSetProblemsTable extends React.PureComponent<ProblemSetProblemsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed problem-set-problems-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
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

  private renderRows = () => {
    const { data, problemsMap } = this.props.response;

    const rows = data.map(problem => (
      <tr key={problem.problemJid}>
        <td>{problem.alias}</td>
        <td>{problemsMap[problem.problemJid] && problemsMap[problem.problemJid].slug}</td>
        <td>{problem.type}</td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
