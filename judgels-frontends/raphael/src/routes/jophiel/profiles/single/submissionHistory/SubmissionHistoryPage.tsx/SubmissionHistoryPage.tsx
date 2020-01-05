import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { RouteComponentProps } from 'react-router-dom';

import { Card } from '../../../../../../components/Card/Card';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { AppState } from '../../../../../../modules/store';
import { SubmissionsResponse } from '../../../../../../modules/api/jerahmeel/submissionProgramming';
import { selectUserJid, selectUsername } from '../../../../modules/profileSelectors';
import { profileActions as injectedProfileActions } from '../../modules/profileActions';
import Pagination from '../../../../../../components/Pagination/Pagination';

interface SubmissionHistoryPageProps extends RouteComponentProps<{}> {
  userJid: string;
  onGetSubmissions: (userJid: string, page: number) => Promise<SubmissionsResponse>;
}

interface SubmissionHistoryPageState {
  response?: SubmissionsResponse;
}

class SubmissionHistoryPage extends React.Component<SubmissionHistoryPageProps, SubmissionHistoryPageState> {
  state: SubmissionHistoryPageState = {};

  render() {
    return (
      <Card title="Submission history">
        {this.renderSubmissions()}
        {this.renderPagination()}
      </Card>
    );
  }

  private renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const {
      data: submissions,
      config,
      problemAliasesMap,
      problemNamesMap,
      containerNamesMap,
      containerPathsMap,
    } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <SubmissionsTable
        submissions={submissions.page}
        userJid={this.props.userJid}
        canManage={config.canManage}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
      />
    );
  };

  private renderPagination = () => {
    return <Pagination key={1} currentPage={1} pageSize={50} onChangePage={this.onChangePage} />;
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshSubmissions(nextPage);
    return data.totalCount;
  };

  private refreshSubmissions = async (page?: number) => {
    const response = await this.props.onGetSubmissions(this.props.userJid, page);
    this.setState({ response });
    return response.data;
  };
}

function createSubmissionHistoryPage(profileActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
    username: selectUsername(state),
  });
  const mapDispatchToProps = {
    onGetSubmissions: profileActions.getSubmissions,
  };

  return withRouter(connect(mapStateToProps, mapDispatchToProps)(SubmissionHistoryPage));
}

export default createSubmissionHistoryPage(injectedProfileActions);
