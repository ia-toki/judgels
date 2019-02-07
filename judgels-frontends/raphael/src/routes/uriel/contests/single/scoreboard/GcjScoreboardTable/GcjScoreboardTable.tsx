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
        <strong>{entry.totalPoints}</strong>
        <br />
        <small>{entry.totalPenalties}</small>
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

    const shownAttempts = attempts > 0 || state === GcjScoreboardProblemState.Accepted ? '' + attempts : '-';
    const shownPenalty = state !== GcjScoreboardProblemState.Accepted ? '-' : '' + penalty;

    return (
      <td key={idx} className={classNames(className)}>
        <strong>{shownAttempts}</strong>
        <br />
        <small>{shownPenalty}</small>
      </td>
    );
  };
}
