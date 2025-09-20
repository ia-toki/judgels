import { Button, HTMLTable } from '@blueprintjs/core';
import { Refresh } from '@blueprintjs/icons';

import { ContentCard } from '../../../ContentCard/ContentCard';
import { FormattedRelative } from '../../../FormattedRelative/FormattedRelative';
import { FormattedAnswer } from '../FormattedAnswer/FormattedAnswer';
import { VerdictTag } from '../VerdictTag/VerdictTag';

import './SubmissionDetails.scss';

export function SubmissionDetails({
  name,
  alias,
  showTitle = true,
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
            <>
              <td className="col-fit">
                {submission.grading ? <VerdictTag verdict={submission.grading.verdict} /> : '-'}
              </td>
              <td className="col-fit">{submission.grading ? submission.grading.score : '-'}</td>
            </>
          )}
        </tr>
      );
    } else {
      return (
        <tr key={itemJid}>
          <td className="col-item-num">{index + 1}</td>
          <td>-</td>
          {canViewGrading && (
            <>
              <td className="col-fit">-</td>
              <td className="col-fit">-</td>
            </>
          )}
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
    <ContentCard className="bundle-submission-details">
      <div className="card-header">
        {showTitle && (
          <h4>
            {alias ? `${alias} . ` : ''}
            {name}
          </h4>
        )}
        {canManage && onRegrade && (
          <Button intent="primary" icon={<Refresh />} onClick={onRegrade}>
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
            {canViewGrading && (
              <>
                <th className="col-fit">Verdict</th>
                <th className="col-fit">Score</th>
              </>
            )}
          </tr>
        </thead>
        <tbody>{itemJids.map(renderSingleRow)}</tbody>
      </HTMLTable>
    </ContentCard>
  );
}
