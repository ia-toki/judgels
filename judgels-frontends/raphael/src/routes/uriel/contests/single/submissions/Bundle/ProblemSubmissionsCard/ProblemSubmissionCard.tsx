import React, { FunctionComponent } from 'react';
import { Card, H3, HTMLTable } from '@blueprintjs/core';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';
import { FormattedDate } from 'components/FormattedDate/FormattedDate';

import './ProblemSubmissionCard.css';

export interface ProblemSubmissionCardProps {
  alias: string;
  submissions: ItemSubmission[];
  canSupervise: boolean;
  canManage: boolean;
}

export const ProblemSubmissionCard: FunctionComponent<ProblemSubmissionCardProps> = ({ alias, submissions }) => {
  const renderAnswer = (answer?: string) => (answer && answer.length > 0 ? answer : '-');

  const renderSingleSubmission = (submission: ItemSubmission, itemNum: number) => (
    <tr key={submission.itemJid}>
      <td>{itemNum + 1}</td>
      <td>{renderAnswer(submission.answer)}</td>
      <td>
        <FormattedDate value={submission.time} />
      </td>
    </tr>
  );

  return (
    <Card className="contest-bundle-problem-submission">
      <H3>{alias}</H3>
      <HTMLTable className="submission-table" bordered striped>
        <thead>
          <tr>
            <th>Nomor Soal</th>
            <th>Jawaban</th>
            <th>Time</th>
          </tr>
        </thead>
        <tbody>{submissions.map(renderSingleSubmission)}</tbody>
      </HTMLTable>
    </Card>
  );
};
