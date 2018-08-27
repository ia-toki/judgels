import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import Pagination from 'components/Pagination/Pagination';
import { AppState } from 'modules/store';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Contest } from 'modules/api/uriel/contest';
import { ContestSubmissionConfig, ContestSubmissionsResponse } from 'modules/api/uriel/contestSubmission';
import { Page } from 'modules/api/pagination';
import { Submission } from 'modules/api/sandalphon/submission';

import { ContestSubmissionsTable } from '../ContestSubmissionsTable/ContestSubmissionsTable';
import { selectContest } from '../../../modules/contestSelectors';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';

export interface ContestSubmissionsPageProps {
  contest: Contest;
  onGetSubmissions: (contestJid: string, page: number) => Promise<ContestSubmissionsResponse>;
  onGetSubmissionConfig: (contestJid: string) => Promise<ContestSubmissionConfig>;
}

interface ContestSubmissionsPageState {
  config?: ContestSubmissionConfig;
  submissions?: Page<Submission>;
  profilesMap?: ProfilesMap;
  problemAliasesMap?: { [problemJid: string]: string };
}

export class ContestSubmissionsPage extends React.PureComponent<
  ContestSubmissionsPageProps,
  ContestSubmissionsPageState
> {
  private static PAGE_SIZE = 20;

  state: ContestSubmissionsPageState = {};

  async componentDidMount() {
    const config = await this.props.onGetSubmissionConfig(this.props.contest.jid);
    this.setState({ config });
  }

  render() {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderSubmissions = () => {
    const { config, submissions, profilesMap, problemAliasesMap } = this.state;
    if (!config || !submissions || !profilesMap || !problemAliasesMap) {
      return <LoadingState />;
    }

    if (submissions.totalData === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ContestSubmissionsTable
        contest={this.props.contest}
        submissions={submissions.data}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        showUserColumn={config.isAllowedToViewAllSubmissions}
      />
    );
  };

  private renderPagination = () => {
    return <Pagination currentPage={1} pageSize={ContestSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  private onChangePage = async (nextPage: number) => {
    const { data, profilesMap, problemAliasesMap } = await this.props.onGetSubmissions(
      this.props.contest.jid,
      nextPage
    );
    this.setState({
      submissions: data,
      profilesMap,
      problemAliasesMap,
    });
    return data.totalData;
  };
}

function createContestSubmissionsPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetSubmissions: contestSubmissionActions.getSubmissions,
    onGetSubmissionConfig: contestSubmissionActions.getSubmissionConfig,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
}

export default createContestSubmissionsPage(injectedContestSubmissionActions);
