import * as React from 'react';
import { connect } from 'react-redux';

import Pagination from '../../../../../../components/Pagination/Pagination';
import { Card } from '../../../../../../components/Card/Card';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { AppState } from '../../../../../../modules/store';
import { SubmissionsResponse } from '../../../../../../modules/api/jerahmeel/submissionProgramming';
import { selectUserJid } from '../../../../modules/profileSelectors';
import { selectMaybeUserJid } from '../../../../../../modules/session/sessionSelectors';
import * as profileActions from '../../modules/profileActions';

interface SubmissionHistoryPageProps {
  userJid: string;
  sessionUserJid?: string;
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
        userJid={this.props.sessionUserJid}
        canManage={config.canManage}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
      />
    );
  };

  private renderPagination = () => {
    return <Pagination key={1} currentPage={1} pageSize={20} onChangePage={this.onChangePage} />;
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

const mapStateToProps = (state: AppState) => ({
  userJid: selectUserJid(state),
  sessionUserJid: selectMaybeUserJid(state),
});
const mapDispatchToProps = {
  onGetSubmissions: profileActions.getSubmissions,
};

export default connect(mapStateToProps, mapDispatchToProps)(SubmissionHistoryPage);
