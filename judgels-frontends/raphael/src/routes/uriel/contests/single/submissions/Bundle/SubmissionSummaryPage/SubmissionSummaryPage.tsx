import * as React from 'react';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';
import { AppState } from 'modules/store';
import { withRouter, RouteComponentProps } from 'react-router';
import { connect } from 'react-redux';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';
import { Contest } from 'modules/api/uriel/contest';
import { ContestantAnswerSummaryResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { ProblemSubmissionCard, ProblemSubmissionCardProps } from '../ProblemSubmissionsCard/ProblemSubmissionCard';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';

import './SubmissionSummaryPage.css';

interface SubmissionSummaryPageRoute {
  userJid: string;
}

export interface SubmissionSummaryPageProps extends RouteComponentProps<SubmissionSummaryPageRoute> {
  contest: Contest;
  language?: string;
  onGetSummary: (contestJid: string, userJid?: string, language?: string) => Promise<ContestantAnswerSummaryResponse>;
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
    const response = await onGetSummary(contest.jid, this.props.match.params.userJid, this.props.language);

    const problemSummaries: ProblemSubmissionCardProps[] = [];
    for (const problemJid of Object.keys(response.itemJidsByProblemJid)) {
      problemSummaries.push({
        name: response.problemNamesMap[problemJid] || '-',
        alias: response.problemAliasesMap[problemJid] || '-',
        itemJids: response.itemJidsByProblemJid[problemJid],
        submissionsByItemJid: response.submissionsByItemJid,
        canSupervise: response.config.canSupervise,
        canManage: response.config.canManage,
      });
    }
    this.setState({ problemSummaries });
  }

  render() {
    return (
      <div className="submisions-summary-page">
        {this.state.problemSummaries.map(props => <ProblemSubmissionCard key={props.alias} {...props} />)}
      </div>
    );
  }
}

export function createSubmissionSummaryPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    language: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSummary: contestSubmissionActions.getSummary,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(SubmissionSummaryPage));
}

export default createSubmissionSummaryPage(injectedContestSubmissionActions);
