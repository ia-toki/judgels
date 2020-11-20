import classNames from 'classnames';
import * as React from 'react';

import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';
import { UserRef } from '../../../../../../components/UserRef/UserRef';

import './BundleScoreboardPage.css';

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
      <td key="totalAnsweredItems">
        <strong>{entry.totalAnsweredItems}</strong>
      </td>,
    ];
    const problemCells = entry.answeredItems.map((item, i) => renderProblemCell(i, item));
    cells = [...cells, ...problemCells];
    return (
      <tr key={entry.contestantJid} className={classNames({ 'my-rank': entry.contestantJid === userJid })}>
        {cells}
      </tr>
    );
  };

  const renderProblemCell = (idx, answered) => {
    return <td key={idx}>{answered}</td>;
  };

  return (
    <ScoreboardTable className="bundle-scoreboard__content" state={scoreboard.state}>
      {renderData(scoreboard.content)}
    </ScoreboardTable>
  );
}
