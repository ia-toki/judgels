import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ContestContestantProblemCard } from '../ContestContestantProblemCard/ContestContestantProblemCard';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestContestantProblem,
  ContestContestantProblemsResponse,
  ContestProblemStatus,
} from '../../../../../../../../../../modules/api/uriel/contestProblem';
import { selectContest } from '../../../../../modules/contestSelectors';
import { AppState } from '../../../../../../../../../../modules/store';
import { contestProblemActions as injectedContestProblemActions } from '../modules/contestProblemActions';

export interface ContestProblemsPageProps {
  contest: Contest;
  onFetchMyProblems: (contestJid: string) => Promise<ContestContestantProblemsResponse>;
}

interface ContestProblemsPageState {
  contestantProblems?: ContestContestantProblem[];
  problemNamesMap?: { [problemJid: string]: string };
}

export class ContestProblemsPage extends React.PureComponent<ContestProblemsPageProps, ContestProblemsPageState> {
  state: ContestProblemsPageState = {};

  async componentDidMount() {
    const { data, problemNamesMap } = await this.props.onFetchMyProblems(this.props.contest.jid);
    this.setState({
      contestantProblems: data,
      problemNamesMap,
    });
  }

  render() {
    return (
      <ContentCard>
        <h3>Problems</h3>
        <hr />
        {this.renderContestantProblems()}
      </ContentCard>
    );
  }

  private renderContestantProblems = () => {
    const { contestantProblems, problemNamesMap } = this.state;
    if (!contestantProblems || !problemNamesMap) {
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
        problemName={this.state.problemNamesMap![contestantProblem.problem.problemJid]}
      />
    ));
  };
}

export function createContestProblemsPage(contestProblemActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onFetchMyProblems: contestProblemActions.fetchMyList,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestProblemsPage));
}

export default createContestProblemsPage(injectedContestProblemActions);
