import classNames from 'classnames';

import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';

import './IoiScoreboardTable.css';

export function IoiScoreboardTable({
  userJid,
  contestJid,
  onOpenSubmissionImage,
  scoreboard: { state, content },
  profilesMap,
  canViewSubmissions,
}) {
  const renderData = () => {
    const rows = content.entries.map(renderRow);
    return <tbody>{rows}</tbody>;
  };

  const renderRow = entry => {
    let cells = [
      <td key="rank">{entry.rank === -1 ? '?' : entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        <UserRef profile={profilesMap[entry.contestantJid]} showFlag />
      </td>,
      <td key="totalScores">
        <strong>{entry.totalScores}</strong>
      </td>,
    ];
    const problemCells = entry.scores.map((item, i) =>
      renderProblemCell(i, item, entry.contestantJid, state.problemJids[i])
    );
    cells = [...cells, ...problemCells];
    return (
      <tr key={entry.contestantJid} className={classNames({ 'my-rank': entry.contestantJid === userJid })}>
        {cells}
      </tr>
    );
  };

  const renderProblemCell = (idx, score, contestantJid, problemJid) => {
    const clickable = canViewSubmissions && score !== null;

    return (
      <td
        className={classNames(clickable ? 'clickable' : {})}
        onClick={() => clickable && onOpenSubmissionImage(contestJid, contestantJid, problemJid)}
        key={idx}
      >
        {score === null ? '-' : score}
      </td>
    );
  };

  return (
    <ScoreboardTable className="ioi-scoreboard__content" state={state}>
      {renderData()}
    </ScoreboardTable>
  );
}
