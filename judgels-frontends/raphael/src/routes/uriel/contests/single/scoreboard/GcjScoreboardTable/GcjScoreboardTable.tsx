import * as classNames from 'classnames';
import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import {
  GcjScoreboardProblemState,
  GcjScoreboard,
  GcjScoreboardContent,
  GcjScoreboardEntry,
} from 'modules/api/uriel/scoreboard';
import { ProfilesMap } from 'modules/api/jophiel/profile';

import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';

import './GcjScoreboardTable.css';

export class GcjScoreboardTableProps {
  userJid?: string;
  scoreboard: GcjScoreboard;
  profilesMap: ProfilesMap;
}

export class GcjScoreboardTable extends React.PureComponent<GcjScoreboardTableProps> {
  render() {
    const { scoreboard } = this.props;
    return (
      <ScoreboardTable className="gcj-scoreboard__content" state={scoreboard.state}>
        {this.renderData(scoreboard.content)}
      </ScoreboardTable>
    );
  }

  private renderData = (content: GcjScoreboardContent) => {
    let rows = content.entries.map(this.renderRow);
    return <tbody>{rows}</tbody>;
  };

  private renderRow = (entry: GcjScoreboardEntry) => {
    let cells = [
      <td key="rank">{entry.rank === -1 ? '?' : entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        <UserRef profile={this.props.profilesMap[entry.contestantJid]} showFlag />
      </td>,
      <td key="totalAccepted">
        <strong className="total-points-cell">{entry.totalPoints}</strong>
        <br />
        <small>{this.renderPenalty(entry.totalPenalties, GcjScoreboardProblemState.Accepted)}</small>
      </td>,
    ];
    const problemCells = entry.attemptsList.map((item, i) =>
      this.renderProblemCell(i, entry.attemptsList[i], entry.penaltyList[i], entry.problemStateList[i])
    );
    cells = [...cells, ...problemCells];
    return (
      <tr key={entry.contestantJid} className={classNames({ 'my-rank': entry.contestantJid === this.props.userJid })}>
        {cells}
      </tr>
    );
  };

  private renderProblemCell = (idx: number, attempts: number, penalty: number, state: GcjScoreboardProblemState) => {
    let className = {};
    if (state === GcjScoreboardProblemState.Accepted) {
      className = 'accepted';
    } else if (state === GcjScoreboardProblemState.NotAccepted && attempts > 0) {
      className = 'not-accepted';
    } else if (state === GcjScoreboardProblemState.Frozen) {
      className = 'frozen';
    }

    return (
      <td key={idx} className={classNames(className)}>
        <strong>{this.renderAttempts(attempts, state)}</strong>
        <br />
        <small>{this.renderPenalty(penalty, state)}</small>
      </td>
    );
  };

  private renderAttempts = (attempts: number, state: GcjScoreboardProblemState) => {
    if (state !== GcjScoreboardProblemState.Accepted) {
      return '-';
    }

    const wrongAttempts = attempts - 1;
    if (wrongAttempts === 0) {
      return '+';
    }
    return '+' + wrongAttempts;
  };

  private renderPenalty = (penalty: number, state: GcjScoreboardProblemState) => {
    if (state !== GcjScoreboardProblemState.Accepted) {
      return '-';
    }
    return `${this.renderPenaltyHours(penalty)}:${this.renderPenaltyMinutes(penalty)}`;
  };

  private renderPenaltyHours = (penalty: number) => {
    const hours = Math.floor(penalty / 60);
    if (hours < 10) {
      return '0' + hours;
    }
    return hours;
  };

  private renderPenaltyMinutes = (penalty: number) => {
    const minutes = penalty % 60;
    if (minutes < 10) {
      return '0' + minutes;
    }
    return minutes;
  };
}
