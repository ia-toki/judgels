import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { selectStatementLanguage } from '../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../../modules/contestSelectors';

import { ProblemSubmissionCard } from '../../../../../../../components/SubmissionDetails/Bundle/ProblemSubmissionsCard/ProblemSubmissionCard';
import * as contestSubmissionActions from '../modules/contestSubmissionActions';

class SubmissionSummaryPage extends React.Component {
  state = {
    config: undefined,
    profile: undefined,
    problemSummaries: [],
  };

  async refreshSubmissions() {
    const { contest, onGetSubmissionSummary } = this.props;
    const response = await onGetSubmissionSummary(contest.jid, this.props.match.params.username, this.props.language);

    const problemSummaries = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canViewGrading: response.config.canManage,
      canManage: response.config.canManage,
      itemTypesMap: response.itemTypesMap,
      onRegrade: () => this.regrade(problemJid),
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
          Summary for <UserRef profile={this.state.profile} />
        </ContentCard>
        {this.state.problemSummaries.map(props => (
          <ProblemSubmissionCard key={props.alias} {...props} />
        ))}
      </ContentCard>
    );
  }

  regrade = async problemJid => {
    const { userJids } = this.state.config;
    const userJid = userJids[0];

    await this.props.onRegradeAll(this.props.contest.jid, userJid, problemJid);
    await this.refreshSubmissions();
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  language: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetSubmissionSummary: contestSubmissionActions.getSubmissionSummary,
  onRegradeAll: contestSubmissionActions.regradeSubmissions,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SubmissionSummaryPage));
