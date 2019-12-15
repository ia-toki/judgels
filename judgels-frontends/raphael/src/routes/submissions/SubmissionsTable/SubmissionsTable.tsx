import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { FormattedRelative } from '../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../components/UserRef/UserRef';
import { VerdictTag } from '../../../components/VerdictTag/VerdictTag';
import { ProfilesMap } from '../../../modules/api/jophiel/profile';
import { getGradingLanguageName } from '../../../modules/api/gabriel/language';
import { Submission as ProgrammingSubmission } from '../../../modules/api/sandalphon/submissionProgramming';

import '../../../components/SubmissionsTable/Programming/SubmissionsTable.css';
import { constructProblemName } from '../../../modules/api/sandalphon/problem';

export interface SubmissionsTableProps {
  submissions: ProgrammingSubmission[];
  canManage: boolean;
  userJid: string;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };
  containerNamesMap: { [problemJid: string]: string };
  onRegrade: (submissionJid: string) => any;
}

export class SubmissionsTable extends React.PureComponent<SubmissionsTableProps> {
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
          <th className="col-user">User</th>
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

  private renderRows = () => {
    const {
      submissions,
      userJid,
      canManage,
      profilesMap,
      problemAliasesMap,
      problemNamesMap,
      containerNamesMap,
    } = this.props;

    const rows = submissions.map(submission => (
      <tr key={submission.jid}>
        <td>
          {submission.id}
          {canManage && (
            <>
              &nbsp;&nbsp;&nbsp;
              <Icon className="action" icon="refresh" intent="primary" onClick={this.onClickRegrade(submission.jid)} />
            </>
          )}
        </td>
        <td>
          <UserRef profile={profilesMap[submission.userJid]} />
        </td>

        <td>{containerNamesMap[submission.containerJid]}</td>
        <td>
          {constructProblemName(problemNamesMap[submission.problemJid], problemAliasesMap[submission.problemJid])}
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
            <Link className="action" to={`/submissions/${submission.id}`}>
              <Icon icon="search" />
            </Link>
          )}
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  private onClickRegrade = (submissionJid: string) => {
    return () => this.props.onRegrade(submissionJid);
  };
}
