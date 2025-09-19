import classNames from 'classnames';

import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';

import './BundleScoreboardTable.scss';

export function BundleScoreboardTable({ userJid, scoreboard, profilesMap }) {
  const renderData = content => {
    let rows = content.entries.map(renderRow);
    return <tbody>{rows}</tbody>;
  };

  const renderRow = entry => {
    let cells = [
      <td key="rank">{entry.rank === -1 ? '?' : entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        <UserRef profile={profilesMap[entry.contestantJid]} />
      </td>,
      <td key="totalScores">
        <strong>{entry.totalScores}</strong>
      </td>,
    ];
    const problemCells = entry.scores.map((score, i) => renderProblemCell(i, score));
    cells = [...cells, ...problemCells];
    return (
      <tr key={entry.contestantJid} className={classNames({ 'my-rank': entry.contestantJid === userJid })}>
        {cells}
      </tr>
    );
  };

  const renderProblemCell = (idx, score) => {
    return <td key={idx}>{score}</td>;
  };

  return (
    <ScoreboardTable className="bundle-scoreboard__content" state={scoreboard.state}>
      {renderData(scoreboard.content)}
    </ScoreboardTable>
  );
}
