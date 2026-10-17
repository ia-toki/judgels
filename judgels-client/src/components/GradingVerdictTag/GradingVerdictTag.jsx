import { Tag } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';

import { VerdictCode } from '../../modules/api/gradingVerdict';
import { VerdictTag } from '../VerdictTag/VerdictTag';

import './GradingVerdictTag.scss';

export function GradingVerdictTag({ grading, wide }) {
  const verdict = grading.verdict;

  const getScore = () => {
    if (verdict.code === VerdictCode.PND || verdict.code === VerdictCode.CE || verdict.code === VerdictCode.ERR) {
      return null;
    } else if (verdict.code === VerdictCode.AC) {
      return grading.score !== 100 ? grading.score : null;
    } else {
      return grading.score !== 0 ? grading.score : null;
    }
  };

  const renderScore = () => {
    const score = getScore();
    if (score === null) {
      return null;
    }

    return <Tag className="grading-verdict-tag__score">{score}</Tag>;
  };

  return (
    <Box display="inline-flex" gap={2} {...(wide && { width: 100 })}>
      {verdict && <VerdictTag verdictCode={verdict.code} />}
      {renderScore()}
    </Box>
  );
}
