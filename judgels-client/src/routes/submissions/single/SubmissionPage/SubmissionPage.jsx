import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { constructProblemUrl } from '../../../../modules/api/jerahmeel/submission';
import { selectStatementLanguage } from '../../../../modules/webPrefs/webPrefsSelectors';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as submissionActions from '../../modules/submissionActions';

export class SubmissionPage extends Component {
  state = {
    submissionWithSource: undefined,
    profile: undefined,
    problemName: undefined,
    problemAlias: undefined,
    containerPath: undefined,
    containerName: undefined,
    sourceImageUrl: undefined,
  };

  async componentDidMount() {
    const { data, profile, problemName, problemAlias, containerPath, containerName } =
      await this.props.onGetSubmissionWithSource(+this.props.match.params.submissionId, this.props.statementLanguage);
    const sourceImageUrl = data.source ? undefined : await this.props.onGetSubmissionSourceImage(data.submission.jid);
    this.props.onPushBreadcrumb(this.props.match.url, 'Submission #' + data.submission.id);
    this.setState({
      submissionWithSource: data,
      profile,
      problemName,
      problemAlias,
      containerPath,
      containerName,
      sourceImageUrl,
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
    const { submissionWithSource, profile, problemAlias, problemName, containerPath, containerName, sourceImageUrl } =
      this.state;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        sourceImageUrl={sourceImageUrl}
        profile={profile}
        problemName={problemName}
        problemAlias={problemAlias}
        problemUrl={`${constructProblemUrl(containerPath, problemAlias)}`}
        containerName={containerName}
      />
    );
  };
}

const mapStateToProps = state => ({
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetSubmissionWithSource: submissionActions.getSubmissionWithSource,
  onGetSubmissionSourceImage: submissionActions.getSubmissionSourceImage,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SubmissionPage));
