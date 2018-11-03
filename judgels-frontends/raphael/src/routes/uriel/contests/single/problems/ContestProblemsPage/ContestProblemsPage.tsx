import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from 'components/StatementLanguageWidget/StatementLanguageWidget';
import { Contest } from 'modules/api/uriel/contest';
import {
  ContestContestantProblem,
  ContestContestantProblemsResponse,
  ContestProblemStatus,
} from 'modules/api/uriel/contestProblem';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { consolidateLanguages } from 'modules/api/sandalphon/language';
import { getProblemName } from 'modules/api/sandalphon/problem';
import { AppState } from 'modules/store';

import { ContestContestantProblemCard } from '../ContestContestantProblemCard/ContestContestantProblemCard';
import { selectContest } from '../../../modules/contestSelectors';
import { contestProblemActions as injectedContestProblemActions } from '../modules/contestProblemActions';

export interface ContestProblemsPageProps {
  contest: Contest;
  statementLanguage: string;
  onGetMyProblems: (contestJid: string) => Promise<ContestContestantProblemsResponse>;
}

interface ContestProblemsPageState {
  response?: ContestContestantProblemsResponse;
  defaultLanguage?: string;
  uniqueLanguages?: string[];
}

export class ContestProblemsPage extends React.PureComponent<ContestProblemsPageProps, ContestProblemsPageState> {
  state: ContestProblemsPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetMyProblems(this.props.contest.jid);
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
      response.problemsMap,
      this.props.statementLanguage
    );

    this.setState({
      response,
      defaultLanguage,
      uniqueLanguages,
    });
  }

  async componentDidUpdate(prevProps: ContestProblemsPageProps, prevState: ContestProblemsPageState) {
    const { response } = this.state;
    if (this.props.statementLanguage !== prevProps.statementLanguage && response) {
      const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
        response.problemsMap,
        this.props.statementLanguage
      );

      this.setState({
        defaultLanguage,
        uniqueLanguages,
      });
    }
  }

  render() {
    return (
      <ContentCard>
        <h3>Problems</h3>
        <hr />
        {this.renderStatementLanguageWidget()}
        {this.renderProblems()}
      </ContentCard>
    );
  }

  private renderStatementLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = this.state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props: StatementLanguageWidgetProps = {
      defaultLanguage,
      statementLanguages: uniqueLanguages,
    };
    return <StatementLanguageWidget {...props} />;
  };

  private renderProblems = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: problems } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return (
      <div>
        {this.renderOpenProblems(problems.filter(p => p.problem.status === ContestProblemStatus.Open))}
        {this.renderClosedProblems(problems.filter(p => p.problem.status === ContestProblemStatus.Closed))}
      </div>
    );
  };

  private renderOpenProblems = (contestantProblems: ContestContestantProblem[]) => {
    return <div>{this.renderFilteredProblems(contestantProblems)}</div>;
  };

  private renderClosedProblems = (contestantProblems: ContestContestantProblem[]) => {
    return (
      <div>
        {contestantProblems.length !== 0 && <hr />}
        {this.renderFilteredProblems(contestantProblems)}
      </div>
    );
  };

  private renderFilteredProblems = (contestantProblems: ContestContestantProblem[]) => {
    return contestantProblems.map(contestantProblem => (
      <ContestContestantProblemCard
        key={contestantProblem.problem.problemJid}
        contest={this.props.contest}
        contestantProblem={contestantProblem}
        problemName={getProblemName(
          this.state.response!.problemsMap[contestantProblem.problem.problemJid],
          this.state.defaultLanguage!
        )}
      />
    ));
  };
}

export function createContestProblemsPage(contestProblemActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetMyProblems: contestProblemActions.getMyProblems,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemsPage));
}

export default createContestProblemsPage(injectedContestProblemActions);
