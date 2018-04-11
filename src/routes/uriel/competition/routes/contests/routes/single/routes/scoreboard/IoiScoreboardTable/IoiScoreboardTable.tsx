import * as React from 'react';

import {
  IoiScoreboard,
  IoiScoreboardContent,
  IoiScoreboardEntry,
  ScoreboardState,
} from '../../../../../../../../../../modules/api/uriel/scoreboard';

import './IoiScoreboardTable.css';

export class IoiScoreboardTableProps {
  scoreboard: IoiScoreboard;
  contestantDisplayNames: { [contestantJid: string]: string };
}

export class IoiScoreboardTable extends React.Component<IoiScoreboardTableProps> {
  render() {
    const { scoreboard } = this.props;
    return (
      <table className="pt-table pt-striped scoreboard__content ioi-scoreboard__content">
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

  private renderData = (content: IoiScoreboardContent) => {
    let rows = content.entries.map(this.renderRow);
    return <tbody>{rows}</tbody>;
  };

  private renderRow = (entry: IoiScoreboardEntry) => {
    let cells = [
      <td key="rank">{entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        {this.props.contestantDisplayNames[entry.contestantJid]}
      </td>,
      <td key="totalScores">
        <strong>{entry.totalScores}</strong>
      </td>,
    ];
    const problemCells = entry.scores.map((item, i) => this.renderProblemCell(i, item));
    cells = [...cells, ...problemCells];
    return <tr key={entry.contestantJid}>{cells}</tr>;
  };

  private renderProblemCell = (idx: number, score: number | null) => {
    return <td key={idx}>{score}</td>;
  };
}
