import { HTMLTable } from '@blueprintjs/core';
import { Search } from '@blueprintjs/icons';
import { Link } from 'react-router-dom';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { VerdictTag } from '../../../../../../components/VerdictTag/VerdictTag';
import { constructProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { constructContainerUrl, constructProblemUrl } from '../../../../../../modules/api/jerahmeel/submission';
import { getGradingLanguageName } from '../../../../../../modules/api/gabriel/language.js';

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
          <th className="col-id">ID</th>
          <th className="col-container">Archive</th>
          <th className="col-problem">Problem</th>
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
        <td>{submission.id}</td>

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
        <td>{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="cell-centered">
          {submission.latestGrading && <VerdictTag verdictCode={submission.latestGrading.verdict.code} />}
        </td>
        <td>{submission.latestGrading && submission.latestGrading.score}</td>
        <td>
          <FormattedRelative value={submission.time} />{' '}
        </td>
        <td className="cell-centered">
          <Link className="action" to={`/submissions/${submission.id}`}>
            <Search />
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
