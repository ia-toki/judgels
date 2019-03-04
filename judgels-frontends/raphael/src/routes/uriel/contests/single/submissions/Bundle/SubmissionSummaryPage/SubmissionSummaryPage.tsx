import React, { Component } from 'react';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';
import { AppState } from 'modules/store';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';
import { Contest } from 'modules/api/uriel/contest';
import { ContestAnswerResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { ProblemSubmissionCard, ProblemSubmissionCardProps } from '../ProblemSubmissionsCard/ProblemSubmissionCard';

export interface SubmissionSummaryPageProps {
  contest: Contest;
  getSummary: (contestJid: string, userJid?: string) => Promise<ContestAnswerResponse>;
}

export interface SubmissionSummaryPageState {
  problemSummaries: ProblemSubmissionCardProps[];
}

class SubmissionSummaryPage extends Component<SubmissionSummaryPageProps, SubmissionSummaryPageState> {
  constructor(props: SubmissionSummaryPageProps) {
    super(props);
    this.state = {
      problemSummaries: [],
    };
  }

  async componentDidMount() {
    const { contest, getSummary } = this.props;
    const response = await getSummary(contest.jid);

    const problemSummaries: ProblemSubmissionCardProps[] = [];
    for (const problemJid of Object.keys(response.answers)) {
      problemSummaries.push({
        alias: response.problemAliasesMap[problemJid] || '-',
        submissions: response.answers[problemJid],
        canSupervise: response.config.canSupervise,
        canManage: response.config.canManage,
      });
    }
    this.setState({ problemSummaries });
  }

  render() {
    return (
      <div>{this.state.problemSummaries.map(props => <ProblemSubmissionCard key={props.alias} {...props} />)}</div>
    );
  }
}

export function createSubmissionSummaryPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    getSummary: contestSubmissionActions.getSummary,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(SubmissionSummaryPage));
}

export default createSubmissionSummaryPage(injectedContestSubmissionActions);
