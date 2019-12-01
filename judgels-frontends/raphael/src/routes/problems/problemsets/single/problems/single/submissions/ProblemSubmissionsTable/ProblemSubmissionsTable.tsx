import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { FormattedRelative } from '../../../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import { VerdictTag } from '../../../../../../../../components/VerdictTag/VerdictTag';
import { ProfilesMap } from '../../../../../../../../modules/api/jophiel/profile';
import { getGradingLanguageName } from '../../../../../../../../modules/api/gabriel/language';
import { ProblemSet } from '../../../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { Submission as ProgrammingSubmission } from '../../../../../../../../modules/api/sandalphon/submissionProgramming';

import './ProblemSubmissionsTable.css';

export interface ProblemSubmissionsTableProps {
  problemSet: ProblemSet;
  problem: ProblemSetProblem;
  submissions: ProgrammingSubmission[];
  canManage: boolean;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export class ProblemSubmissionsTable extends React.PureComponent<ProblemSubmissionsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed problem-submissions-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-id">ID</th>
          {this.props.canManage && <th className="col-user">User</th>}
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

  private renderRows = () => {
    const { problemSet, problem, submissions, canManage, profilesMap, problemAliasesMap } = this.props;

    const rows = submissions.map(submission => (
      <tr key={submission.jid}>
        <td>{submission.id}</td>
        {canManage && (
          <td>
            <UserRef profile={profilesMap[submission.userJid]} />
          </td>
        )}

        <td>{problemAliasesMap[submission.problemJid]}</td>
        <td>{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="cell-centered">
          {submission.latestGrading && <VerdictTag verdictCode={submission.latestGrading.verdict.code} />}
        </td>
        <td>{submission.latestGrading && submission.latestGrading.score}</td>
        <td>
          <FormattedRelative value={submission.time} />{' '}
        </td>
        <td className="cell-centered">
          <Link className="action" to={`/problems/${problemSet.slug}/${problem.alias}/submissions/${submission.id}`}>
            <Icon icon="search" />
          </Link>
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
