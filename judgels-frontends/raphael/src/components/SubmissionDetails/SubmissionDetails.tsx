import { HTMLTable, Tag } from '@blueprintjs/core';
import * as base64 from 'base-64';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { SourceCode } from 'components/SourceCode/SourceCode';
import { FormattedDate } from 'components/FormattedDate/FormattedDate';
import { UserRef } from 'components/UserRef/UserRef';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { VerdictTag } from 'components/VerdictTag/VerdictTag';
import { constructProblemName } from 'modules/api/sandalphon/problem';
import { Submission } from 'modules/api/sandalphon/submission';
import { GradingEngineCode } from 'modules/api/gabriel/engine';
import { getGradingLanguageName, getGradingLanguageSyntaxHighlighterValue } from 'modules/api/gabriel/language';
import { TestCaseResult } from 'modules/api/gabriel/grading';
import { SubmissionSource } from 'modules/api/gabriel/submission';
import { Profile } from 'modules/api/jophiel/profile';

import './SubmissionDetails.css';

export interface SubmissionDetailsProps {
  submission: Submission;
  source: SubmissionSource;
  profile: Profile;
  problemName?: string;
  problemAlias?: string;
  problemUrl?: string;
  containerTitle: string;
  containerName: string;
}

export class SubmissionDetails extends React.PureComponent<SubmissionDetailsProps> {
  render() {
    return (
      <div className="submission-details">
        {this.renderGeneralInfo()}
        {this.renderDetails()}
        {this.renderSourceFiles()}
      </div>
    );
  }

  private renderDetails = () => {
    const { submission } = this.props;
    const grading = submission.latestGrading;
    if (!grading || !grading.details) {
      return null;
    }

    const hasSubtasks = this.hasSubtasks();

    return (
      <>
        {this.renderSubtaskResults(hasSubtasks)}
        {this.renderSampleTestDataResults(hasSubtasks)}
        {this.renderTestDataResults(hasSubtasks)}
      </>
    );
  };

  private renderGeneralInfo = () => {
    const { submission, profile, problemAlias, problemName, containerTitle, containerName, problemUrl } = this.props;
    const grading = submission.latestGrading;

    return (
      <>
        <h4>General Info</h4>
        <ContentCard>
          <HTMLTable striped className="submission-details">
            <thead>
              <tr>
                <th className="col-info">Info</th>
                <th>Value</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Author</td>
                <td>
                  <UserRef profile={profile} />
                </td>
              </tr>
              <tr>
                <td>Problem</td>
                <td>
                  {!!problemUrl ? (
                    <Link to={problemUrl}>{constructProblemName(problemName, problemAlias)}</Link>
                  ) : (
                    constructProblemName(problemName, problemAlias)
                  )}
                </td>
              </tr>
              <tr>
                <td>{containerTitle}</td>
                <td>{containerName}</td>
              </tr>
              <tr>
                <td>Language</td>
                <td>{getGradingLanguageName(submission.gradingLanguage)}</td>
              </tr>
              <tr>
                <td>Verdict</td>
                <td>{grading && <VerdictTag verdictCode={grading.verdict.code} />}</td>
              </tr>
              <tr>
                <td>Score</td>
                <td>{grading && grading.score}</td>
              </tr>
              <tr>
                <td>Time</td>
                <td>
                  <FormattedDate value={submission.time} showSeconds />
                </td>
              </tr>
            </tbody>
          </HTMLTable>
        </ContentCard>
      </>
    );
  };

  private renderSubtaskResults = (hasSubtasks: boolean) => {
    if (!hasSubtasks) {
      return null;
    }

    const results = this.props.submission.latestGrading!.details!.subtaskResults.map((result, idx) => (
      <tr key={idx}>
        <td>{idx + 1}</td>
        <td className="col-centered">
          <VerdictTag verdictCode={result.verdict.code} />
        </td>
        <td>{result.score}</td>
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

  private renderSampleTestDataResults = (hasSubtasks: boolean) => {
    const details = this.props.submission.latestGrading!.details!;
    if (details.testDataResults.length < 1) {
      return null;
    }

    const results = details.testDataResults[0].testCaseResults.map((result, idx) => (
      <tr key={idx}>
        <td>{idx + 1}</td>
        <td className="col-centered">
          <VerdictTag verdictCode={result.verdict.code} />
        </td>
        <td>{result.executionResult ? result.executionResult.time + ' ms' : '?'}</td>
        <td>{result.executionResult ? result.executionResult.memory + ' KB' : '?'}</td>
        <td>{result.score}</td>
        {hasSubtasks && <td className="col-centered">{this.renderSubtaskTags(result.subtaskIds)}</td>}
      </tr>
    ));

    return (
      <>
        <h4>Sample Test Data Results</h4>
        <ContentCard>
          <HTMLTable striped className="submission-details">
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

  private renderTestDataResults = (hasSubtasks: boolean) => {
    const details = this.props.submission.latestGrading!.details!;
    if (details.testDataResults.length < 2) {
      return null;
    }

    let groups: JSX.Element[] = [];

    for (let idx = 1; idx < details.testDataResults.length; idx++) {
      const group = details.testDataResults[idx];

      const results = group.testCaseResults.map((result, idx2) => (
        <tr key={idx2}>
          <td>{idx2 + 1}</td>
          <td className="col-centered">
            <VerdictTag verdictCode={result.verdict.code} />
          </td>
          <td>{result.executionResult ? result.executionResult.time + ' ms' : '?'}</td>
          <td>{result.executionResult ? result.executionResult.memory + ' KB' : '?'}</td>
          <td>{result.score}</td>
        </tr>
      ));

      groups = [
        ...groups,
        <ContentCard key={idx}>
          {hasSubtasks && this.renderTestGroupHeading(idx, group.testCaseResults)}
          <HTMLTable striped className="submission-details">
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

  private renderSourceFiles = () => {
    const { submission, source } = this.props;
    if (submission.gradingEngine === GradingEngineCode.OutputOnly) {
      return null;
    }

    const details = this.props.submission.latestGrading!.details;

    const sourceFiles = Object.keys(source.submissionFiles).map(key => (
      <ContentCard key={key}>
        <h5>
          {key === 'source' ? '' : key + ': '} {source.submissionFiles[key].name}
        </h5>
        <SourceCode language={getGradingLanguageSyntaxHighlighterValue(submission.gradingLanguage)}>
          {base64.decode(source.submissionFiles[key].content)}
        </SourceCode>
        {details && (
          <div className="compilation-output">
            <h5>Compilation Output</h5>
            <pre>{base64.decode(details.compilationOutputs[key])}</pre>
          </div>
        )}
      </ContentCard>
    ));

    return (
      <>
        <h4>Source Files</h4>
        {sourceFiles}
      </>
    );
  };

  private renderTestGroupHeading = (id: number, results: TestCaseResult[]) => {
    const subtaskTags = results.length !== 0 && this.renderSubtaskTags(results[0].subtaskIds);

    return (
      <>
        <h5 className="test-group__id">Test Group {id}</h5>
        <div className="test-group__subtasks-tags">
          <span className="test-group__subtasks">Subtasks</span> {subtaskTags}
        </div>
      </>
    );
  };

  private renderSubtaskTags = (subtaskIds: number[]) => {
    return (
      <span>
        {subtaskIds.filter(id => id !== 0).map(id => (
          <Tag className="subtask-tag" key={id} round>
            {id}
          </Tag>
        ))}
      </span>
    );
  };

  private hasSubtasks = () => {
    const { submission } = this.props;
    if (submission.gradingLanguage.indexOf('Subtasks') !== -1) {
      return false;
    }
    const grading = submission.latestGrading;
    if (!grading || !grading.details) {
      return false;
    }
    return grading.details.subtaskResults.length > 1;
  };
}
