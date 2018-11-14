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
import { ContestContestantProblem, ContestContestantProblemWorksheet } from 'modules/api/uriel/contestProblem';

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
  ) => Promise<ContestContestantProblemWorksheet>;
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
  contestantProblem?: ContestContestantProblem;
  worksheet?: ProblemWorksheet;
}

export class ContestProblemPage extends React.Component<ContestProblemPageProps, ContestProblemPageState> {
  state: ContestProblemPageState = {};

  async componentDidMount() {
    const { defaultLanguage, languages, contestantProblem, worksheet } = await this.props.onGetProblemWorksheet(
      this.props.contest.jid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );
    this.props.onPushBreadcrumb(this.props.match.url, 'Problem ' + contestantProblem.problem.alias);
    this.setState({ defaultLanguage, languages, contestantProblem, worksheet });
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
    const { problem } = this.state.contestantProblem!;
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
    const { contestantProblem, worksheet } = this.state;
    if (!contestantProblem || !worksheet) {
      return <LoadingState />;
    }

    let submissionWarning;
    if (contestantProblem.problem.submissionsLimit !== 0) {
      const submissionsLeft = contestantProblem.problem.submissionsLimit - contestantProblem.totalSubmissions;
      submissionWarning = '' + submissionsLeft + ' submissions left.';
    }

    return (
      <ProblemWorksheetCard
        alias={contestantProblem.problem.alias}
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
