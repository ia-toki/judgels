import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../../../../components/ContentCard/ContentCard';
import { SubmissionDetails } from '../../../../../../../../../../../../components/SubmissionDetails/SubmissionDetails';
import { AppState } from '../../../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../../../modules/contestSelectors';
import { Contest } from '../../../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestSubmission,
  ContestSubmissionResponse,
} from '../../../../../../../../../../../../modules/api/uriel/contestSubmission';
import { UserInfo } from '../../../../../../../../../../../../modules/api/jophiel/user';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../../../modules/contestSubmissionActions';

export interface ContestSubmissionPageProps extends RouteComponentProps<{ submissionId: string }> {
  contest: Contest;
  onFetchSubmission: (submissionId: number) => Promise<ContestSubmissionResponse>;
}

interface ContestSubmissionPageState {
  submission?: ContestSubmission;
  user?: UserInfo;
  problemName?: string;
  problemAlias?: string;
  contestName?: string;
}

export class ContestSubmissionPage extends React.Component<ContestSubmissionPageProps, ContestSubmissionPageState> {
  state: ContestSubmissionPageState = {};

  async componentDidMount() {
    const { data, user, problemName, problemAlias, contestName } = await this.props.onFetchSubmission(
      +this.props.match.params.submissionId
    );
    this.setState({
      submission: data,
      user,
      problemName,
      problemAlias,
      contestName,
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
    const { submission, user, problemName, problemAlias, contestName } = this.state;
    if (!submission) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submission.submission}
        source={submission.source}
        user={user!}
        problemName={problemName!}
        problemAlias={problemAlias!}
        containerTitle="Contest"
        containerName={contestName!}
      />
    );
  };
}

function createContestSubmissionPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onFetchSubmission: contestSubmissionActions.fetch,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionPage));
}

export default createContestSubmissionPage(injectedContestSubmissionActions);
