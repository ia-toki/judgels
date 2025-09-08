import { Button, HTMLTable, ProgressBar } from '@blueprintjs/core';
import { Download, Lock } from '@blueprintjs/icons';
import { Link } from 'react-router-dom';

import { isInteractive, isOutputOnly } from '../../../modules/api/gabriel/engine';
import {
  getGradingLanguageName,
  getGradingLanguageSyntaxHighlighterValue,
} from '../../../modules/api/gabriel/language.js';
import { DEFAULT_SOURCE_KEY } from '../../../modules/api/gabriel/submission';
import { VerdictCode } from '../../../modules/api/gabriel/verdict';
import { constructProblemName } from '../../../modules/api/sandalphon/problem';
import { decodeBase64 } from '../../../utils/base64';
import { ContentCard } from '../../ContentCard/ContentCard';
import { FormattedDate } from '../../FormattedDate/FormattedDate';
import { GradingVerdictTag } from '../../GradingVerdictTag/GradingVerdictTag';
import SourceCode from '../../SourceCode/SourceCode';
import { UserRef } from '../../UserRef/UserRef';
import { VerdictTag } from '../../VerdictTag/VerdictTag';

import './SubmissionDetails.scss';

export function SubmissionDetails({
  submission: { gradingEngine, gradingLanguage, time, latestGrading },
  source,
  sourceImageUrl,
  profile,
  problemName,
  problemAlias,
  problemUrl,
  containerName,
  onDownload,
  hideSource,
  hideSourceFilename,
  onClickViewSource,
  showLoaderWhenPending,
}) {
  const hasSubtasks = latestGrading && latestGrading.details && latestGrading.details.subtaskResults.length > 1;

  const renderDetails = () => {
    const grading = latestGrading;
    if (!grading || !grading.details) {
      return null;
    }

    if (grading.details.errorMessage) {
      return (
        <>
          <h4>Grading Error</h4>
          <ContentCard>
            <h5>Message</h5>
            <pre>{grading.details.errorMessage}</pre>
          </ContentCard>
        </>
      );
    }

    return (
      <div className="details-results">
        {renderSampleTestDataResults()}
        {renderSubtaskResults()}
        {renderTestDataResults()}
      </div>
    );
  };

  const renderGeneralInfo = () => {
    const separator = <>&nbsp;&bull;&nbsp;</>;

    const grading = latestGrading;

    return (
      <div className="general-info">
        {(containerName || problemName) && (
          <h4>
            {containerName && <>{containerName} / </>}
            {problemName && !!problemUrl ? (
              <Link to={problemUrl}>{constructProblemName(problemName, problemAlias)}</Link>
            ) : (
              constructProblemName(problemName, problemAlias)
            )}
          </h4>
        )}
        <p>
          {grading && <GradingVerdictTag grading={grading} />}
          {profile && (
            <>
              {' '}
              {separator} <UserRef profile={profile} />
            </>
          )}
          {!isOutputOnly(gradingEngine) && (
            <>
              {' '}
              {separator} {getGradingLanguageName(gradingLanguage)}
            </>
          )}{' '}
          {separator}{' '}
          <span className="general-info__time">
            <FormattedDate value={time} showSeconds />
          </span>
        </p>
      </div>
    );
  };

  const renderScore = score => {
    let formattedScore = score;

    if (score.startsWith('*')) {
      formattedScore = '✓' + score.substring(1);
    } else if (score.startsWith('X')) {
      formattedScore = '✕' + score.substring(1);
    }

    if (formattedScore.includes(' [')) {
      const [points, feedback] = formattedScore.split(' [', 2);
      return (
        <>
          {points}{' '}
          <span style={{ display: 'inline-block' }}>
            {'['}
            {feedback}
          </span>
        </>
      );
    }

    return formattedScore;
  };

  const renderSubtaskScore = score => {
    if (Number.isInteger(score)) {
      return <span className="subtask-score">{score}</span>;
    }

    const integer = Math.trunc(score);
    const decimal = (score - integer).toString().replace('0.', '.');
    return (
      <>
        <span className="subtask-score">{integer}</span>
        <span className="subtask-score-decimal">{decimal}</span>
      </>
    );
  };

  const renderSubtaskResults = () => {
    if (!hasSubtasks) {
      return null;
    }

    return latestGrading.details.subtaskResults.map(subtaskResult => (
      <ContentCard className="details-card">
        <details>
          <summary>
            <h5>
              <span className="subtask-name">Subtask {subtaskResult.id}</span>
              <span className="subtask-verdict">
                <VerdictTag verdictCode={subtaskResult.verdict.code} />
              </span>
              {renderSubtaskScore(subtaskResult.score)}
            </h5>
          </summary>

          <div className="details-content">
            <hr />
            <HTMLTable striped>
              <thead>
                <tr>
                  <th className="col-id">ID</th>
                  <th className="col-verdict">Verdict</th>
                  <th className="col-tc-info">Time</th>
                  <th className="col-tc-info">Memory</th>
                  <th>Score</th>
                </tr>
              </thead>
              <tbody>
                {latestGrading.details.testDataResults.map(testGroupResult =>
                  testGroupResult.testCaseResults.map((testCaseResult, testCaseIdx) => {
                    if (testCaseResult.subtaskIds.indexOf(subtaskResult.id) < 0) {
                      return null;
                    }
                    const testCaseId = `${testGroupResult.id}_${testCaseIdx + 1}`;
                    return (
                      <tr key={testCaseId}>
                        <td>{testCaseId}</td>
                        <td>
                          <VerdictTag verdictCode={testCaseResult.verdict.code} />
                        </td>
                        <td>{renderExecutionTime(testCaseResult)}</td>
                        <td>{renderExecutionMemory(testCaseResult)}</td>
                        <td>{renderScore(testCaseResult.score)}</td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </HTMLTable>
          </div>
        </details>
      </ContentCard>
    ));
  };

  const renderLoader = () => {
    if (showLoaderWhenPending && latestGrading?.verdict.code === VerdictCode.PND) {
      return <ProgressBar className="pending-loader" />;
    }
    return null;
  };

  const renderSampleTestDataResults = () => {
    const details = latestGrading.details;
    if (details.testDataResults.length < 1) {
      return null;
    }

    if (details.testDataResults[0].testCaseResults.length < 1) {
      return null;
    }

    return (
      <ContentCard className="details-card">
        <details>
          <summary>
            <h5>
              <span className="test-data-heading">Sample Test Data Results</span>
              <span className="test-data-verdicts">
                {details.testDataResults[0].testCaseResults.map(result => (
                  <VerdictTag blank square verdictCode={result.verdict.code} />
                ))}
              </span>
            </h5>
          </summary>

          <div className="details-content">
            <HTMLTable striped>
              <thead>
                <tr>
                  <th className="col-id">ID</th>
                  <th className="col-verdict">Verdict</th>
                  <th className="col-tc-info">Time</th>
                  <th className="col-tc-info">Memory</th>
                  <th>Score</th>
                </tr>
              </thead>
              <tbody>
                {details.testDataResults[0].testCaseResults.map((result, idx) => (
                  <tr key={idx}>
                    <td>0_{idx + 1}</td>
                    <td>
                      <VerdictTag verdictCode={result.verdict.code} />
                    </td>
                    <td>{renderExecutionTime(result)}</td>
                    <td>{renderExecutionMemory(result)}</td>
                    <td>{renderScore(result.score)}</td>
                  </tr>
                ))}
              </tbody>
            </HTMLTable>
          </div>
        </details>
      </ContentCard>
    );
  };

  const renderTestDataResults = () => {
    if (hasSubtasks) {
      return null;
    }

    const details = latestGrading.details;
    if (details.testDataResults.length < 2) {
      return null;
    }

    return (
      <ContentCard className="details-card">
        <details>
          <summary>
            <h5>
              <span className="test-data-heading">Test Data Results</span>
              <span className="test-data-verdicts">
                <VerdictTag blank verdictCode={latestGrading.verdict.code} />
              </span>
            </h5>
          </summary>

          <div className="details-content">
            <HTMLTable striped>
              <thead>
                <tr>
                  <th className="col-id">ID</th>
                  <th className="col-verdict">Verdict</th>
                  <th className="col-tc-info">Time</th>
                  <th className="col-tc-info">Memory</th>
                  <th>Score</th>
                </tr>
              </thead>
              <tbody>
                {details.testDataResults.map((testGroupResult, testGroupIdx) => {
                  if (testGroupIdx === 0) {
                    return null;
                  }
                  return testGroupResult.testCaseResults.map((result, idx) => (
                    <tr key={idx}>
                      <td>{`${testGroupIdx === 1 ? '' : testGroupIdx + '_'}${idx + 1}`}</td>
                      <td>
                        <VerdictTag verdictCode={result.verdict.code} />
                      </td>
                      <td>{renderExecutionTime(result)}</td>
                      <td>{renderExecutionMemory(result)}</td>
                      <td>{renderScore(result.score)}</td>
                    </tr>
                  ));
                })}
              </tbody>
            </HTMLTable>
          </div>
        </details>
      </ContentCard>
    );
  };

  const renderExecutionTime = ({ executionResult, verdict }) => {
    if (!executionResult) {
      return '?';
    }
    if (verdict.code === VerdictCode.TLE) {
      if (isInteractive(gradingEngine)) {
        return 'N/A';
      }
      if (executionResult.isKilled) {
        return '> ' + executionResult.time + ' ms';
      }
    }
    return executionResult.time + ' ms';
  };

  const renderExecutionMemory = ({ executionResult }) => {
    if (!executionResult) {
      return '?';
    }
    return executionResult.memory + ' KB';
  };

  const renderSourceFiles = () => {
    if (isOutputOnly(gradingEngine)) {
      return renderSourceFilesHeading();
    }

    const grading = latestGrading;
    if (!grading) {
      return null;
    }

    if (!source) {
      if (hideSource) {
        return (
          <ContentCard>
            <Lock /> &nbsp;<small>You cannot view other's solution before solving this problem in this course.</small>
          </ContentCard>
        );
      }

      if (onClickViewSource) {
        const isLoading = source === null;
        return (
          <ContentCard>
            <Button small disabled={isLoading} loading={isLoading} onClick={onClickViewSource}>
              View source
            </Button>
          </ContentCard>
        );
      }

      return (
        <>
          {renderSourceFilesHeading()}
          <ContentCard>
            <img src={sourceImageUrl} className="submission-details-image" alt="submission" />
          </ContentCard>
        </>
      );
    }

    const { details, verdict } = grading;
    const { submissionFiles } = source;

    const sourceFiles = Object.keys(submissionFiles).map(key => (
      <ContentCard key={key}>
        {!hideSourceFilename && (
          <h5>
            {key === DEFAULT_SOURCE_KEY ? '' : key + ': '} {submissionFiles[key].name}
          </h5>
        )}
        <SourceCode language={getGradingLanguageSyntaxHighlighterValue(gradingLanguage)}>
          {decodeBase64(submissionFiles[key].content)}
        </SourceCode>
        {verdict.code === VerdictCode.CE &&
          details &&
          details.compilationOutputs &&
          details.compilationOutputs[key] !== undefined && (
            <div className="compilation-output">
              <h5>Compilation Output</h5>
              <pre>{details.compilationOutputs[key]}</pre>
            </div>
          )}
      </ContentCard>
    ));

    const defaultCompilationOutputs =
      verdict.code === VerdictCode.CE &&
      details &&
      Object.keys(details.compilationOutputs)
        .filter(key => submissionFiles[key] === undefined)
        .map(key => (
          <ContentCard>
            <h5>Compilation Output</h5>
            <pre>{details.compilationOutputs[key]}</pre>
          </ContentCard>
        ));

    return (
      <>
        {renderSourceFilesHeading()}
        {sourceFiles}
        {defaultCompilationOutputs}
      </>
    );
  };

  const renderSourceFilesHeading = () => {
    if (!onDownload) {
      return null;
    }
    return (
      <div>
        <h4 className="source-heading">Source Files</h4>
        <Button small className="source-download" icon={<Download />} onClick={onDownload}>
          Download
        </Button>
        <div className="clearfix" />
      </div>
    );
  };

  return (
    <div className="programming-submission-details">
      {renderGeneralInfo()}
      {renderLoader()}
      {renderDetails()}
      {renderSourceFiles()}
    </div>
  );
}
