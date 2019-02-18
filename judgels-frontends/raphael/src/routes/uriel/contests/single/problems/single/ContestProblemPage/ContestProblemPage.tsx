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
import { ProblemType } from 'modules/api/sandalphon/problem';
import { Contest } from 'modules/api/uriel/contest';
import { ContestProblem } from 'modules/api/uriel/contestProblem';
import { ContestProblemWorksheet as ContestBundleProblemWorksheet } from 'modules/api/uriel/contestProblemBundle';
import { ContestProblemWorksheet as ContestProgrammingProblemWorksheet } from 'modules/api/uriel/contestProblemProgramming';
import { ProblemSubmissionFormData as ProgrammingProblemSubmissionFormData } from 'components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { ProblemWorksheet as ProgrammingProblemWorksheet } from 'modules/api/sandalphon/problemProgramming';
import { ProblemWorksheet as BundleProblemWorksheet } from 'modules/api/sandalphon/problemBundle';
import { selectContest } from '../../../../modules/contestSelectors';
import { contestProblemActions as injectedContestProblemActions } from '../../modules/contestProblemActions';
import { contestProgrammingSubmissionActions as injectedContestProgrammingSubmissionActions } from '../../../submissions/modules/contestProgrammingSubmissionActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from 'modules/breadcrumbs/breadcrumbsActions';
import { ProblemWorksheetCard as ProgrammingProblemWorksheetCard } from 'components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';

import './ContestProblemPage.css';

export interface ContestProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  contest: Contest;
  statementLanguage: string;
  onGetProblemType: (contestJid: string, problemAlias: string) => Promise<ProblemType>;
  onGetProgrammingProblemWorksheet: (
    contestJid: string,
    problemAlias: string,
    language?: string
  ) => Promise<ContestProgrammingProblemWorksheet>;
  onGetBundleProblemWorksheet: (
    contestJid: string,
    problemAlias: string,
    language?: string
  ) => Promise<ContestBundleProblemWorksheet>;
  onCreateSubmission: (
    contestJid: string,
    contestSlug: string,
    problemJid: string,
    data: ProgrammingProblemSubmissionFormData
  ) => Promise<void>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface ContestProblemPageState {
  defaultLanguage?: string;
  languages?: string[];
  problem?: ContestProblem;
  totalSubmissions?: number;
  worksheet?: ProgrammingProblemWorksheet | BundleProblemWorksheet;
  problemType?: ProblemType;
}

export class ContestProblemPage extends React.Component<ContestProblemPageProps, ContestProblemPageState> {
  state: ContestProblemPageState = {};

  async componentDidMount() {
    const problemType = await this.props.onGetProblemType(this.props.contest.jid, this.props.match.params.problemAlias);
    const { defaultLanguage, languages, problem, totalSubmissions, worksheet } =
      problemType === ProblemType.Programming
        ? await this.props.onGetProgrammingProblemWorksheet(
            this.props.contest.jid,
            this.props.match.params.problemAlias,
            this.props.statementLanguage
          )
        : await this.props.onGetBundleProblemWorksheet(
            this.props.contest.jid,
            this.props.match.params.problemAlias,
            this.props.statementLanguage
          );
    this.setState({ problemType, defaultLanguage, languages, problem, totalSubmissions, worksheet });
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

  private onCreateSubmission = async (data: ProgrammingProblemSubmissionFormData) => {
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
    const { problemType, problem, totalSubmissions, worksheet } = this.state;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    let submissionWarning;
    if (problem.submissionsLimit !== 0) {
      const submissionsLeft = problem.submissionsLimit - totalSubmissions!;
      submissionWarning = '' + submissionsLeft + ' submissions left.';
    }

    if (problemType === ProblemType.Programming) {
      return (
        <ProgrammingProblemWorksheetCard
          alias={problem.alias}
          worksheet={worksheet as ProgrammingProblemWorksheet}
          onSubmit={this.onCreateSubmission}
          submissionWarning={submissionWarning}
        />
      );
    } else {
      return <React.Fragment />;
    }
  };
}

export function createContestProblemPage(
  contestProblemActions,
  contestProgrammingSubmissionActions,
  breadcrumbsActions
) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetBundleProblemWorksheet: contestProblemActions.getBundleProblemWorksheet,
    onGetProgrammingProblemWorksheet: contestProblemActions.getProgrammingProblemWorksheet,
    onCreateSubmission: contestProgrammingSubmissionActions.createSubmission,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemPage));
}

export default createContestProblemPage(
  injectedContestProblemActions,
  injectedContestProgrammingSubmissionActions,
  injectedBreadcrumbsActions
);
