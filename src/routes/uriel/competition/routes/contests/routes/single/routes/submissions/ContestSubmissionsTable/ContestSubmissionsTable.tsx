import { Icon, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { Submission } from '../../../../../../../../../../modules/api/sandalphon/submission';
import { getGradingLanguageName } from '../../../../../../../../../../modules/api/gabriel/languages';
import { getVerdictIntent } from '../../../../../../../../../../modules/api/gabriel/verdicts';

import './ContestSubmissionsTable.css';

export interface ContestSubmissionsTableProps {
  submissions: Submission[];
  problemAliasesMap: { [problemJid: string]: string };
}

export class ContestSubmissionsTable extends React.Component<ContestSubmissionsTableProps> {
  render() {
    return (
      <table className="pt-html-table pt-html-table-striped table-list submissions">
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
    const { submissions, problemAliasesMap } = this.props;

    const rows = submissions.map(submission => (
      <tr key={submission.jid}>
        <td>{submission.id}</td>
        <td>{problemAliasesMap[submission.problemJid]}</td>
        <td>{getGradingLanguageName(submission.gradingLanguage)}</td>
        <td className="cell-centered">
          <Tag round intent={getVerdictIntent(submission.latestGrading.verdict)}>
            {submission.latestGrading.verdict}
          </Tag>
        </td>
        <td>{submission.latestGrading.score}</td>
        <td>
          <FormattedRelative value={submission.time} />{' '}
        </td>
        <td className="cell-centered">
          <Icon icon="search" />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
