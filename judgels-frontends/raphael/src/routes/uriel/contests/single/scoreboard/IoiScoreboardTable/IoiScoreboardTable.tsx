import * as classNames from 'classnames';
import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { IoiScoreboard, IoiScoreboardContent, IoiScoreboardEntry } from 'modules/api/uriel/scoreboard';
import { ProfilesMap } from 'modules/api/jophiel/profile';

import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';

import './IoiScoreboardTable.css';

export class IoiScoreboardTableProps {
  userJid?: string;
  scoreboard: IoiScoreboard;
  profilesMap: ProfilesMap;
}

export class IoiScoreboardTable extends React.PureComponent<IoiScoreboardTableProps> {
  render() {
    const { scoreboard } = this.props;
    return (
      <ScoreboardTable className="ioi-scoreboard__content" state={scoreboard.state}>
        {this.renderData(scoreboard.content)}
      </ScoreboardTable>
    );
  }

  private renderData = (content: IoiScoreboardContent) => {
    let rows = content.entries.map(this.renderRow);
    return <tbody>{rows}</tbody>;
  };

  private renderRow = (entry: IoiScoreboardEntry) => {
    let cells = [
      <td key="rank">{entry.rank === -1 ? '?' : entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        <UserRef profile={this.props.profilesMap[entry.contestantJid]} showFlag />
      </td>,
      <td key="totalScores">
        <strong>{entry.totalScores}</strong>
      </td>,
    ];
    const problemCells = entry.scores.map((item, i) => this.renderProblemCell(i, item));
    cells = [...cells, ...problemCells];
    return (
      <tr key={entry.contestantJid} className={classNames({ 'my-rank': entry.contestantJid === this.props.userJid })}>
        {cells}
      </tr>
    );
  };

  private renderProblemCell = (idx: number, score: number | null) => {
    return <td key={idx}>{score === null ? '-' : score}</td>;
  };
}
