import classNames from 'classnames';

import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { IcpcScoreboardProblemState } from '../../../../../../modules/api/uriel/scoreboard';
import { ScoreboardTable } from '../ScoreboardTable/ScoreboardTable';

import './IcpcScoreboardTable.scss';

export function IcpcScoreboardTable({
  userJid,
  contestJid,
  scoreboard: { state, content },
  profilesMap,
  onOpenSubmissionImage,
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
      <td key="totalAccepted">
        <span className="top">{entry.totalAccepted}</span>
        <span className="bottom">{entry.totalPenalties}</span>
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
    if (state === IcpcScoreboardProblemState.Accepted) {
      className = 'accepted';
    } else if (state === IcpcScoreboardProblemState.FirstAccepted) {
      className = 'first-accepted';
    } else if (state === IcpcScoreboardProblemState.NotAccepted && attempts > 0) {
      className = 'not-accepted';
    } else if (state === IcpcScoreboardProblemState.Frozen) {
      className = 'frozen';
    } else {
      attempted = false;
    }

    const shownAttempts = state === IcpcScoreboardProblemState.Frozen ? '?' : attempts === 0 ? '-' : '' + attempts;
    const shownPenalty =
      state === IcpcScoreboardProblemState.NotAccepted
        ? '-'
        : state === IcpcScoreboardProblemState.Frozen
          ? '?'
          : '' + penalty;

    const clickable = canViewSubmissions && attempted;

    return (
      <td
        key={idx}
        className={classNames(className, clickable ? 'clickable' : {})}
        onClick={() => clickable && onOpenSubmissionImage(contestJid, contestantJid, problemJid)}
      >
        <span className="top">{shownAttempts}</span>
        <span className="bottom">{shownPenalty}</span>
      </td>
    );
  };

  return (
    <ScoreboardTable className="icpc-scoreboard__content" state={state}>
      {renderData()}
    </ScoreboardTable>
  );
}
