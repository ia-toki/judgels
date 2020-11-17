import classNames from 'classnames';
import * as React from 'react';

import { UserRef } from '../../../../../../components/UserRef/UserRef';
import {
  IcpcScoreboardProblemState,
  IcpcScoreboard,
  IcpcScoreboardContent,
  IcpcScoreboardEntry,
} from '../../../../../../modules/api/uriel/scoreboard';
import { ProfilesMap } from '../../../../../../modules/api/jophiel/profile';

import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';

import './IcpcScoreboardTable.css';

export class IcpcScoreboardTableProps {
  userJid?: string;
  scoreboard: IcpcScoreboard;
  profilesMap: ProfilesMap;
}

export class IcpcScoreboardTable extends React.PureComponent<IcpcScoreboardTableProps> {
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
        <UserRef profile={this.props.profilesMap[entry.contestantJid]} showFlag />
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
    return (
      <tr key={entry.contestantJid} className={classNames({ 'my-rank': entry.contestantJid === this.props.userJid })}>
        {cells}
      </tr>
    );
  };

  private renderProblemCell = (idx: number, attempts: number, penalty: number, state: IcpcScoreboardProblemState) => {
    let className = {};
    if (state === IcpcScoreboardProblemState.Accepted) {
      className = 'accepted';
    } else if (state === IcpcScoreboardProblemState.FirstAccepted) {
      className = 'first-accepted';
    } else if (state === IcpcScoreboardProblemState.NotAccepted && attempts > 0) {
      className = 'not-accepted';
    } else if (state === IcpcScoreboardProblemState.Frozen) {
      className = 'frozen';
    }

    const shownAttempts = state === IcpcScoreboardProblemState.Frozen ? '?' : attempts === 0 ? '-' : '' + attempts;
    const shownPenalty =
      state === IcpcScoreboardProblemState.NotAccepted
        ? '-'
        : state === IcpcScoreboardProblemState.Frozen
        ? '?'
        : '' + penalty;

    return (
      <td key={idx} className={classNames(className)}>
        <strong>{shownAttempts}</strong>
        <br />
        <small>{shownPenalty}</small>
      </td>
    );
  };
}
