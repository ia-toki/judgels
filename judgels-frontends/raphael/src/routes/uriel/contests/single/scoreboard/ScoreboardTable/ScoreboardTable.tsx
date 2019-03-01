import { HTMLTable } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { ScoreboardState } from 'modules/api/uriel/scoreboard';

import './ScoreboardTable.css';
import { ContestStyle } from '../../../../../../modules/api/uriel/contest';

export class ScoreboardTableProps {
  className: string;
  state: ScoreboardState;
  children?: any;
  style?: ContestStyle;
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
    const PROBLEM_POINTS_STRING_LENGTH = 2;
    const alias = state.problemAliases[idx];
    let points;
    if (this.props.style === ContestStyle.Bundle) {
      points = (
        <>
          <br />[{Number(alias.substr(alias.length - PROBLEM_POINTS_STRING_LENGTH))}]
        </>
      );
    } else {
      points =
        state.problemPoints === undefined || state.problemPoints === null ? (
          ''
        ) : (
          <>
            <br />[{state.problemPoints![idx]}]
          </>
        );
    }
    return (
      <th key={alias} className="problem-cell">
        {alias}
        {points}
      </th>
    );
  };
}
