import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { SubmissionDetails } from 'components/SubmissionDetails/Programming/SubmissionDetails';
import { AppState } from 'modules/store';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { Contest } from 'modules/api/uriel/contest';
import { SubmissionWithSource, SubmissionWithSourceResponse } from 'modules/api/sandalphon/submissionProgramming';
import { Profile } from 'modules/api/jophiel/profile';
import { breadcrumbsActions as injectedBreadcrumbsActions } from 'modules/breadcrumbs/breadcrumbsActions';

import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../../modules/contestSubmissionActions';

export interface ContestSubmissionPageProps extends RouteComponentProps<{ submissionId: string }> {
  contest: Contest;
  statementLanguage: string;
  onGetSubmissionWithSource: (
    contestJid: string,
    submissionId: number,
    language?: string
  ) => Promise<SubmissionWithSourceResponse>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface ContestSubmissionPageState {
  submissionWithSource?: SubmissionWithSource;
  profile?: Profile;
  problemName?: string;
  problemAlias?: string;
  containerName?: string;
}

export class ContestSubmissionPage extends React.Component<ContestSubmissionPageProps, ContestSubmissionPageState> {
  state: ContestSubmissionPageState = {};

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

  private renderSubmission = () => {
    const { submissionWithSource, profile, problemName, problemAlias, containerName } = this.state;
    const { contest } = this.props;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile!}
        problemName={problemName!}
        problemAlias={problemAlias!}
        problemUrl={`/contests/${contest.slug}/problems/${problemAlias}`}
        containerTitle="Contest"
        containerName={containerName!}
      />
    );
  };
}

function createContestSubmissionPage(contestProgrammingSubmissionActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSubmissionWithSource: contestProgrammingSubmissionActions.getSubmissionWithSource,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionPage));
}

export default createContestSubmissionPage(injectedContestSubmissionActions, injectedBreadcrumbsActions);
