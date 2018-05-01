import * as React from 'react';

import {
  IoiScoreboard,
  IoiScoreboardContent,
  IoiScoreboardEntry,
} from '../../../../../../../../../../modules/api/uriel/scoreboard';
import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';
import { UsersMap } from '../../../../../../../../../../modules/api/jophiel/user';

import './IoiScoreboardTable.css';
import * as classNames from 'classnames';

export class IoiScoreboardTableProps {
  userJid?: string;
  scoreboard: IoiScoreboard;
  usersMap: UsersMap;
}

export class IoiScoreboardTable extends React.Component<IoiScoreboardTableProps> {
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
        {this.props.usersMap[entry.contestantJid] && this.props.usersMap[entry.contestantJid].username}
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
    return <td key={idx}>{score}</td>;
  };
}
