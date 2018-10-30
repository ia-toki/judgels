import { Icon } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';
import { Link } from 'react-router-dom';

import { UserRef } from 'components/UserRef/UserRef';
import { VerdictTag } from 'components/VerdictTag/VerdictTag';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Submission } from 'modules/api/sandalphon/submission';
import { getGradingLanguageName } from 'modules/api/gabriel/language';
import { Contest } from 'modules/api/uriel/contest';

import './ContestSubmissionsTable.css';

export interface ContestSubmissionsTableProps {
  contest: Contest;
  submissions: Submission[];
  canSupervise: boolean;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export class ContestSubmissionsTable extends React.PureComponent<ContestSubmissionsTableProps> {
  render() {
    return (
      <table className="bp3-html-table bp3-html-table-striped table-list-condensed submissions">
        {this.renderHeader()}
        {this.renderRows()}
      </table>
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
        <td>{submission.id}</td>
        {canSupervise && (
          <td>
            <UserRef profile={profilesMap[submission.userJid]} />
          </td>
        )}
        <td>{problemAliasesMap[submission.problemJid]}</td>
        <td>{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="cell-centered">
          {submission.latestGrading && <VerdictTag verdictCode={submission.latestGrading.verdict} />}
        </td>
        <td>{submission.latestGrading && submission.latestGrading.score}</td>
        <td>
          <FormattedRelative value={submission.time} />{' '}
        </td>
        <td className="cell-centered">
          <Link to={`/contests/${contest.slug}/submissions/${submission.id}`}>
            <Icon icon="search" />
          </Link>
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
