import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import {
  ContestProblem,
  ContestProblemData,
  ContestProblemsResponse,
  ContestProblemStatus,
} from '../../../../../../modules/api/uriel/contestProblem';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { AppState } from '../../../../../../modules/store';

import { ContestProblemEditDialog } from '../ContestProblemEditDialog/ContestProblemEditDialog';
import { ContestProblemCard, ContestProblemCardProps } from '../ContestProblemCard/ContestProblemCard';
import { selectContest } from '../../../modules/contestSelectors';
import { contestProblemActions as injectedContestProblemActions } from '../modules/contestProblemActions';

import './ContestProblemsPage.css';

export interface ContestProblemsPageProps {
  contest: Contest;
  statementLanguage: string;
  onGetProblems: (contestJid: string) => Promise<ContestProblemsResponse>;
  onSetProblems: (contestJid: string, data: ContestProblemData) => Promise<void>;
}

interface ContestProblemsPageState {
  response?: ContestProblemsResponse;
  defaultLanguage?: string;
  uniqueLanguages?: string[];
}

export class ContestProblemsPage extends React.PureComponent<ContestProblemsPageProps, ContestProblemsPageState> {
  state: ContestProblemsPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetProblems(this.props.contest.jid);
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

  async componentDidUpdate(prevProps: ContestProblemsPageProps) {
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
        <div className="contest-problems-page__header">
          {this.renderSetDialog()}
          {this.renderStatementLanguageWidget()}
          <div className="clearfix" />
        </div>
        {this.renderProblems()}
      </ContentCard>
    );
  }

  private renderSetDialog = () => {
    const { response } = this.state;
    if (!response || !response.config.canManage) {
      return null;
    }

    const problems = response.data.map(
      p =>
        ({
          alias: p.alias,
          slug: response.problemsMap[p.problemJid].slug,
          status: p.status,
          submissionsLimit: p.submissionsLimit,
          points: p.points,
        } as ContestProblemData)
    );
    return (
      <ContestProblemEditDialog contest={this.props.contest} problems={problems} onSetProblems={this.setProblems} />
    );
  };

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

    const { data: problems, totalSubmissionsMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return (
      <div>
        {this.renderOpenProblems(
          problems.filter(p => p.status === ContestProblemStatus.Open),
          totalSubmissionsMap
        )}
        {this.renderClosedProblems(
          problems.filter(p => p.status === ContestProblemStatus.Closed),
          totalSubmissionsMap
        )}
      </div>
    );
  };

  private renderOpenProblems = (problems: ContestProblem[], totalSubmissionsMap) => {
    return <div>{this.renderFilteredProblems(problems, totalSubmissionsMap)}</div>;
  };

  private renderClosedProblems = (problems: ContestProblem[], totalSubmissionsMap) => {
    return (
      <div>
        {problems.length !== 0 && <hr />}
        {this.renderFilteredProblems(problems, totalSubmissionsMap)}
      </div>
    );
  };

  private renderFilteredProblems = (problems: ContestProblem[], totalSubmissionsMap) => {
    return problems.map(problem => {
      const props: ContestProblemCardProps = {
        contest: this.props.contest,
        problem,
        problemName: getProblemName(this.state.response!.problemsMap[problem.problemJid], this.state.defaultLanguage!),
        totalSubmissions: totalSubmissionsMap[problem.problemJid],
      };
      return <ContestProblemCard key={problem.problemJid} {...props} />;
    });
  };

  private setProblems = async (contestJid, data) => {
    const response = await this.props.onSetProblems(contestJid, data);
    await this.componentDidMount();
    return response;
  };
}

export function createContestProblemsPage(contestProblemActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetProblems: contestProblemActions.getProblems,
    onSetProblems: contestProblemActions.setProblems,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemsPage));
}

export default createContestProblemsPage(injectedContestProblemActions);
