import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import StatementLanguageWidget from '../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { ContestContestantProblemCard } from '../ContestContestantProblemCard/ContestContestantProblemCard';
import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import {
  ContestContestantProblem,
  ContestContestantProblemsResponse,
  ContestProblemStatus,
} from '../../../../../../../../modules/api/uriel/contestProblem';
import { selectContest } from '../../../../../modules/contestSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { AppState } from '../../../../../../../../modules/store';
import { consolidateLanguages } from '../../../../../../../../modules/api/sandalphon/language';
import { getProblemName, ProblemInfo } from '../../../../../../../../modules/api/sandalphon/problem';
import { contestProblemActions as injectedContestProblemActions } from '../modules/contestProblemActions';

export interface ContestProblemsPageProps {
  contest: Contest;
  statementLanguage: string;
  onGetMyProblems: (contestJid: string) => Promise<ContestContestantProblemsResponse>;
}

interface ContestProblemsPageState {
  contestantProblems?: ContestContestantProblem[];
  problemsMap?: { [problemJid: string]: ProblemInfo };
  defaultLanguage?: string;
  uniqueLanguages?: string[];
}

export class ContestProblemsPage extends React.PureComponent<ContestProblemsPageProps, ContestProblemsPageState> {
  state: ContestProblemsPageState = {};

  async componentDidMount() {
    const { data, problemsMap } = await this.props.onGetMyProblems(this.props.contest.jid);
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(problemsMap, this.props.statementLanguage);

    this.setState({
      contestantProblems: data,
      problemsMap,
      defaultLanguage,
      uniqueLanguages,
    });
  }

  async componentDidUpdate(prevProps: ContestProblemsPageProps, prevState: ContestProblemsPageState) {
    const { problemsMap } = this.state;
    if (this.props.statementLanguage !== prevProps.statementLanguage && problemsMap) {
      const { defaultLanguage, uniqueLanguages } = consolidateLanguages(problemsMap, this.props.statementLanguage);

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
        {this.renderContestantProblems()}
      </ContentCard>
    );
  }

  private renderStatementLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = this.state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props: any = {
      defaultLanguage,
      statementLanguages: uniqueLanguages,
    };
    return <StatementLanguageWidget {...props} />;
  };

  private renderContestantProblems = () => {
    const { contestantProblems, problemsMap } = this.state;
    if (!contestantProblems || !problemsMap) {
      return <LoadingState />;
    }

    if (contestantProblems.length === 0) {
      return (
        <p>
          <small>
            <em>No problems.</em>
          </small>
        </p>
      );
    }

    return (
      <div>
        {this.renderOpenContestantProblems(
          contestantProblems.filter(p => p.problem.status === ContestProblemStatus.Open)
        )}
        {this.renderClosedContestantProblems(
          contestantProblems.filter(p => p.problem.status === ContestProblemStatus.Closed)
        )}
      </div>
    );
  };

  private renderOpenContestantProblems = (contestantProblems: ContestContestantProblem[]) => {
    return <div>{this.renderFilteredContestantProblems(contestantProblems)}</div>;
  };

  private renderClosedContestantProblems = (contestantProblems: ContestContestantProblem[]) => {
    return (
      <div>
        {contestantProblems.length !== 0 && <hr />}
        {this.renderFilteredContestantProblems(contestantProblems)}
      </div>
    );
  };

  private renderFilteredContestantProblems = (contestantProblems: ContestContestantProblem[]) => {
    return contestantProblems.map(contestantProblem => (
      <ContestContestantProblemCard
        key={contestantProblem.problem.problemJid}
        contest={this.props.contest}
        contestantProblem={contestantProblem}
        problemName={getProblemName(
          this.state.problemsMap![contestantProblem.problem.problemJid],
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
