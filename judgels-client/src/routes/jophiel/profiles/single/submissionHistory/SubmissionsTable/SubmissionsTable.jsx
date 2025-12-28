import { HTMLTable } from '@blueprintjs/core';
import { Search } from '@blueprintjs/icons';
import { Link } from 'react-router';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { GradingVerdictTag } from '../../../../../../components/GradingVerdictTag/GradingVerdictTag';
import { getGradingLanguageName } from '../../../../../../modules/api/gabriel/language.js';
import { constructContainerUrl, constructProblemUrl } from '../../../../../../modules/api/jerahmeel/submission';
import { constructProblemName } from '../../../../../../modules/api/sandalphon/problem';

import '../../../../../../components/SubmissionsTable/Programming/SubmissionsTable.scss';

export function SubmissionsTable({
  submissions,
  problemAliasesMap,
  problemNamesMap,
  containerNamesMap,
  containerPathsMap,
}) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-fit">ID</th>
          <th>Archive</th>
          <th>Problem</th>
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
        <td className="col-fit">{submission.id}</td>

        <td>
          <Link to={constructContainerUrl(containerPathsMap[submission.containerJid])}>
            {containerNamesMap[submission.containerJid]}
          </Link>
        </td>
        <td>
          <Link
            to={`${constructProblemUrl(
              containerPathsMap[submission.containerJid],
              problemAliasesMap[submission.containerJid + '-' + submission.problemJid] || '#'
            )}`}
          >
            {constructProblemName(
              problemNamesMap[submission.problemJid],
              problemAliasesMap[submission.containerJid + '-' + submission.problemJid]
            )}
          </Link>
        </td>
        <td className="col-fit">{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="col-fit">
          {submission.latestGrading && <GradingVerdictTag wide grading={submission.latestGrading} />}
        </td>
        <td>
          <FormattedRelative value={submission.time} />
        </td>
        <td className="col-fit">
          <Link className="action" to={`/submissions/${submission.id}`}>
            <Search title="search" />
          </Link>
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed submissions-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
