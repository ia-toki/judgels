import { HTMLTable } from '@blueprintjs/core';
import { Refresh, Search } from '@blueprintjs/icons';
import { Link } from 'react-router';

import { FormattedRelative } from '../../../../../../../components/FormattedRelative/FormattedRelative';
import { GradingVerdictTag } from '../../../../../../../components/GradingVerdictTag/GradingVerdictTag';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { getGradingLanguageName } from '../../../../../../../modules/api/gabriel/language.js';

import '../../../../../../../components/SubmissionsTable/Programming/SubmissionsTable.scss';

export function ContestSubmissionsTable({
  contest,
  submissions,
  canSupervise,
  canManage,
  profilesMap,
  problemAliasesMap,
  onRegrade,
}) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-fit">ID</th>
          {canSupervise && <th>User</th>}
          <th className="col-fit">Prob</th>
          <th className="col-fit">Lang</th>
          <th className="col-fit">Verdict</th>
          <th>Time</th>
          <th className="col-fit" />
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = submissions.map(submission => (
      <tr key={submission.jid}>
        <td className="col-fit">
          {submission.id}
          {canManage && (
            <>
              &nbsp;&nbsp;&nbsp;
              <Refresh className="action" intent="primary" title="refresh" onClick={onClickRegrade(submission.jid)} />
            </>
          )}
        </td>
        {canSupervise && (
          <td>
            <UserRef profile={profilesMap[submission.userJid]} />
          </td>
        )}
        <td className="col-fit">{problemAliasesMap[submission.problemJid]}</td>
        <td className="col-fit">{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="col-fit">
          {submission.latestGrading && <GradingVerdictTag wide grading={submission.latestGrading} />}
        </td>
        <td>
          <FormattedRelative value={submission.time} />
        </td>
        <td className="col-fit">
          <Link className="action" to={`/contests/${contest.slug}/submissions/${submission.id}`}>
            <Search title="search" />
          </Link>
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  const onClickRegrade = submissionJid => {
    return () => onRegrade(submissionJid);
  };

  return (
    <HTMLTable striped className="table-list-condensed submissions-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
