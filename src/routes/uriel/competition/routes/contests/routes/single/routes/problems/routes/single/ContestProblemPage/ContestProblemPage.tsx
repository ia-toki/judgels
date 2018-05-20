import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../../../../components/ContentCard/ContentCard';
import { ProblemWorksheetCard } from '../../../../../../../../../../../../components/ProblemWorksheetCard/ProblemWorksheetCard';
import { ProblemSubmissionFormData } from '../../../../../../../../../../../../components/ProblemWorksheetCard/ProblemSubmissionForm';
import { AppState } from '../../../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../../../modules/contestSelectors';
import { ProblemWorksheet } from '../../../../../../../../../../../../modules/api/sandalphon/problem';
import { Contest } from '../../../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestContestantProblem,
  ContestContestantProblemWorksheet,
} from '../../../../../../../../../../../../modules/api/uriel/contestProblem';
import { contestProblemActions as injectedContestProblemActions } from '../../../modules/contestProblemActions';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../../../../submissions/modules/contestSubmissionActions';

export interface ContestProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  contest: Contest;
  onFetchProblemWorksheet: (contestJid: string, problemAlias: string) => Promise<ContestContestantProblemWorksheet>;
  onSubmit: (contestJid: string, problemJid: string, data: ProblemSubmissionFormData) => Promise<void>;
}

interface ContestProblemPageState {
  contestantProblem?: ContestContestantProblem;
  worksheet?: ProblemWorksheet;
}

export class ContestProblemPage extends React.Component<ContestProblemPageProps, ContestProblemPageState> {
  state: ContestProblemPageState = {};

  async componentDidMount() {
    const { contestantProblem, worksheet } = await this.props.onFetchProblemWorksheet(
      this.props.contest.jid,
      this.props.match.params.problemAlias
    );
    this.setState({ contestantProblem, worksheet });
  }

  render() {
    return <ContentCard>{this.renderStatement()}</ContentCard>;
  }

  private onSubmit = async (data: ProblemSubmissionFormData) => {
    return await this.props.onSubmit(this.props.contest.jid, this.state.contestantProblem!.problem.problemJid, data);
  };

  private renderStatement = () => {
    const { contestantProblem, worksheet } = this.state;
    if (!contestantProblem || !worksheet) {
      return <LoadingState />;
    }

    return (
      <ProblemWorksheetCard alias={contestantProblem.problem.alias} worksheet={worksheet} onSubmit={this.onSubmit} />
    );
  };
}

export function createContestProblemPage(contestProblemActions, contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onFetchProblemWorksheet: contestProblemActions.fetchWorksheet,
    onSubmit: contestSubmissionActions.submit,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemPage));
}

export default createContestProblemPage(injectedContestProblemActions, injectedContestSubmissionActions);
