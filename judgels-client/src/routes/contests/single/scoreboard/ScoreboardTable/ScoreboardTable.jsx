import { HTMLTable } from '@blueprintjs/core';
import classNames from 'classnames';

import './ScoreboardTable.scss';

export function ScoreboardTable({ className, state, children }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="rank-cell">#</th>
          <th className="contestant-cell">Contestant</th>
          <th className="problem-cell">Total</th>
          {state.problemAliases.map((alias, idx) => renderProblemHeader(idx))}
        </tr>
      </thead>
    );
  };

  const renderProblemHeader = idx => {
    const alias = state.problemAliases[idx];
    const points =
      state.problemPoints === undefined || state.problemPoints === null ? (
        ''
      ) : (
        <span className="bottom">{state.problemPoints[idx]}</span>
      );
    return (
      <th key={alias} className="problem-cell">
        {alias}
        {points}
      </th>
    );
  };

  return (
    <HTMLTable striped className={classNames('scoreboard__content', className)}>
      {renderHeader(state)}
      {children}
    </HTMLTable>
  );
}
