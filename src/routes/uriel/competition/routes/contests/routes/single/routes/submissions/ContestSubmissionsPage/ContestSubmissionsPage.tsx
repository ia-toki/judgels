import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { UsersMap } from '../../../../../../../../../../modules/api/jophiel/user';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import { ContestSubmissionsResponse } from '../../../../../../../../../../modules/api/uriel/contestSubmission';
import { Page } from '../../../../../../../../../../modules/api/pagination';
import { Submission } from '../../../../../../../../../../modules/api/sandalphon/submission';
import { ContestSubmissionsTable } from '../ContestSubmissionsTable/ContestSubmissionsTable';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';
import Pagination from '../../../../../../../../../../components/Pagination/Pagination';

export interface ContestSubmissionsPageProps {
  contest: Contest;
  onFetchMySubmissions: (contestJid: string, page: number) => Promise<ContestSubmissionsResponse>;
}

interface ContestSubmissionsPageState {
  submissions?: Page<Submission>;
  usersMap?: UsersMap;
  problemAliasesMap?: { [problemJid: string]: string };
}

export class ContestSubmissionsPage extends React.Component<ContestSubmissionsPageProps, ContestSubmissionsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestSubmissionsPageState = {};

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
    const { submissions, usersMap, problemAliasesMap } = this.state;
    if (!submissions || !usersMap || !problemAliasesMap) {
      return <LoadingState />;
    }

    if (submissions.totalData === 0) {
      return (
        <p>
          <small>
            <em>No submissions.</em>
          </small>
        </p>
      );
    }

    return <ContestSubmissionsTable submissions={submissions.data} problemAliasesMap={problemAliasesMap} />;
  };

  private renderPagination = () => {
    return <Pagination currentPage={1} pageSize={ContestSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  private onChangePage = async (nextPage: number) => {
    const { data, usersMap, problemAliasesMap } = await this.props.onFetchMySubmissions(
      this.props.contest.jid,
      nextPage
    );
    this.setState({
      submissions: data,
      usersMap,
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
    onFetchMySubmissions: contestSubmissionActions.fetchMyList,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
}

export default createContestSubmissionsPage(injectedContestSubmissionActions);
