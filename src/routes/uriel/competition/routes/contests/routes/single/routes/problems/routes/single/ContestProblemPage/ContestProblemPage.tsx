import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../../../../components/ContentCard/ContentCard';
import { AppState } from '../../../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../../../modules/contestSelectors';
import { ProblemWorksheet } from '../../../../../../../../../../../../modules/api/sandalphon/problem';
import { Contest } from '../../../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestProblem,
  ContestContestantProblemWorksheet,
} from '../../../../../../../../../../../../modules/api/uriel/contestProblem';
import { contestProblemActions as injectedContestProblemActions } from '../../../modules/contestProblemActions';
import { ProblemWorksheetCard } from '../../../../../../../../../../../../components/ProblemWorksheetCard/ProblemWorksheetCard';

export interface ContestProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  contest: Contest;
  onFetchProblemWorksheet: (contestJid: string, problemAlias: string) => Promise<ContestContestantProblemWorksheet>;
}

interface ContestProblemPageState {
  problem?: ContestProblem;
  totalSubmissions?: number;
  worksheet?: ProblemWorksheet;
}

export class ContestProblemPage extends React.Component<ContestProblemPageProps, ContestProblemPageState> {
  state: ContestProblemPageState = {};

  async componentDidMount() {
    const { problem, totalSubmissions, worksheet } = await this.props.onFetchProblemWorksheet(
      this.props.contest.jid,
      this.props.match.params.problemAlias
    );
    this.setState({ problem, totalSubmissions, worksheet });
  }

  render() {
    return <ContentCard>{this.renderStatement()}</ContentCard>;
  }

  private renderStatement = () => {
    const { problem, worksheet } = this.state;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    return <ProblemWorksheetCard alias={problem.alias} worksheet={worksheet} />;
  };
}

export function createContestProblemPage(contestProblemActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onFetchProblemWorksheet: contestProblemActions.fetchWorksheet,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemPage));
}

export default createContestProblemPage(injectedContestProblemActions);
