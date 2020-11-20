import { Button, HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { FormattedRelative } from '../../../FormattedRelative/FormattedRelative';
import { ContentCard } from '../../../ContentCard/ContentCard';
import { FormattedAnswer } from '../FormattedAnswer/FormattedAnswer';
import { VerdictTag } from '../VerdictTag/VerdictTag';

import './ProblemSubmissionCard.css';

export function ProblemSubmissionCard({
  name,
  alias,
  itemJids,
  submissionsByItemJid,
  itemTypesMap,
  canViewGrading,
  canManage,
  onRegrade,
}) {
  const renderSingleRow = (itemJid, index) => {
    const submission = submissionsByItemJid[itemJid];
    if (submission) {
      return (
        <tr key={itemJid}>
          <td className="col-item-num">{index + 1}</td>
          <td>
            <FormattedAnswer answer={submission.answer} type={itemTypesMap[itemJid]} />
          </td>
          {canViewGrading && (
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
          {canViewGrading && <td className="col-verdict">-</td>}
          <td className="col-time">-</td>
        </tr>
      );
    }
  };

  const renderTotalScore = () => {
    let totalScore = 0;
    Object.keys(submissionsByItemJid).forEach(itemJid => {
      totalScore += submissionsByItemJid[itemJid].grading ? submissionsByItemJid[itemJid].grading.score || 0 : 0;
    });

    return (
      <>
        Score: <strong>{totalScore}</strong> points.
      </>
    );
  };

  return (
    <ContentCard className="bundle-problem-submission">
      <div className="card-header">
        <h4>
          {alias ? `${alias} . ` : ''}
          {name}
        </h4>
        {canManage && onRegrade && (
          <Button intent="primary" icon="refresh" onClick={onRegrade}>
            Regrade
          </Button>
        )}
      </div>
      <p>{canManage && renderTotalScore()}</p>
      <HTMLTable striped className="table-list-condensed submission-table">
        <thead>
          <tr>
            <th className="col-item-num">No.</th>
            <th>Answer</th>
            {canViewGrading && <th className="col-verdict">Verdict</th>}
            <th className="col-time">Time</th>
          </tr>
        </thead>
        <tbody>{itemJids.map(renderSingleRow)}</tbody>
      </HTMLTable>
    </ContentCard>
  );
}
