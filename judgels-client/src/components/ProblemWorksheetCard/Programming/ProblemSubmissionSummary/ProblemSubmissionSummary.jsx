import { ProgressBar } from '@blueprintjs/core';
import { Tag } from '@blueprintjs/core';

import { getGradingLanguageSyntaxHighlighterValue } from '../../../../modules/api/gabriel/language';
import { VerdictCode } from '../../../../modules/api/gabriel/verdict';
import { ButtonLink } from '../../../ButtonLink/ButtonLink';
import { ContentCard } from '../../../ContentCard/ContentCard';
import SourceCode from '../../../SourceCode/SourceCode';
import { VerdictTag } from '../../../VerdictTag/VerdictTag';

import './ProblemSubmissionSummary.scss';

export function ProblemSubmissionSummary({ submissionId, submission, submissionUrl }) {
  const renderScore = grading => {
    const verdict = grading.verdict;

    let score;

    if (verdict.code === VerdictCode.PND || verdict.code === VerdictCode.CE || verdict.code === VerdictCode.ERR) {
      score = null;
    } else if (verdict.code === VerdictCode.AC) {
      score = grading.score !== 100 ? grading.score : null;
    } else {
      score = grading.score !== 0 ? grading.score : null;
    }

    if (score === null) {
      return null;
    }

    return <Tag>{score}</Tag>;
  };

  const renderDetails = () => {
    const { latestGrading, gradingLanguage } = submission;
    const verdictCode = latestGrading.verdict.code;

    if (verdictCode === VerdictCode.CE) {
      const { compilationOutputs } = latestGrading.details;
      if (compilationOutputs) {
        return Object.keys(compilationOutputs).map(key => (
          <SourceCode showLineNumbers={false} language={getGradingLanguageSyntaxHighlighterValue(gradingLanguage)}>
            {compilationOutputs[key].trim()}
          </SourceCode>
        ));
      }
    }

    return null;
  };

  const renderSubmission = () => {
    if (!submission) {
      return null;
    }

    const { latestGrading } = submission;
    const verdictCode = latestGrading.verdict.code || VerdictCode.PND;
    if (verdictCode === VerdictCode.PND) {
      return (
        <>
          <p>Grading submission, please wait...</p>
          <ProgressBar className="pending-loader" />
        </>
      );
    }

    return (
      <>
        <div className="problem-submission-summary__result">
          <p>Verdict</p>
          {renderScore(latestGrading)}
          <ButtonLink to={submissionUrl} className="details-button" small>
            Details
          </ButtonLink>
        </div>
        <VerdictTag square verdictCode={verdictCode} />
        {renderDetails()}
      </>
    );
  };

  if (!submissionId) {
    return null;
  }

  return <ContentCard className="problem-submission-summary">{renderSubmission()}</ContentCard>;
}
