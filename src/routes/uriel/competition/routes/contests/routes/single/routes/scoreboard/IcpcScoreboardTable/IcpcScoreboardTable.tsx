import * as classNames from 'classnames';
import * as React from 'react';

import {
  IcpcScoreboardProblemState,
  IcpcScoreboard,
  IcpcScoreboardContent,
  IcpcScoreboardEntry,
} from '../../../../../../../../../../modules/api/uriel/scoreboard';
import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';
import { UsersMap } from '../../../../../../../../../../modules/api/jophiel/user';

import './IcpcScoreboardTable.css';

export class IcpcScoreboardTableProps {
  scoreboard: IcpcScoreboard;
  usersMap: UsersMap;
}

export class IcpcScoreboardTable extends React.Component<IcpcScoreboardTableProps> {
  render() {
    const { scoreboard } = this.props;
    return (
      <ScoreboardTable className="icpc-scoreboard__content" state={scoreboard.state}>
        {this.renderData(scoreboard.content)}
      </ScoreboardTable>
    );
  }

  private renderData = (content: IcpcScoreboardContent) => {
    let rows = content.entries.map(this.renderRow);
    return <tbody>{rows}</tbody>;
  };

  private renderRow = (entry: IcpcScoreboardEntry) => {
    let cells = [
      <td key="rank">{entry.rank === -1 ? '?' : entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        {this.props.usersMap[entry.contestantJid] && this.props.usersMap[entry.contestantJid].username}
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
