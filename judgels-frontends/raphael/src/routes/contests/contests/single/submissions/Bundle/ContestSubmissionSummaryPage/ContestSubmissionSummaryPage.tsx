import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, RouteComponentProps } from 'react-router';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { AppState } from '../../../../../../../modules/store';
import { Profile } from '../../../../../../../modules/api/jophiel/profile';
import { Contest } from '../../../../../../../modules/api/uriel/contest';
import { ContestSubmissionSummaryResponse } from '../../../../../../../modules/api/uriel/contestSubmissionBundle';
import { ContestSubmissionConfig } from '../../../../../../../modules/api/uriel/contestSubmission';
import { selectStatementLanguage } from '../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../../modules/contestSelectors';

import {
  ProblemSubmissionCard,
  ProblemSubmissionCardProps,
} from '../../../../../../../components/SubmissionDetails/Bundle/ProblemSubmissionsCard/ProblemSubmissionCard';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';

interface ContestSubmissionSummaryPageRoute {
  username?: string;
}

export interface ContestSubmissionSummaryPageProps extends RouteComponentProps<ContestSubmissionSummaryPageRoute> {
  contest: Contest;
  language?: string;
  onGetSubmissionSummary: (
    contestJid: string,
    username?: string,
    language?: string
  ) => Promise<ContestSubmissionSummaryResponse>;
  onRegradeAll: (contestJid: string, userJid?: string, problemJid?: string) => Promise<void>;
}

export interface ContestSubmissionSummaryPageState {
  config?: ContestSubmissionConfig;
  profile?: Profile;
  problemSummaries: ProblemSubmissionCardProps[];
}

class SubmissionSummaryPage extends React.Component<
  ContestSubmissionSummaryPageProps,
  ContestSubmissionSummaryPageState
> {
  state: ContestSubmissionSummaryPageState = {
    config: undefined,
    profile: undefined,
    problemSummaries: [],
  };

  async refreshSubmissions() {
    const { contest, onGetSubmissionSummary } = this.props;
    const response = await onGetSubmissionSummary(contest.jid, this.props.match.params.username, this.props.language);

    const problemSummaries: ProblemSubmissionCardProps[] = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
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
      <ContentCard className="contest-submision-summary-page">
        <h3>Submissions</h3>
        <hr />
        <ContentCard>
          Summary for <UserRef profile={this.state.profile!} />
        </ContentCard>
        {this.state.problemSummaries.map(props => (
          <ProblemSubmissionCard key={props.alias} {...props} />
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

export function createContestSubmissionSummaryPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    language: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSubmissionSummary: contestSubmissionActions.getSubmissionSummary,
    onRegradeAll: contestSubmissionActions.regradeSubmissions,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SubmissionSummaryPage));
}

export default createContestSubmissionSummaryPage(injectedContestSubmissionActions);
