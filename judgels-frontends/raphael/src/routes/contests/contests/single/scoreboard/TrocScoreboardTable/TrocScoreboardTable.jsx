import classNames from 'classnames';

import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { TrocScoreboardProblemState } from '../../../../../../modules/api/uriel/scoreboard';
import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';

import './TrocScoreboardTable.css';

export function TrocScoreboardTable({
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
        <strong className="total-points-cell">{entry.totalPoints}</strong>
        <br />
        <small>{renderPenalty(entry.totalPenalties, TrocScoreboardProblemState.Accepted)}</small>
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
    if (state === TrocScoreboardProblemState.Accepted) {
      className = 'accepted';
    } else if (state === TrocScoreboardProblemState.FirstAccepted) {
      className = 'first-accepted';
    } else if (state === TrocScoreboardProblemState.NotAccepted && attempts > 0) {
      className = 'not-accepted';
    } else if (state === TrocScoreboardProblemState.Frozen) {
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
        <strong>{renderAttempts(attempts, state)}</strong>
        <br />
        <small>{renderPenalty(penalty, state)}</small>
      </td>
    );
  };

  const renderAttempts = (attempts, state) => {
    if (attempts === 0) {
      return '-';
    }

    let wrongAttempts;
    if (state === TrocScoreboardProblemState.Accepted || state === TrocScoreboardProblemState.FirstAccepted) {
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
    if (state !== TrocScoreboardProblemState.Accepted && state !== TrocScoreboardProblemState.FirstAccepted) {
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
    <ScoreboardTable className="troc-scoreboard__content" state={state}>
      {renderData(content)}
    </ScoreboardTable>
  );
}
