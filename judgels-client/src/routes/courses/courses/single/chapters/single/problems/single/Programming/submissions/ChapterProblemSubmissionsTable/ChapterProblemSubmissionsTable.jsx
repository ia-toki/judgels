import { HTMLTable } from '@blueprintjs/core';
import { Refresh, Search } from '@blueprintjs/icons';
import { Link } from 'react-router-dom';

import { FormattedRelative } from '../../../../../../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../../../../../../components/UserRef/UserRef';
import { GradingVerdictTag } from '../../../../../../../../../../../components/GradingVerdictTag/GradingVerdictTag';
import { getGradingLanguageName } from '../../../../../../../../../../../modules/api/gabriel/language.js';

import '../../../../../../../../../../../components/SubmissionsTable/Programming/SubmissionsTable.scss';

export function ChapterProblemSubmissionsTable({
  course,
  chapter,
  problemAlias,
  submissions,
  canManage,
  profilesMap,
  onRegrade,
}) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-fit">ID</th>
          <th>User</th>
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
              <Refresh className="action" intent="primary" onClick={onClickRegrade(submission.jid)} />
            </>
          )}
        </td>
        <td>
          <UserRef profile={profilesMap[submission.userJid]} />
        </td>
        <td className="col-fit">{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="col-fit">
          {submission.latestGrading && <GradingVerdictTag wide grading={submission.latestGrading} />}
        </td>
        <td>
          <FormattedRelative value={submission.time} />
        </td>
        <td className="col-fit">
          <Link
            className="action"
            to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problemAlias}/submissions/${submission.id}`}
          >
            <Search />
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
