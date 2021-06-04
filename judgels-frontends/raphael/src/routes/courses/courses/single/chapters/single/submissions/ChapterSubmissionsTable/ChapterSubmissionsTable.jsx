import { HTMLTable, Icon } from '@blueprintjs/core';
import { Link } from 'react-router-dom';

import { FormattedRelative } from '../../../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import { VerdictTag } from '../../../../../../../../components/VerdictTag/VerdictTag';
import { getGradingLanguageName } from '../../../../../../../../modules/api/gabriel/language.js';

import '../../../../../../../../components/SubmissionsTable/Programming/SubmissionsTable.scss';

export function ChapterSubmissionsTable({
  course,
  chapter,
  submissions,
  canManage,
  profilesMap,
  problemAliasesMap,
  onRegrade,
}) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-id">ID</th>
          <th className="col-user">User</th>
          <th className="col-prob">Prob</th>
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
        <td>{problemAliasesMap[submission.containerJid + '-' + submission.problemJid]}</td>
        <td>{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="cell-centered">
          {submission.latestGrading && <VerdictTag verdictCode={submission.latestGrading.verdict.code} />}
        </td>
        <td>{submission.latestGrading && submission.latestGrading.score}</td>
        <td>
          <FormattedRelative value={submission.time} />{' '}
        </td>
        <td className="cell-centered">
          <Link
            className="action"
            to={`/courses/${course.slug}/chapters/${chapter.alias}/submissions/${submission.id}`}
          >
            <Icon icon="search" />
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
