import classNames from 'classnames';

import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { GcjScoreboardProblemState } from '../../../../../../modules/api/uriel/scoreboard';
import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';

import './GcjScoreboardTable.scss';

export function GcjScoreboardTable({
  userJid,
  contestJid,
  scoreboard: { state, content },
  profilesMap,
  onOpenSubmissionImage,
  canViewSubmissions,
}) {
  const renderData = () => {
    let rows = content.entries.map(renderRow);
    return <tbody>{rows}</tbody>;
  };

  const renderRow = entry => {
    let cells = [
      <td key="rank">{entry.rank === -1 ? '?' : entry.rank}</td>,
      <td key="contestantJid" className="contestant-cell">
        <UserRef profile={profilesMap[entry.contestantJid]} showFlag />
      </td>,
      <td key="totalAccepted">
        <span className="top total-points-cell">{entry.totalPoints}</span>
        <span className="bottom">{renderPenalty(entry.totalPenalties, GcjScoreboardProblemState.Accepted)}</span>
      </td>,
    ];
    const problemCells = entry.attemptsList.map((item, i) =>
      renderProblemCell(
        i,
        entry.attemptsList[i],
        entry.penaltyList[i],
        entry.problemStateList[i],
        entry.contestantJid,
        state.problemJids[i]
      )
    );
    cells = [...cells, ...problemCells];
    return (
      <tr key={entry.contestantJid} className={classNames({ 'my-rank': entry.contestantJid === userJid })}>
        {cells}
      </tr>
    );
  };

  const renderProblemCell = (idx, attempts, penalty, state, contestantJid, problemJid) => {
    let className = {};
    let attempted = true;
    if (state === GcjScoreboardProblemState.Accepted) {
      className = 'accepted';
    } else if (state === GcjScoreboardProblemState.NotAccepted && attempts > 0) {
      className = 'not-accepted';
    } else if (state === GcjScoreboardProblemState.Frozen) {
      className = 'frozen';
    } else {
      attempted = false;
    }

    const clickable = canViewSubmissions && attempted;

    return (
      <td
        key={idx}
        className={classNames(className, clickable ? 'clickable' : {})}
        onClick={() => clickable && onOpenSubmissionImage(contestJid, contestantJid, problemJid)}
      >
        <span className="top">{renderAttempts(attempts, state)}</span>
        <span className="bottom">{renderPenalty(penalty, state)}</span>
      </td>
    );
  };

  const renderAttempts = (attempts, state) => {
    if (attempts === 0) {
      return '-';
    }

    let wrongAttempts;
    if (state === GcjScoreboardProblemState.Accepted) {
      wrongAttempts = attempts - 1;
    } else {
      wrongAttempts = attempts;
    }

    if (wrongAttempts === 0) {
      return '+';
    }
    return '+' + wrongAttempts;
  };

  const renderPenalty = (penalty, state) => {
    if (state !== GcjScoreboardProblemState.Accepted) {
      return '-';
    }
    return `${renderPenaltyHours(penalty)}:${renderPenaltyMinutes(penalty)}`;
  };

  const renderPenaltyHours = penalty => {
    const hours = Math.floor(penalty / 60);
    if (hours < 10) {
      return '0' + hours;
    }
    return hours;
  };

  const renderPenaltyMinutes = penalty => {
    const minutes = penalty % 60;
    if (minutes < 10) {
      return '0' + minutes;
    }
    return minutes;
  };

  return (
    <ScoreboardTable className="gcj-scoreboard__content" state={state}>
      {renderData(content)}
    </ScoreboardTable>
  );
}
