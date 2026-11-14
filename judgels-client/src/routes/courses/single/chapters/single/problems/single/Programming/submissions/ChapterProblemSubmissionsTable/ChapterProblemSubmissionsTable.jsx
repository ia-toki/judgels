import { Button, HTMLTable } from '@blueprintjs/core';
import { Refresh, Search } from '@blueprintjs/icons';

import { ButtonLink } from '../../../../../../../../../../components/ButtonLink/ButtonLink';
import { FormattedRelative } from '../../../../../../../../../../components/FormattedRelative/FormattedRelative';
import { GradingVerdictTag } from '../../../../../../../../../../components/GradingVerdictTag/GradingVerdictTag';
import { UserRef } from '../../../../../../../../../../components/UserRef/UserRef';
import { getGradingLanguageName } from '../../../../../../../../../../modules/api/gradingLanguage.js';

export function ChapterProblemSubmissionsTable({
  course,
  chapterAlias,
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
              <Button
                small
                minimal
                icon={<Refresh title="refresh" />}
                aria-label={`Regrade submission ${submission.id}`}
                onClick={onClickRegrade(submission.jid)}
              />
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
          <ButtonLink
            small
            minimal
            to={`/courses/${course.slug}/chapters/${chapterAlias}/problems/${problemAlias}/submissions/${submission.id}`}
          >
            <Search title="search" />
          </ButtonLink>
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  const onClickRegrade = submissionJid => {
    return () => onRegrade(submissionJid);
  };

  return (
    <HTMLTable striped className="table-list-condensed">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
