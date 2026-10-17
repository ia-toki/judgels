import { Button, Intent, ProgressBar, Tag } from '@blueprintjs/core';
import { Clipboard, Cross } from '@blueprintjs/icons';
import { Box, Flex } from '@blueprintjs/labs';

import { getGradingLanguageSyntaxHighlighterValue } from '../../../../modules/api/gradingLanguage';
import { VerdictCode } from '../../../../modules/api/gradingVerdict';
import { ButtonLink } from '../../../ButtonLink/ButtonLink';
import { ContentCard } from '../../../ContentCard/ContentCard';
import SourceCode from '../../../SourceCode/SourceCode';
import { VerdictTag } from '../../../VerdictTag/VerdictTag';

import './ProblemSubmissionSummary.scss';

export function ProblemSubmissionSummary({ submissionJid, submission, submissionUrl, onClose }) {
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

    return <Tag className="problem-submission-summary__score">{score}</Tag>;
  };

  const renderErrors = () => {
    const { latestGrading, gradingLanguage } = submission;
    const verdictCode = latestGrading.verdict.code;

    if (verdictCode !== VerdictCode.CE) {
      return null;
    }

    const { compilationOutputs } = latestGrading.details;
    if (compilationOutputs) {
      return Object.keys(compilationOutputs).map(key => (
        <SourceCode showLineNumbers={false} language={getGradingLanguageSyntaxHighlighterValue(gradingLanguage)}>
          {compilationOutputs[key].trim()}
        </SourceCode>
      ));
    }
    return null;
  };

  const renderTestCaseResult = (result, idx) => {
    const input = result.revealedInput && (
      <div>
        <h5>Input</h5>
        <pre>{result.revealedInput}</pre>
      </div>
    );
    const output = (
      <div>
        <h5>Your output</h5>
        <pre>{result.revealedSolutionOutput}</pre>
      </div>
    );

    return (
      <Flex flexDirection="column" gap={2} marginBottom={2} key={idx}>
        {input}
        {output}
      </Flex>
    );
  };

  const renderEvaluationResults = () => {
    const { latestGrading } = submission;
    const verdictCode = latestGrading.verdict.code;

    if (verdictCode !== VerdictCode.AC && verdictCode !== VerdictCode.WA) {
      return null;
    }

    for (const testGroupResult of latestGrading.details.testDataResults) {
      if (testGroupResult.id === -1) {
        if (testGroupResult.testCaseResults.some(r => r.hasOwnProperty('revealedSolutionOutput'))) {
          return (
            <Box position="relative">
              <Button
                className="problem-submission-summary__close-button"
                small
                intent={Intent.NONE}
                icon={<Cross />}
                onClick={onClose}
              />
              {testGroupResult.testCaseResults.map(renderTestCaseResult)}
            </Box>
          );
        }
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
          <ProgressBar />
        </>
      );
    }

    return (
      <>
        {renderEvaluationResults()}
        <Flex gap={2}>
          <h5>Verdict</h5>
          {renderScore(latestGrading)}
          <ButtonLink to={submissionUrl} className="problem-submission-summary__details-button" small>
            Details
          </ButtonLink>
        </Flex>
        <VerdictTag square verdictCode={verdictCode} />
        {renderErrors()}
      </>
    );
  };

  if (!submissionJid) {
    return null;
  }

  return <ContentCard className="problem-submission-summary">{renderSubmission()}</ContentCard>;
}
