import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import ItemSubmissionUserFilter from '../../../../../../../../components/ItemSubmissionUserFilter/ItemSubmissionUserFilter';
import { selectMaybeUserJid } from '../../../../../../../../modules/session/sessionSelectors';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemSubmissionCard } from '../../../../../../../../components/SubmissionDetails/Bundle/ProblemSubmissionsCard/ProblemSubmissionCard';
import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

class ProblemSubmissionSummaryPage extends React.Component {
  state = {
    config: undefined,
    profile: undefined,
    problemSummaries: undefined,
  };

  async refreshSubmissions() {
    const { userJid, problemSet, problem, onGetSubmissionSummary } = this.props;
    if (!userJid) {
      this.setState({ problemSummaries: [] });
      return;
    }

    const response = await onGetSubmissionSummary(
      problemSet.jid,
      problem.problemJid,
      this.props.match.params.username,
      this.props.language
    );

    const problemSummaries = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canViewGrading: true,
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
    return (
      <ContentCard>
        <h3>Results</h3>
        <hr />
        {this.renderUserFilter()}
        {this.renderResults()}
      </ContentCard>
    );
  }

  renderUserFilter = () => {
    if (this.props.location.pathname.includes('/users/')) {
      return null;
    }
    return <ItemSubmissionUserFilter />;
  };

  renderResults = () => {
    const { problemSummaries } = this.state;
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No results.</small>;
    }
    return (
      <>
        <ContentCard>
          Summary for <UserRef profile={this.state.profile} />
        </ContentCard>
        {this.state.problemSummaries.map(props => (
          <ProblemSubmissionCard key={props.alias} {...props} />
        ))}
      </>
    );
  };

  regrade = async problemJid => {
    const { userJids } = this.state.config;
    const userJid = userJids[0];

    await this.props.onRegradeAll(this.props.problemSet.jid, userJid, problemJid);
    await this.refreshSubmissions();
  };
}

const mapStateToProps = state => ({
  userJid: selectMaybeUserJid(state),
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
  language: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetSubmissionSummary: problemSetSubmissionActions.getSubmissionSummary,
  onRegradeAll: problemSetSubmissionActions.regradeSubmissions,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemSubmissionSummaryPage));
