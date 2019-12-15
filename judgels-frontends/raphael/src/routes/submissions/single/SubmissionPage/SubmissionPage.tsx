import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { SubmissionDetails } from '../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { AppState } from '../../../../modules/store';
import { selectStatementLanguage } from '../../../../modules/webPrefs/webPrefsSelectors';
import {
  SubmissionWithSource,
  SubmissionWithSourceResponse,
} from '../../../../modules/api/sandalphon/submissionProgramming';
import { Profile } from '../../../../modules/api/jophiel/profile';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../modules/breadcrumbs/breadcrumbsActions';
import { submissionActions as injectedSubmissionActions } from '../../modules/submissionActions';

export interface SubmissionPageProps extends RouteComponentProps<{ submissionId: string }> {
  statementLanguage: string;
  onGetSubmissionWithSource: (submissionId: number, language?: string) => Promise<SubmissionWithSourceResponse>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface SubmissionPageState {
  submissionWithSource?: SubmissionWithSource;
  profile?: Profile;
  problemName?: string;
  problemAlias?: string;
  containerName?: string;
}

export class SubmissionPage extends React.Component<SubmissionPageProps, SubmissionPageState> {
  state: SubmissionPageState = {};

  async componentDidMount() {
    const { data, profile, problemName, problemAlias, containerName } = await this.props.onGetSubmissionWithSource(
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

  private renderSubmission = () => {
    const { submissionWithSource, profile, problemAlias, problemName, containerName } = this.state;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile}
        problemAlias={problemAlias}
        problemName={problemName}
        problemUrl={'#'}
        containerTitle="Archive"
        containerName={containerName}
      />
    );
  };
}

function createSubmissionPage(submissionActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) => ({
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSubmissionWithSource: submissionActions.getSubmissionWithSource,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SubmissionPage));
}

export default createSubmissionPage(injectedSubmissionActions, injectedBreadcrumbsActions);
