import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { NotFoundError } from '../../../../../../../../../modules/api/error';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectProblemSet } from '../../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../../modules/problemSetProblemSelectors';

import * as breadcrumbsActions from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as toastActions from '../../../../../../../../../modules/toast/toastActions';
import * as problemSetSubmissionActions from '../../modules/problemSetSubmissionActions';

export class ProblemSubmissionPage extends Component {
  state = {
    submissionWithSource: undefined,
    sourceImageUrl: undefined,
    profile: undefined,
    problemName: undefined,
    problemAlias: undefined,
    containerName: undefined,
  };

  async componentDidMount() {
    const { data, profile, problemName, problemAlias, containerName } = await this.props.onGetSubmissionWithSource(
      +this.props.match.params.submissionId,
      this.props.statementLanguage
    );
    if (data.submission.problemJid !== this.props.problem.problemJid) {
      const error = new NotFoundError();
      toastActions.showErrorToast(error);
      throw error;
    }
    const sourceImageUrl = data.source ? undefined : await this.props.onGetSubmissionSourceImage(data.submission.jid);
    this.props.onPushBreadcrumb(this.props.match.url, '#' + data.submission.id);
    this.setState({
      submissionWithSource: data,
      sourceImageUrl,
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
    const { submissionWithSource, profile, problemName, problemAlias, containerName, sourceImageUrl } = this.state;
    const { problemSet } = this.props;

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
        problemUrl={`/problems/${problemSet.slug}/${problemAlias}`}
        containerName={containerName}
      />
    );
  };
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
  statementLanguage: selectStatementLanguage(state),
  problem: selectProblemSetProblem(state),
});

const mapDispatchToProps = {
  onGetSubmissionWithSource: problemSetSubmissionActions.getSubmissionWithSource,
  onGetSubmissionSourceImage: problemSetSubmissionActions.getSubmissionSourceImage,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemSubmissionPage));
