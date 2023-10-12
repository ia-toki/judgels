import { HTMLTable, Tag, Button } from '@blueprintjs/core';
import { Download } from '@blueprintjs/icons';
import { Link } from 'react-router-dom';

import SourceCode from '../../SourceCode/SourceCode';
import { FormattedDate } from '../../FormattedDate/FormattedDate';
import { UserRef } from '../../UserRef/UserRef';
import { ContentCard } from '../../ContentCard/ContentCard';
import { VerdictTag } from '../../VerdictTag/VerdictTag';
import { GradingVerdictTag } from '../../GradingVerdictTag/GradingVerdictTag';
import { constructProblemName } from '../../../modules/api/sandalphon/problem';
import {
  getGradingLanguageName,
  getGradingLanguageSyntaxHighlighterValue,
} from '../../../modules/api/gabriel/language.js';
import { isInteractive, isOutputOnly } from '../../../modules/api/gabriel/engine';
import { DEFAULT_SOURCE_KEY } from '../../../modules/api/gabriel/submission';
import { VerdictCode } from '../../../modules/api/gabriel/verdict';

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
      <>
        {renderSubtaskResults()}
        {renderSampleTestDataResults()}
        {renderTestDataResults()}
      </>
    );
  };

  const renderGeneralInfo = () => {
    const separator = <>&nbsp;&#9679;&nbsp;</>;

    const grading = latestGrading;

    return (
      <div className="general-info">
        {grading && <GradingVerdictTag grading={grading} />} {separator} <UserRef profile={profile} />
        {(containerName || problemName) && (
          <>
            {' '}
            {separator} {containerName && <>{containerName} / </>}
            {problemName && !!problemUrl ? (
              <Link to={problemUrl}>{constructProblemName(problemName, problemAlias)}</Link>
            ) : (
              constructProblemName(problemName, problemAlias)
            )}
          </>
        )}{' '}
        {separator} {getGradingLanguageName(gradingLanguage)} {separator} <FormattedDate value={time} showSeconds />
      </div>
    );
  };

  const renderSubtaskResults = () => {
    if (!hasSubtasks) {
      return null;
    }

    const results = latestGrading.details.subtaskResults.map(({ verdict, score }, idx) => (
      <tr key={idx}>
        <td>{idx + 1}</td>
        <td>
          <VerdictTag verdictCode={verdict.code} />
        </td>
        <td>{score}</td>
      </tr>
    ));

    return (
      <>
        <h4>Subtask Results</h4>
        <ContentCard>
          <HTMLTable striped>
            <thead>
              <tr>
                <th className="col-id">ID</th>
                <th className="col-verdict">Verdict</th>
                <th>Score</th>
              </tr>
            </thead>
            <tbody>{results}</tbody>
          </HTMLTable>
        </ContentCard>
      </>
    );
  };

  const renderSampleTestDataResults = () => {
    const details = latestGrading.details;
    if (details.testDataResults.length < 1) {
      return null;
    }

    const results = details.testDataResults[0].testCaseResults.map((result, idx) => (
      <tr key={idx}>
        <td>{idx + 1}</td>
        <td>
          <VerdictTag verdictCode={result.verdict.code} />
        </td>
        <td>{renderExecutionTime(result)}</td>
        <td>{renderExecutionMemory(result)}</td>
        <td>{result.score}</td>
        {hasSubtasks && <td className="col-centered">{renderSubtaskTags(result.subtaskIds)}</td>}
      </tr>
    ));

    return (
      <>
        <h4>Sample Test Data Results</h4>
        <ContentCard>
          <HTMLTable striped className="programming-submission-details">
            <thead>
              <tr>
                <th className="col-id">ID</th>
                <th className="col-verdict">Verdict</th>
                <th className="col-tc-info">Time</th>
                <th className="col-tc-info">Memory</th>
                <th>Score</th>
                {hasSubtasks && <th className="col-tc-subtasks">{hasSubtasks && 'Subtasks'}</th>}
              </tr>
            </thead>
            <tbody>{results}</tbody>
          </HTMLTable>
        </ContentCard>
      </>
    );
  };

  const renderTestDataResults = () => {
    const details = latestGrading.details;
    if (details.testDataResults.length < 2) {
      return null;
    }

    let groups = [];

    for (let idx = 1; idx < details.testDataResults.length; idx++) {
      const group = details.testDataResults[idx];

      const results = group.testCaseResults.map((result, idx2) => (
        <tr key={idx2}>
          <td>{idx2 + 1}</td>
          <td>
            <VerdictTag verdictCode={result.verdict.code} />
          </td>
          <td>{renderExecutionTime(result)}</td>
          <td>{renderExecutionMemory(result)}</td>
          <td>{result.score}</td>
        </tr>
      ));

      groups = [
        ...groups,
        <ContentCard key={idx}>
          {hasSubtasks && renderTestGroupHeading(idx, group.testCaseResults)}
          <HTMLTable striped className="programming-submission-details">
            <thead>
              <tr>
                <th className="col-id">ID</th>
                <th className="col-verdict">Verdict</th>
                <th className="col-tc-info">Time</th>
                <th className="col-tc-info">Memory</th>
                <th>Score</th>
              </tr>
            </thead>
            <tbody>{results}</tbody>
          </HTMLTable>
        </ContentCard>,
      ];
    }

    return (
      <>
        <h4>Test Data Results</h4>
        {groups}
      </>
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
        <h5>
          {key === DEFAULT_SOURCE_KEY ? '' : key + ': '} {submissionFiles[key].name}
        </h5>
        <SourceCode language={getGradingLanguageSyntaxHighlighterValue(gradingLanguage)}>
          {atob(submissionFiles[key].content)}
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
      return <h4>Source Files</h4>;
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

  const renderTestGroupHeading = (id, results) => {
    const subtaskTags = results.length !== 0 && renderSubtaskTags(results[0].subtaskIds);

    return (
      <>
        <h5 className="test-group__id">Test Group {id}</h5>
        <div className="test-group__subtasks-tags">
          <span className="test-group__subtasks">Subtasks</span> {subtaskTags}
        </div>
      </>
    );
  };

  const renderSubtaskTags = subtaskIds => {
    return (
      <span>
        {subtaskIds
          .filter(id => id !== 0)
          .map(id => (
            <Tag className="subtask-tag" key={id} round>
              {id}
            </Tag>
          ))}
      </span>
    );
  };

  return (
    <div className="programming-submission-details">
      {renderGeneralInfo()}
      {renderDetails()}
      {renderSourceFiles()}
    </div>
  );
}
