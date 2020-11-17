import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { FormattedRelative } from '../../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { VerdictTag } from '../../../../../../../components/VerdictTag/VerdictTag';
import { ProfilesMap } from '../../../../../../../modules/api/jophiel/profile';
import { getGradingLanguageName } from '../../../../../../../modules/api/gabriel/language';
import { Contest } from '../../../../../../../modules/api/uriel/contest';
import { Submission as ProgrammingSubmission } from '../../../../../../../modules/api/sandalphon/submissionProgramming';

import '../../../../../../../components/SubmissionsTable/Programming/SubmissionsTable.css';

export interface ContestSubmissionsTableProps {
  contest: Contest;
  submissions: ProgrammingSubmission[];
  canSupervise: boolean;
  canManage: boolean;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
  onRegrade: (submissionJid: string) => any;
}

export class ContestSubmissionsTable extends React.PureComponent<ContestSubmissionsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed submissions-table">
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
          {this.props.canSupervise && <th className="col-user">User</th>}
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
    const { contest, submissions, profilesMap, problemAliasesMap, canSupervise } = this.props;

    const rows = submissions.map(submission => (
      <tr key={submission.jid}>
        <td>
          {submission.id}
          {this.props.canManage && (
            <>
              &nbsp;&nbsp;&nbsp;
              <Icon className="action" icon="refresh" intent="primary" onClick={this.onClickRegrade(submission.jid)} />
            </>
          )}
        </td>
        {canSupervise && (
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
          <Link className="action" to={`/contests/${contest.slug}/submissions/${submission.id}`}>
            <Icon icon="search" />
          </Link>
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  private onClickRegrade = (submissionJid: string) => {
    return () => this.props.onRegrade(submissionJid);
  };
}
