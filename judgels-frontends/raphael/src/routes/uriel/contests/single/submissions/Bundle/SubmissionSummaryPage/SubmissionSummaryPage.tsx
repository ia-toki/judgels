import { AppState } from 'modules/store';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, RouteComponentProps } from 'react-router';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { UserRef } from 'components/UserRef/UserRef';
import { Profile } from 'modules/api/jophiel/profile';
import { Contest } from 'modules/api/uriel/contest';
import { ContestantAnswerSummaryResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';

import { ProblemSubmissionCard, ProblemSubmissionCardProps } from '../ProblemSubmissionsCard/ProblemSubmissionCard';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';

import './SubmissionSummaryPage.css';

interface SubmissionSummaryPageRoute {
  username?: string;
}

export interface SubmissionSummaryPageProps extends RouteComponentProps<SubmissionSummaryPageRoute> {
  contest: Contest;
  language?: string;
  onGetSummary: (contestJid: string, username?: string, language?: string) => Promise<ContestantAnswerSummaryResponse>;
}

export interface SubmissionSummaryPageState {
  profile?: Profile;
  problemSummaries: ProblemSubmissionCardProps[];
}

class SubmissionSummaryPage extends React.Component<SubmissionSummaryPageProps, SubmissionSummaryPageState> {
  state: SubmissionSummaryPageState = {
    profile: undefined,
    problemSummaries: [],
  };

  async componentDidMount() {
    const { contest, onGetSummary } = this.props;
    const response = await onGetSummary(contest.jid, this.props.match.params.username, this.props.language);

    const problemSummaries: ProblemSubmissionCardProps[] = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canSupervise: response.config.canSupervise,
      canManage: response.config.canManage,
      itemTypesMap: response.itemTypesMap,
    }));

    this.setState({ profile: response.profile, problemSummaries });
  }

  render() {
    if (!this.state.profile) {
      return null;
    }
    return (
      <ContentCard className="submisions-summary-page">
        <h3>Submissions</h3>
        <hr />
        <ContentCard>
          Summary for <UserRef profile={this.state.profile!} />
        </ContentCard>
        {this.state.problemSummaries.map(props => <ProblemSubmissionCard key={props.alias} {...props} />)}
      </ContentCard>
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
