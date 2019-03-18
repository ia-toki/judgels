import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from 'components/StatementLanguageWidget/StatementLanguageWidget';
import { AppState } from 'modules/store';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { Contest } from 'modules/api/uriel/contest';
import { ContestProblem } from 'modules/api/uriel/contestProblem';
import { ContestProblemWorksheet } from 'modules/api/uriel/contestProblemBundle';
import { ProblemWorksheet as BundleProblemWorksheet } from 'modules/api/sandalphon/problemBundle';
import { breadcrumbsActions as injectedBreadcrumbsActions } from 'modules/breadcrumbs/breadcrumbsActions';
import { contestProblemActions as injectedContestProblemActions } from '../../../modules/contestProblemActions';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../../../../submissions/Bundle/modules/contestSubmissionActions';
import { ProblemWorksheetCard as BundleProblemWorksheetCard } from 'components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';

import './ContestProblemPage.css';

export interface ContestProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  contest: Contest;
  statementLanguage: string;
  onGetProblemWorksheet: (
    contestJid: string,
    problemAlias: string,
    language?: string
  ) => Promise<ContestProblemWorksheet>;
  onCreateSubmission: (contestJid: string, problemAlias: string, itemJid: string, answer: string) => Promise<void>;
  onGetLatestSubmission: (contestJid: string, problemAlias: string) => Promise<{ [id: string]: ItemSubmission }>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface ContestProblemPageState {
  defaultLanguage?: string;
  languages?: string[];
  problem?: ContestProblem;
  latestSubmission?: { [id: string]: ItemSubmission };
  worksheet?: BundleProblemWorksheet;
}

export class ContestProblemPage extends React.Component<ContestProblemPageProps, ContestProblemPageState> {
  state: ContestProblemPageState = {};

  async componentDidMount() {
    const { defaultLanguage, languages, problem, worksheet } = await this.props.onGetProblemWorksheet(
      this.props.contest.jid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );

    const latestSubmission = await this.props.onGetLatestSubmission(this.props.contest.jid, problem.alias);
    this.setState({
      latestSubmission,
      defaultLanguage,
      languages,
      problem,
      worksheet,
    });
    this.props.onPushBreadcrumb(this.props.match.url, 'Problem ' + problem.alias);
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

  private onCreateSubmission = async (itemJid: string, answer: string) => {
    const problem = this.state.problem!;
    return await this.props.onCreateSubmission(this.props.contest.jid, problem.alias, itemJid, answer);
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
      <div className="contest-bundle-problem-page__widget">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  private renderStatement = () => {
    const { problem, worksheet, latestSubmission } = this.state;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    if (!latestSubmission) {
      return <LoadingState />;
    }
    return (
      <BundleProblemWorksheetCard
        alias={problem.alias}
        language={this.props.statementLanguage}
        latestSubmission={latestSubmission}
        onAnswerItem={this.onCreateSubmission}
        worksheet={worksheet as BundleProblemWorksheet}
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
    onGetProblemWorksheet: contestProblemActions.getBundleProblemWorksheet,
    onCreateSubmission: contestSubmissionActions.createItemSubmission,
    onGetLatestSubmission: contestSubmissionActions.getLatestSubmissions,
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
