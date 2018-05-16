import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../../../../components/ContentCard/ContentCard';
import { AppState } from '../../../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../../../modules/contestSelectors';
import { ProblemStatement } from '../../../../../../../../../../../../modules/api/sandalphon/problem';
import { Contest } from '../../../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestProblem,
  ContestContestantProblemStatement,
} from '../../../../../../../../../../../../modules/api/uriel/contestProblem';
import { contestProblemActions as injectedContestProblemActions } from '../../../modules/contestProblemActions';
import { ProblemStatementDetails } from '../../../../../../../../../../../../components/ProblemStatementDetails/ProblemStatementDetails';

export interface ContestProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  contest: Contest;
  onFetchProblemStatement: (contestJid: string, problemAlias: string) => Promise<ContestContestantProblemStatement>;
}

interface ContestProblemPageState {
  problem?: ContestProblem;
  totalSubmissions?: number;
  statement?: ProblemStatement;
}

export class ContestProblemPage extends React.Component<ContestProblemPageProps, ContestProblemPageState> {
  state: ContestProblemPageState = {};

  async componentDidMount() {
    const { problem, totalSubmissions, statement } = await this.props.onFetchProblemStatement(
      this.props.contest.jid,
      this.props.match.params.problemAlias
    );
    this.setState({ problem, totalSubmissions, statement });
  }

  render() {
    return <ContentCard>{this.renderStatement()}</ContentCard>;
  }

  private renderStatement = () => {
    const { problem, statement } = this.state;
    if (!problem || !statement) {
      return <LoadingState />;
    }

    return <ProblemStatementDetails alias={problem.alias} statement={statement} />;
  };
}

export function createContestProblemPage(contestProblemActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onFetchProblemStatement: contestProblemActions.fetchStatement,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemPage));
}

export default createContestProblemPage(injectedContestProblemActions);
