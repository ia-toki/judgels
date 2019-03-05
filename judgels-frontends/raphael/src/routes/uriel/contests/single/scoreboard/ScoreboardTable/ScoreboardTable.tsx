import { HTMLTable } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { ScoreboardState } from 'modules/api/uriel/scoreboard';

import './ScoreboardTable.css';

export class ScoreboardTableProps {
  className: string;
  state: ScoreboardState;
  children?: any;
}

export class ScoreboardTable extends React.PureComponent<ScoreboardTableProps> {
  render() {
    return (
      <HTMLTable striped className={classNames('scoreboard__content', this.props.className)}>
        {this.renderHeader(this.props.state)}
        {this.props.children}
      </HTMLTable>
    );
  }

  private renderHeader = (state: ScoreboardState) => {
    return (
      <thead>
        <tr>
          <th className="rank-cell">#</th>
          <th className="contestant-cell">Contestant</th>
          <th className="problem-cell">Total</th>
          {state.problemAliases.map((alias, idx) => this.renderProblemHeader(state, idx))}
        </tr>
      </thead>
    );
  };

  private renderProblemHeader = (state: ScoreboardState, idx: number) => {
    const alias = state.problemAliases[idx];
    const points =
      state.problemPoints === undefined || state.problemPoints === null ? (
        ''
      ) : (
        <>
          <br />[{state.problemPoints![idx]}]
        </>
      );
    return (
      <th key={alias} className="problem-cell">
        {alias}
        {points}
      </th>
    );
  };
}
