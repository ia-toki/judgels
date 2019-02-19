import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from 'components/StatementLanguageWidget/StatementLanguageWidget';
import { ProblemWorksheetCard } from 'components/ProblemWorksheetCard/ProblemWorksheetCard';
import { ProblemSubmissionFormData } from 'components/ProblemWorksheetCard/ProblemSubmissionForm/ProblemSubmissionForm';
import { AppState } from 'modules/store';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheet } from 'modules/api/sandalphon/problem';
import { Contest } from 'modules/api/uriel/contest';
import { ContestProblem, ContestProblemWorksheet } from 'modules/api/uriel/contestProblem';

import { selectContest } from '../../../../modules/contestSelectors';
import { contestProblemActions as injectedContestProblemActions } from '../../modules/contestProblemActions';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../../../submissions/modules/contestSubmissionActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from 'modules/breadcrumbs/breadcrumbsActions';

import './ContestProblemPage.css';

export interface ContestProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  contest: Contest;
  statementLanguage: string;
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
    const { defaultLanguage, languages, problem, totalSubmissions, worksheet } = await this.props.onGetProblemWorksheet(
      this.props.contest.jid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );
    this.props.onPushBreadcrumb(this.props.match.url, 'Problem ' + problem.alias);
    this.setState({ defaultLanguage, languages, problem, totalSubmissions, worksheet });
  }

  async componentDidUpdate(prevProps: ContestProblemPageProps, prevState: ContestProblemPageState) {
    if (this.props.statementLanguage !== prevProps.statementLanguage && prevState.worksheet) {
      this.setState({ worksheet: undefined });
    } else if (!this.state.worksheet && prevState.worksheet) {
      await this.componentDidMount();
    }
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

  private onCreateSubmission = async (data: ProblemSubmissionFormData) => {
    const problem = this.state.problem!;
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
      <div className="contest-problem-page__widget">
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
    if (problem.submissionsLimit !== undefined) {
      const submissionsLeft = problem.submissionsLimit - totalSubmissions!;
      submissionWarning = '' + submissionsLeft + ' submissions left.';
    }

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        worksheet={worksheet}
        onSubmit={this.onCreateSubmission}
        submissionWarning={submissionWarning}
      />
    );
  };
}

export function createContestProblemPage(contestProblemActions, contestSubmissionActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetProblemWorksheet: contestProblemActions.getProblemWorksheet,
    onCreateSubmission: contestSubmissionActions.createSubmission,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemPage));
}

export default createContestProblemPage(
  injectedContestProblemActions,
  injectedContestSubmissionActions,
  injectedBreadcrumbsActions
);
