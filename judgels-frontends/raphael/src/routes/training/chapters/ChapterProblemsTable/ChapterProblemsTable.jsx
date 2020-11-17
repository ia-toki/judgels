import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { ChapterProblemsResponse } from '../../../../modules/api/jerahmeel/chapterProblem';

import './ChapterProblemsTable.css';

export interface ChapterProblemsTableProps {
  response: ChapterProblemsResponse;
}

export class ChapterProblemsTable extends React.PureComponent<ChapterProblemsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed chapter-problems-table">
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
