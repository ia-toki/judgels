import * as React from 'react';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';
import { AppState } from 'modules/store';
import { withRouter, RouteComponentProps } from 'react-router';
import { connect } from 'react-redux';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';
import { Contest } from 'modules/api/uriel/contest';
import { ContestAnswerResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { ProblemSubmissionCard, ProblemSubmissionCardProps } from '../ProblemSubmissionsCard/ProblemSubmissionCard';
import { Intent } from '@blueprintjs/core';
import { ButtonLink } from 'components/ButtonLink/ButtonLink';

import './SubmissionSummaryPage.css';

interface SubmissionSummaryPageRoute {
  userJid: string;
}

export interface SubmissionSummaryPageProps extends RouteComponentProps<SubmissionSummaryPageRoute> {
  contest: Contest;
  onGetSummary: (contestJid: string, userJid?: string) => Promise<ContestAnswerResponse>;
}

export interface SubmissionSummaryPageState {
  problemSummaries: ProblemSubmissionCardProps[];
}

class SubmissionSummaryPage extends React.Component<SubmissionSummaryPageProps, SubmissionSummaryPageState> {
  state: SubmissionSummaryPageState = {
    problemSummaries: [],
  };

  async componentDidMount() {
    const { contest, onGetSummary } = this.props;
    const response = await onGetSummary(contest.jid, this.props.match.params.userJid);

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
    const { contest } = this.props;
    return (
      <div className="submisions-summary-page">
        <ButtonLink
          className="goto-submissions-button"
          intent={Intent.PRIMARY}
          to={`/contests/${contest.slug}/submissions/`}
        >
          Submissions
        </ButtonLink>
        {this.state.problemSummaries.map(props => <ProblemSubmissionCard key={props.alias} {...props} />)}
      </div>
    );
  }
}

export function createSubmissionSummaryPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetSummary: contestSubmissionActions.getSummary,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(SubmissionSummaryPage));
}

export default createSubmissionSummaryPage(injectedContestSubmissionActions);
