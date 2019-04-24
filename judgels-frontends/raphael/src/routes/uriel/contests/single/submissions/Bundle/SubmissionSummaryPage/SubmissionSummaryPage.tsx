import { AppState } from 'modules/store';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, RouteComponentProps } from 'react-router';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { UserRef } from 'components/UserRef/UserRef';
import { Profile } from 'modules/api/jophiel/profile';
import { Contest } from 'modules/api/uriel/contest';
import { ContestantAnswerSummaryResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { ContestSubmissionConfig } from 'modules/api/uriel/contestSubmission';
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
  onRegradeAll: (contestJid: string, userJid?: string, problemJid?: string) => Promise<void>;
}

export interface SubmissionSummaryPageState {
  config?: ContestSubmissionConfig;
  profile?: Profile;
  problemSummaries: ProblemSubmissionCardProps[];
}

class SubmissionSummaryPage extends React.Component<SubmissionSummaryPageProps, SubmissionSummaryPageState> {
  state: SubmissionSummaryPageState = {
    config: undefined,
    profile: undefined,
    problemSummaries: [],
  };

  async refreshSubmissions() {
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
      onRegrade: () => this.onRegrade(problemJid),
    }));

    this.setState({ config: response.config, profile: response.profile, problemSummaries });
  }

  async componentDidMount() {
    await this.refreshSubmissions();
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
        {this.state.problemSummaries.map(props => (
          <ProblemSubmissionCard key={props.alias} onRegrade={this.onRegrade} {...props} />
        ))}
      </ContentCard>
    );
  }

  private onRegrade = async problemJid => {
    const { userJids } = this.state.config!;
    const userJid = userJids[0];

    await this.props.onRegradeAll(this.props.contest.jid, userJid, problemJid);
    await this.refreshSubmissions();
  };
}

export function createSubmissionSummaryPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    language: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSummary: contestSubmissionActions.getSummary,
    onRegradeAll: contestSubmissionActions.regradeSubmissions,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(SubmissionSummaryPage));
}

export default createSubmissionSummaryPage(injectedContestSubmissionActions);
