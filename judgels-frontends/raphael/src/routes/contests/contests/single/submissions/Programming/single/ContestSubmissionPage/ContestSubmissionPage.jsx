import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { SubmissionDetails } from '../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../../../modules/contestSelectors';
import * as contestSubmissionActions from '../../modules/contestSubmissionActions';
import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

export class ContestSubmissionPage extends React.Component {
  state = {
    submissionWithSource: undefined,
    profile: undefined,
    problemName: undefined,
    problemAlias: undefined,
    containerName: undefined,
  };

  async componentDidMount() {
    const { data, profile, problemName, problemAlias, containerName } = await this.props.onGetSubmissionWithSource(
      this.props.contest.jid,
      +this.props.match.params.submissionId,
      this.props.statementLanguage
    );
    this.props.onPushBreadcrumb(this.props.match.url, 'Submission #' + data.submission.id);
    this.setState({
      submissionWithSource: data,
      profile,
      problemName,
      problemAlias,
      containerName,
    });
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return (
      <ContentCard>
        <h3>Submission #{this.props.match.params.submissionId}</h3>
        <hr />
        {this.renderSubmission()}
      </ContentCard>
    );
  }

  renderSubmission = () => {
    const { submissionWithSource, profile, problemName, problemAlias, containerName } = this.state;
    const { contest } = this.props;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile}
        problemName={problemName}
        problemAlias={problemAlias}
        problemUrl={`/contests/${contest.slug}/problems/${problemAlias}`}
        containerTitle="Contest"
        containerName={containerName}
        onDownload={this.downloadSubmission}
      />
    );
  };

  downloadSubmission = () => {
    const { submissionWithSource } = this.state;
    this.props.onDownloadSubmission(submissionWithSource.submission.jid);
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetSubmissionWithSource: contestSubmissionActions.getSubmissionWithSource,
  onDownloadSubmission: contestSubmissionActions.downloadSubmission,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionPage));
