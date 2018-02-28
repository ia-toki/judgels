import * as classNames from 'classnames';
import * as React from 'react';

import {
  IcpcProblemState,
  IcpcScoreboard,
  IcpcScoreboardContent,
  IcpcScoreboardEntry,
  IcpcScoreboardState,
} from '../../../../../../../../../../modules/api/uriel/scoreboard';

import './IcpcScoreboardTable.css';

export class IcpcScoreboardTableProps {
  scoreboard: IcpcScoreboard;
}

export class IcpcScoreboardTable extends React.Component<IcpcScoreboardTableProps> {
  render() {
    const { scoreboard } = this.props;
    return (
      <table className="pt-table pt-striped scoreboard__content">
        {this.renderHeader(scoreboard.state)}
        {this.renderData(scoreboard.content)}
      </table>
    );
  }

  private renderHeader = (state: IcpcScoreboardState) => {
    const problems = state.problemAliases.map(alias => (
      <th key={alias} className="problem-cell">
        {alias}
      </th>
    ));

    return (
      <thead>
        <tr>
          <th className="rank-cell">#</th>
          <th className="contestant-cell">Contestant</th>
          <th className="problem-cell">Total</th>
          {problems}
        </tr>
      </thead>
    );
  };

  private renderData = (content: IcpcScoreboardContent) => {
    let rows = content.entries.map(this.renderRow);
    return <tbody>{rows}</tbody>;
  };

  private renderRow = (entry: IcpcScoreboardEntry) => {
    let cells = [
      <td key="rank">{entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        {entry.contestantJid}
      </td>,
      <td key="totalAccepted">
        <strong>{entry.totalAccepted}</strong>
        <br />
        <small>{entry.totalPenalties}</small>
      </td>,
    ];
    const problemCells = entry.attemptsList.map((item, i) =>
      this.renderProblemCell(i, entry.attemptsList[i], entry.penaltyList[i], entry.problemStateList[i])
    );
    cells = [...cells, ...problemCells];
    return <tr key={entry.contestantJid}>{cells}</tr>;
  };

  private renderProblemCell = (idx: number, attempts: number, penalty: number, state: IcpcProblemState) => {
    let className = {};
    if (state === IcpcProblemState.Accepted) {
      className = 'accepted';
    } else if (state === IcpcProblemState.FirstAccepted) {
      className = 'first-accepted';
    } else if (state === IcpcProblemState.NotAccepted && attempts > 0) {
      className = 'not-accepted';
    }

    const shownAttempts = attempts === 0 ? '-' : '' + attempts;
    const shownPenalty = state === IcpcProblemState.NotAccepted ? '-' : '' + penalty;

    return (
      <td key={idx} className={classNames(className)}>
        <strong>{shownAttempts}</strong>
        <br />
        <small>{shownPenalty}</small>
      </td>
    );
  };
}
