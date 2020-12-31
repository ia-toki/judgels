import { HTMLTable, Icon } from '@blueprintjs/core';
import { Link } from 'react-router-dom';

import { FormattedRelative } from '../../../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import { VerdictTag } from '../../../../../../../../components/VerdictTag/VerdictTag';
import { getGradingLanguageName } from '../../../../../../../../modules/api/gabriel/language.js';

import '../../../../../../../../components/SubmissionsTable/Programming/SubmissionsTable.css';

export function ProblemSubmissionsTable({
  problemSet,
  problem,
  submissions,
  userJid,
  canManage,
  profilesMap,
  onRegrade,
}) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-id">ID</th>
          <th className="col-user">User</th>
          <th className="col-lang">Lang</th>
          <th className="col-verdict">Verdict</th>
          <th className="col-pts">Pts</th>
          <th>Time</th>
          <th className="col-actions" />
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = submissions.map(submission => (
      <tr key={submission.jid}>
        <td>
          {submission.id}
          {canManage && (
            <>
              &nbsp;&nbsp;&nbsp;
              <Icon className="action" icon="refresh" intent="primary" onClick={onClickRegrade(submission.jid)} />
            </>
          )}
        </td>
        <td>
          <UserRef profile={profilesMap[submission.userJid]} />
        </td>

        <td>{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="cell-centered">
          {submission.latestGrading && <VerdictTag verdictCode={submission.latestGrading.verdict.code} />}
        </td>
        <td>{submission.latestGrading && submission.latestGrading.score}</td>
        <td>
          <FormattedRelative value={submission.time} />{' '}
        </td>
        <td className="cell-centered">
          {(canManage || userJid === submission.userJid) && (
            <Link className="action" to={`/problems/${problemSet.slug}/${problem.alias}/submissions/${submission.id}`}>
              <Icon icon="search" />
            </Link>
          )}
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
