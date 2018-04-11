import * as classNames from 'classnames';
import * as React from 'react';

import {
  IcpcScoreboardProblemState,
  IcpcScoreboard,
  IcpcScoreboardContent,
  IcpcScoreboardEntry,
  ScoreboardState,
} from '../../../../../../../../../../modules/api/uriel/scoreboard';

import '../ScoreboardTable.css';
import './IcpcScoreboardTable.css';

export class IcpcScoreboardTableProps {
  scoreboard: IcpcScoreboard;
  contestantDisplayNames: { [contestantJid: string]: string };
}

export class IcpcScoreboardTable extends React.Component<IcpcScoreboardTableProps> {
  render() {
    const { scoreboard } = this.props;
    return (
      <table className="pt-html-table pt-html-table-striped scoreboard__content icpc-scoreboard__content">
        {this.renderHeader(scoreboard.state)}
        {this.renderData(scoreboard.content)}
      </table>
    );
  }

  private renderHeader = (state: ScoreboardState) => {
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
        {this.props.contestantDisplayNames[entry.contestantJid]}
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

  private renderProblemCell = (idx: number, attempts: number, penalty: number, state: IcpcScoreboardProblemState) => {
    let className = {};
    if (state === IcpcScoreboardProblemState.Accepted) {
      className = 'accepted';
    } else if (state === IcpcScoreboardProblemState.FirstAccepted) {
      className = 'first-accepted';
    } else if (state === IcpcScoreboardProblemState.NotAccepted && attempts > 0) {
      className = 'not-accepted';
    }

    const shownAttempts = attempts === 0 ? '-' : '' + attempts;
    const shownPenalty = state === IcpcScoreboardProblemState.NotAccepted ? '-' : '' + penalty;

    return (
      <td key={idx} className={classNames(className)}>
        <strong>{shownAttempts}</strong>
        <br />
        <small>{shownPenalty}</small>
      </td>
    );
  };
}
