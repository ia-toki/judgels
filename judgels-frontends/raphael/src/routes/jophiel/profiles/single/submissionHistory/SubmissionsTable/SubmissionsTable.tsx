import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { VerdictTag } from '../../../../../../components/VerdictTag/VerdictTag';
import { constructProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { getGradingLanguageName } from '../../../../../../modules/api/gabriel/language';
import { Submission as ProgrammingSubmission } from '../../../../../../modules/api/sandalphon/submissionProgramming';

import '../../../../../../components/SubmissionsTable/Programming/SubmissionsTable.css';

export interface SubmissionsTableProps {
  submissions: ProgrammingSubmission[];
  canManage: boolean;
  userJid: string;
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };
  containerNamesMap: { [problemJid: string]: string };
  containerPathsMap: { [problemJid: string]: string[] };
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
      problemAliasesMap,
      problemNamesMap,
      containerNamesMap,
      containerPathsMap,
    } = this.props;

    const rows = submissions.map(submission => (
      <tr key={submission.jid}>
        <td>{submission.id}</td>

        <td>
          <Link to={this.constructContainerUrl(containerPathsMap[submission.containerJid])}>
            {containerNamesMap[submission.containerJid]}
          </Link>
        </td>
        <td>
          <Link
            to={`${this.constructContainerUrl(containerPathsMap[submission.containerJid])}/${problemAliasesMap[
              submission.containerJid + '-' + submission.problemJid
            ] || '#'}`}
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

  private constructContainerUrl = (subpaths: string[]) => {
    if (!subpaths) {
      return '';
    }
    if (subpaths.length === 2) {
      return `/courses/${subpaths[0]}/chapters/${subpaths[1]}/problems`;
    } else {
      return `/problems/${subpaths[0]}`;
    }
  };
}
