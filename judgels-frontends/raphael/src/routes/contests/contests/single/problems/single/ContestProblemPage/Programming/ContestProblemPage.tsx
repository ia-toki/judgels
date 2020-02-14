import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { AppState } from '../../../../../../../../modules/store';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import { ContestProblem } from '../../../../../../../../modules/api/uriel/contestProblem';
import { ContestProblemWorksheet } from '../../../../../../../../modules/api/uriel/contestProblemProgramming';
import { ProblemSubmissionFormData } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { ProblemWorksheet } from '../../../../../../../../modules/api/sandalphon/problemProgramming';
import { contestProblemActions as injectedContestProblemActions } from '../../../modules/contestProblemActions';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../../../../submissions/Programming/modules/contestSubmissionActions';
import { webPrefsActions as injectedWebPrefsActions } from '../../../../../../../../modules/webPrefs/webPrefsActions';
import { selectGradingLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { selectContest } from '../../../../../modules/contestSelectors';

import './ContestProblemPage.css';

export interface ContestProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  contest: Contest;
  statementLanguage: string;
  gradingLanguage: string;
  onGetProblemWorksheet: (
    contestJid: string,
    problemAlias: string,
    language?: string
  ) => Promise<ContestProblemWorksheet>;
  onCreateSubmission: (
    contestJid: string,
    contestSlug: string,
    problemJid: string,
    data: ProblemSubmissionFormData
  ) => Promise<void>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
  onUpdateGradingLanguage: (language: string) => void;
}

interface ContestProblemPageState {
  defaultLanguage?: string;
  languages?: string[];
  problem?: ContestProblem;
  totalSubmissions?: number;
  worksheet?: ProblemWorksheet;
}

export class ContestProblemPage extends React.Component<ContestProblemPageProps, ContestProblemPageState> {
  state: ContestProblemPageState = {};

  async componentDidMount() {
    await this.fetchProblemWorksheet();
    this.props.onPushBreadcrumb(this.props.match.url, 'Problem ' + this.state.problem.alias);
  }

  async componentDidUpdate(prevProps: ContestProblemPageProps, prevState: ContestProblemPageState) {
    if (this.props.statementLanguage !== prevProps.statementLanguage && prevState.worksheet) {
      this.setState({ worksheet: undefined });
    } else if (!this.state.worksheet && prevState.worksheet) {
      await this.fetchProblemWorksheet();
    }
  }

  async fetchProblemWorksheet() {
    const { defaultLanguage, languages, problem, totalSubmissions, worksheet } = await this.props.onGetProblemWorksheet(
      this.props.contest.jid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );

    this.setState({
      defaultLanguage,
      languages,
      problem,
      totalSubmissions,
      worksheet,
    });
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return (
      <ContentCard>
        {this.renderStatementLanguageWidget()}
        {this.renderStatement()}
      </ContentCard>
    );
  }

  private createSubmission = async (data: ProblemSubmissionFormData) => {
    const problem = this.state.problem!;
    this.props.onUpdateGradingLanguage(data.gradingLanguage);
    return await this.props.onCreateSubmission(
      this.props.contest.jid,
      this.props.contest.slug,
      problem.problemJid,
      data
    );
  };

  private renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = this.state;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props: StatementLanguageWidgetProps = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="contest-programming-problem-page__widget">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  private renderStatement = () => {
    const { problem, totalSubmissions, worksheet } = this.state;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    let submissionWarning;
    if (!!problem.submissionsLimit) {
      const submissionsLeft = problem.submissionsLimit - totalSubmissions!;
      submissionWarning = '' + submissionsLeft + ' submissions left.';
    }

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        worksheet={worksheet as ProblemWorksheet}
        onSubmit={this.createSubmission}
        submissionWarning={submissionWarning}
        gradingLanguage={this.props.gradingLanguage}
      />
    );
  };
}

export function createContestProblemPage(
  contestProblemActions,
  contestSubmissionActions,
  breadcrumbsActions,
  webPrefsActions
) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
    gradingLanguage: selectGradingLanguage(state),
  });

  const mapDispatchToProps = {
    onGetProblemWorksheet: contestProblemActions.getProgrammingProblemWorksheet,
    onCreateSubmission: contestSubmissionActions.createSubmission,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
    onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemPage));
}

export default createContestProblemPage(
  injectedContestProblemActions,
  injectedContestSubmissionActions,
  injectedBreadcrumbsActions,
  injectedWebPrefsActions
);
