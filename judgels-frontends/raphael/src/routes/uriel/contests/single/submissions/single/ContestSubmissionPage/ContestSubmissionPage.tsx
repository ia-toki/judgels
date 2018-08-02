import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { SubmissionDetails } from 'components/SubmissionDetails/SubmissionDetails';
import { AppState } from 'modules/store';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { Contest } from 'modules/api/uriel/contest';
import { SubmissionWithSource, SubmissionWithSourceResponse } from 'modules/api/sandalphon/submission';
import { Profile } from 'modules/api/jophiel/profile';

import { selectContest } from '../../../../modules/contestSelectors';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../../modules/contestSubmissionActions';

export interface ContestSubmissionPageProps extends RouteComponentProps<{ submissionId: string }> {
  contest: Contest;
  statementLanguage: string;
  onGetSubmissionWithSource: (
    contestJid: string,
    submissionId: number,
    language: string
  ) => Promise<SubmissionWithSourceResponse>;
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
    this.setState({
      submissionWithSource: data,
      profile,
      problemName,
      problemAlias,
      containerName,
    });
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
        containerTitle="Contest"
        containerName={containerName!}
      />
    );
  };
}

function createContestSubmissionPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetSubmissionWithSource: contestSubmissionActions.getSubmissionWithSource,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionPage));
}

export default createContestSubmissionPage(injectedContestSubmissionActions);
