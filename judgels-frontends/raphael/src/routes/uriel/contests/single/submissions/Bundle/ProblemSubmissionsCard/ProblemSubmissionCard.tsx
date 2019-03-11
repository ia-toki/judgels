import * as React from 'react';
import { H3, HTMLTable } from '@blueprintjs/core';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';
import { VerdictTag } from '../VerdictTag/VerdictTag';
import { FormattedRelative } from 'react-intl';
import { ContentCard } from 'components/ContentCard/ContentCard';

import './ProblemSubmissionCard.css';

export interface ProblemSubmissionCardProps {
  name: string;
  alias: string;
  itemJids: string[];
  submissionsByItemJid: { [itemJid: string]: ItemSubmission };
  canSupervise: boolean;
  canManage: boolean;
}

export const ProblemSubmissionCard: React.FunctionComponent<ProblemSubmissionCardProps> = ({
  name,
  alias,
  itemJids,
  submissionsByItemJid,
  canManage,
}) => {
  const renderSingleRow = (itemJid: string, index: number) => {
    const submission = submissionsByItemJid[itemJid];
    if (submission) {
      return (
        <tr key={itemJid}>
          <td className="col-item-num">{index + 1}</td>
          <td>{submission.answer}</td>
          {canManage && (
            <td className="col-verdict">
              {submission.grading ? <VerdictTag verdict={submission.grading.verdict} /> : '-'}
            </td>
          )}
          <td className="col-time">
            <FormattedRelative value={submission.time} />
          </td>
        </tr>
      );
    } else {
      return (
        <tr key={itemJid}>
          <td className="col-item-num">{index + 1}</td>
          <td>-</td>
          {canManage && <td className="col-verdict">-</td>}
          <td className="col-time">-</td>
        </tr>
      );
    }
  };

  return (
    <ContentCard className="contest-bundle-problem-submission">
      <H3>
        {alias}: {name}
      </H3>
      <HTMLTable striped className="table-list-condensed submission-table">
        <thead>
          <tr>
            <th className="col-item-num">No.</th>
            <th>Answer</th>
            {canManage && <th className="col-verdict">Verdict</th>}
            <th className="col-time">Time</th>
          </tr>
        </thead>
        <tbody>{itemJids.map(renderSingleRow)}</tbody>
      </HTMLTable>
    </ContentCard>
  );
};
